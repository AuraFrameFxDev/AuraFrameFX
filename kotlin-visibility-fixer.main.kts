#!/usr/bin/env kotlin

@file:DependsOn("org.jetbrains.kotlin:kotlin-compiler:1.9.20")
@file:DependsOn("com.github.ajalt.clikt:clikt:4.2.0")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicInteger
import kotlin.io.path.absolutePathString
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.streams.asSequence

class KotlinVisibilityFixer : CliktCommand() {
    private val dir by option("-d", "--directory", help = "Directory to process")
        .file(mustExist = true, canBeFile = false)
        .default(File(System.getProperty("user.dir")))
    
    private val patterns by option("-p", "--pattern", help = "File pattern to match (e.g. *.kt)")
        .multiple(default = listOf("**/*.kt"))
    
    private val dryRun by option("--dry-run", help = "Don't actually modify files, just show what would be done")
        .flag(default = false)
    
    private val verbose by option("-v", "--verbose", help = "Show verbose output")
        .flag(default = false)
    
    private val config = CompilerConfiguration().apply {
        put(
            CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
            PrintingMessageCollector(System.err, MessageRenderer.PLAIN_FULL_PATHS, false)
        )
    }
    
    private val disposable = Disposer.newDisposable()
    private val env = KotlinCoreEnvironment.createForProduction(
        disposable, config, EnvironmentConfigFiles.JVM_CONFIG_FILES
    )
    
    private val fileCount = AtomicInteger(0)
    private val fixedCount = AtomicInteger(0)
    
    override fun run() {
        echo("Scanning directory: ${dir.absolutePath}")
        
        runBlocking {
            val kotlinFiles = findKotlinFiles()
            echo("Found ${kotlinFiles.size} Kotlin files")
            
            val jobs = kotlinFiles.map { file ->
                async(Dispatchers.IO) {
                    processFile(file)
                }
            }
            
            jobs.awaitAll()
        }
        
        echo("Processed ${fileCount.get()} files, fixed ${fixedCount.get()} files")
        Disposer.dispose(disposable)
    }
    
    private fun findKotlinFiles(): List<Path> {
        return patterns.flatMap { pattern ->
            val glob = if (pattern.contains("*")) pattern else "**/$pattern"
            Files.walk(dir.toPath())
                .asSequence()
                .filter { it.isRegularFile() && it.extension == "kt" }
                .filter { path ->
                    val relativePath = dir.toPath().relativize(path).toString()
                    // Simple glob matching for ** and * patterns
                    glob.matchesGlob(relativePath)
                }
                .toList()
        }.distinct()
    }
    
    private fun String.matchesGlob(path: String): Boolean {
        // Very simple glob implementation - could be more sophisticated
        val regex = this
            .replace(".", "\\.")
            .replace("**", "__DOUBLE_STAR__")
            .replace("*", "[^/]*")
            .replace("__DOUBLE_STAR__", ".*")
        return path.matches(regex.toRegex())
    }
    
    private fun processFile(file: Path): Boolean {
        if (verbose) echo("Processing file: ${file.absolutePathString()}")
        fileCount.incrementAndGet()
        
        val content = Files.readString(file)
        val fileName = file.name
        
        val virtualFile = LightVirtualFile(fileName, KotlinFileType.INSTANCE, content)
        val psiFile = PsiManager.getInstance(env.project).findFile(virtualFile)
            ?: return false
        
        var modified = false
        val visitor = object : KtVisitorVoid() {
            override fun visitClass(klass: KtClass) {
                super.visitClass(klass)
                if (addVisibilityIfNeeded(klass)) {
                    modified = true
                }
            }
            
            override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
                super.visitObjectDeclaration(declaration)
                if (addVisibilityIfNeeded(declaration)) {
                    modified = true
                }
            }
            
            override fun visitFunction(function: KtFunction) {
                super.visitFunction(function)
                if (addVisibilityIfNeeded(function)) {
                    modified = true
                }
            }
            
            override fun visitProperty(property: KtProperty) {
                super.visitProperty(property)
                if (addVisibilityIfNeeded(property)) {
                    modified = true
                }
            }
        }
        
        psiFile.accept(visitor)
        
        if (modified) {
            val modifiedContent = applyModifications(content)
            if (!dryRun) {
                Files.writeString(file, modifiedContent)
                fixedCount.incrementAndGet()
                if (verbose) echo("Fixed visibility in: ${file.absolutePathString()}")
            } else {
                echo("Would fix visibility in: ${file.absolutePathString()}")
            }
            return true
        }
        
        return false
    }
    
    private fun addVisibilityIfNeeded(element: KtModifierListOwner): Boolean {
        val visibilityType = element.visibilityModifierType()
        
        // Skip if already has a visibility modifier
        if (visibilityType != null) return false
        
        // Skip local declarations
        if (element is KtFunction && element.isLocal) return false
        
        // Add public modifier
        val factory = KtPsiFactory(element.project)
        val modifierList = element.modifierList
        
        if (modifierList == null) {
            element.addModifier(factory.createModifier("public"))
            return true
        } else {
            modifierList.addModifier(factory.createModifier("public"))
            return true
        }
    }
    
    private fun applyModifications(content: String): String {
        // In a real implementation, we would use PSI modifications
        // Here we're using a simple approach with regex for demo purposes
        return content
            .replace(Regex("(?<![a-zA-Z])(class|interface|object|data class|enum class|sealed class|fun)(?![a-zA-Z])"), "public $1")
    }
}

fun main(args: Array<String>) = KotlinVisibilityFixer().main(args)

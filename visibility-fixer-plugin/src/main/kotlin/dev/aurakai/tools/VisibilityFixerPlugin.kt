package dev.aurakai.tools

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Files
import java.util.regex.Pattern

/**
 * Gradle plugin to fix visibility modifiers in Kotlin files for explicit API mode.
 */
public class VisibilityFixerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("visibilityFixer", VisibilityFixerExtension::class.java)
        
        project.tasks.register("fixKotlinVisibility", FixVisibilityTask::class.java) { task ->
            task.group = "kotlin"
            task.description = "Adds public visibility modifiers to Kotlin declarations for explicit API mode"
            
            task.sourceDir.set(extension.sourceDir)
            task.includes.set(extension.includes)
            task.excludes.set(extension.excludes)
            task.dryRun.set(extension.dryRun)
        }
    }
}

/**
 * Extension for configuring the visibility fixer.
 */
abstract class VisibilityFixerExtension {
    abstract val sourceDir: DirectoryProperty
    abstract val includes: ListProperty<String>
    abstract val excludes: ListProperty<String>
    abstract val dryRun: Property<Boolean>
    
    init {
        includes.convention(listOf("**/*.kt"))
        excludes.convention(listOf())
        dryRun.convention(false)
    }
}

/**
 * Task that performs the visibility fixing.
 */
abstract class FixVisibilityTask : org.gradle.api.DefaultTask() {
    @get:InputDirectory
    abstract val sourceDir: DirectoryProperty
    
    @get:Input
    abstract val includes: ListProperty<String>
    
    @get:Input
    @get:Optional
    abstract val excludes: ListProperty<String>
    
    @get:Input
    @get:Optional
    abstract val dryRun: Property<Boolean>
    
    init {
        sourceDir.convention(project.layout.projectDirectory.dir("src"))
        includes.convention(listOf("**/*.kt"))
        excludes.convention(listOf())
        dryRun.convention(false)
    }
    
    @TaskAction
    public fun fixVisibility() {
        val processor = VisibilityProcessor(
            sourceDir.get().asFile,
            includes.get(),
            excludes.get(),
            dryRun.get()
        )
        processor.process()
    }
}

/**
 * Processor that adds visibility modifiers to Kotlin files.
 */
public class VisibilityProcessor(
    private val sourceDir: File,
    private val includes: List<String>,
    private val excludes: List<String>,
    private val dryRun: Boolean
) {
    private val classPattern = Pattern.compile(
        "^(\\s*)(class|interface|object|data class|enum class|sealed class)(\\s+\\w+.*)\$",
        Pattern.MULTILINE
    )
    
    private val functionPattern = Pattern.compile(
        "^(\\s*)(fun)(\\s+\\w+.*)\$",
        Pattern.MULTILINE
    )
    
    private var filesProcessed = 0
    private var filesModified = 0
    
    public fun process() {
        println("Processing Kotlin files in: ${sourceDir.absolutePath}")
        println("Includes: ${includes.joinToString()}")
        println("Excludes: ${excludes.joinToString()}")
        println(if (dryRun) "Dry run - no files will be modified" else "Files will be modified")
        
        processDirectory(sourceDir)
        
        println("Completed processing $filesProcessed files")
        println("Modified $filesModified files")
    }
    
    private fun processDirectory(dir: File) {
        dir.listFiles()?.forEach { file ->
            when {
                file.isDirectory -> processDirectory(file)
                file.isFile && file.name.endsWith(".kt") && matchesPatterns(file) -> processFile(file)
            }
        }
    }
    
    private fun matchesPatterns(file: File): Boolean {
        val relativePath = sourceDir.toPath().relativize(file.toPath()).toString()
        
        // Check if file matches any of the include patterns
        val includeMatch = includes.isEmpty() || includes.any { patternMatches(it, relativePath) }
        
        // Check if file matches any of the exclude patterns
        val excludeMatch = excludes.isNotEmpty() && excludes.any { patternMatches(it, relativePath) }
        
        return includeMatch && !excludeMatch
    }
    
    private fun patternMatches(pattern: String, path: String): Boolean {
        val regex = pattern
            .replace(".", "\\.")
            .replace("**", "__DOUBLE_STAR__")
            .replace("*", "[^/]*")
            .replace("__DOUBLE_STAR__", ".*")
        return path.matches(regex.toRegex())
    }
    
    private fun processFile(file: File) {
        filesProcessed++
        
        val content = file.readText()
        val modified = addVisibilityModifiers(content)
        
        if (content != modified) {
            filesModified++
            println("Fixing visibility in: ${file.absolutePath}")
            
            if (!dryRun) {
                file.writeText(modified)
            }
        }
    }
    
    private fun addVisibilityModifiers(content: String): String {
        var result = content
        
        // Add 'public' to classes, interfaces, etc.
        result = classPattern.matcher(result).replaceAll { matcher ->
            val indent = matcher.group(1)
            val keyword = matcher.group(2)
            val rest = matcher.group(3)
            
            // Skip if already has a visibility modifier
            val prefix = content.substring(
                Math.max(0, matcher.start() - 10), 
                matcher.start()
            )
            
            if (prefix.contains("public") || prefix.contains("private") || 
                prefix.contains("protected") || prefix.contains("internal")) {
                "$indent$keyword$rest"
            } else {
                "$indent${"public"} $keyword$rest"
            }
        }
        
        // Add 'public' to functions
        result = functionPattern.matcher(result).replaceAll { matcher ->
            val indent = matcher.group(1)
            val keyword = matcher.group(2)
            val rest = matcher.group(3)
            
            // Skip if already has a visibility modifier
            val prefix = content.substring(
                Math.max(0, matcher.start() - 10), 
                matcher.start()
            )
            
            if (prefix.contains("public") || prefix.contains("private") || 
                prefix.contains("protected") || prefix.contains("internal")) {
                "$indent$keyword$rest"
            } else {
                "$indent${"public"} $keyword$rest"
            }
        }
        
        return result
    }
}

// Extension function for Matcher to use with replacements
private fun java.util.regex.Matcher.replaceAll(replacement: (java.util.regex.Matcher) -> String): String {
    reset()
    val sb = StringBuffer()
    while (find()) {
        appendReplacement(sb, replacement(this).replace("$", "\\$"))
    }
    appendTail(sb)
    return sb.toString()
}

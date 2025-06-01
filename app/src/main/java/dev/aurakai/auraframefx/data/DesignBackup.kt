package dev.aurakai.auraframefx.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class DesignBackup(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val backupFiles = listOf(
        "shared_prefs/design_prefs.xml",
        "shared_prefs/theme_prefs.xml",
        "shared_prefs/layout_prefs.xml",
        "shared_prefs/widget_prefs.xml"
    )
    private val backupDirectories = listOf(
        "designs",
        "themes",
        "layouts",
        "widgets"
    )

    fun backupDesigns() {
        scope.launch {
            try {
                performBackup()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun performBackup() {
        val backupDir = File(context.filesDir, "design_backup")
        if (!backupDir.exists()) {
            backupDir.mkdirs()
        }

        // Backup files
        backupFiles.forEach { path ->
            backupFile(path, backupDir)
        }

        // Backup directories
        backupDirectories.forEach { path ->
            backupDirectory(path, backupDir)
        }

        // Create backup archive
        createBackupArchive(backupDir)
    }

    private fun backupFile(relativePath: String, backupDir: File) {
        try {
            val sourceFile = File(context.filesDir, relativePath)
            if (sourceFile.exists()) {
                val destFile = File(backupDir, sourceFile.name)
                sourceFile.copyTo(destFile, overwrite = true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun backupDirectory(relativePath: String, backupDir: File) {
        try {
            val sourceDir = File(context.filesDir, relativePath)
            if (sourceDir.exists() && sourceDir.isDirectory) {
                val destDir = File(backupDir, sourceDir.name)
                if (!destDir.exists()) {
                    destDir.mkdirs()
                }
                sourceDir.listFiles()?.forEach { file ->
                    try {
                        file.copyTo(File(destDir, file.name), overwrite = true)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createBackupArchive(backupDir: File) {
        try {
            val timestamp = System.currentTimeMillis()
            val archiveFile = File(backupDir, "design_backup_$timestamp.zip")

            FileOutputStream(archiveFile).use { fos ->
                ZipOutputStream(fos).use { zos ->
                    backupDir.listFiles()?.forEach { file ->
                        if (file.isFile) {
                            FileInputStream(file).use { fis ->
                                zos.putNextEntry(ZipEntry(file.name))
                                fis.copyTo(zos)
                                zos.closeEntry()
                            }
                        }
                    }
                }
            }

            // Clean up old backups
            val maxBackups = 5
            val backups = backupDir.listFiles { file ->
                file.name.startsWith("design_backup_") && file.name.endsWith(".zip")
            }?.sortedByDescending { it.lastModified() }

            backups?.let { backupFiles ->
                if (backupFiles.size > maxBackups) {
                    backupFiles.drop(maxBackups).forEach { file ->
                        file.delete()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun restoreDesigns(backupFile: File) {
        scope.launch {
            try {
                performRestore(backupFile)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun performRestore(backupFile: File) {
        try {
            // Extract backup
            val tempDir = File(context.filesDir, "temp_restore")
            if (!tempDir.exists()) {
                tempDir.mkdirs()
            }

            // Restore files
            backupFiles.forEach { path ->
                restoreFile(path, tempDir)
            }

            // Restore directories
            backupDirectories.forEach { path ->
                restoreDirectory(path, tempDir)
            }

            // Clean up
            tempDir.deleteRecursively()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun restoreFile(relativePath: String, tempDir: File) {
        try {
            val sourceFile = File(tempDir, relativePath)
            if (sourceFile.exists()) {
                val destFile = File(context.filesDir, relativePath)
                sourceFile.copyTo(destFile, overwrite = true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun restoreDirectory(relativePath: String, tempDir: File) {
        try {
            val sourceDir = File(tempDir, relativePath)
            if (sourceDir.exists() && sourceDir.isDirectory) {
                val destDir = File(context.filesDir, relativePath)
                if (!destDir.exists()) {
                    destDir.mkdirs()
                }
                sourceDir.listFiles()?.forEach { file ->
                    try {
                        file.copyTo(File(destDir, file.name), overwrite = true)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

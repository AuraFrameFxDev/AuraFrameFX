package dev.aurakai.auraframefx.data

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class BackupService : Service() {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val backupFiles = listOf(
        "shared_prefs/secure_prefs.xml",
        "shared_prefs/xposed_status_prefs.xml",
        "shared_prefs/framework_status_prefs.xml",
        "api_tokens.txt"
    )
    private val backupDirectories = listOf(
        "overlays",
        "music",
        "data"
    )

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            try {
                performBackup()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return START_STICKY
    }

    private suspend fun performBackup() {
        val backupDir = File(filesDir, "backup")
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
            val sourceFile = File(filesDir, relativePath)
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
            val sourceDir = File(filesDir, relativePath)
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
            val archiveFile = File(backupDir, "auraframefx_backup_$timestamp.zip")

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
                file.name.startsWith("auraframefx_backup_") && file.name.endsWith(".zip")
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

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
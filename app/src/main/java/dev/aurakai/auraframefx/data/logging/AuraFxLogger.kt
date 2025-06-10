package dev.aurakai.auraframefx.data.logging

import android.util.Log
import dev.aurakai.auraframefx.ai.services.KaiAIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuraFxLogger @Inject constructor(
    private val kaiService: KaiAIService,
    // Assumption: KaiAIService will provide access to applicationContext if needed for File operations,
    // or KaiAIService's read/write methods are sufficient.
    // The user's snippet directly used kaiService.applicationContext.filesDir.
    // This requires KaiAIService to expose its applicationContext.
    // Let's make applicationContext in KaiAIService public for this to work,
    // or AuraFxLogger needs its own Context injection.
    // For now, proceeding with user's direct access pattern.
    // If KaiAIService's applicationContext is private, this needs adjustment.
    // A cleaner way: Pass context to AuraFxLogger or have KaiAIService provide methods for listing/deleting log files.
    // Given the user's snippet, I'll assume kaiService.applicationContext is accessible.
) {
    private val TAG = "AuraFxLogger" // For AuraFxLogger's own Logcat messages
    private val LOG_FILENAME_PREFIX = "aurafx_log_"
    private val LOG_DIR = "logs" // Subdirectory within app's internal filesDir
    private val LOG_RETENTION_DAYS = 7

    private val loggerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        Log.d(TAG, "AuraFxLogger initialized.")
        loggerScope.launch {
            delay(10 * 1000L) // Initial delay before first cleanup
            Log.d(TAG, "Initiating startup log cleanup.")
            cleanupOldLogs()
        }
    }

    /**
     * Returns the current log file name based on today's date in yyyyMMdd format.
     *
     * The file name is prefixed with the log filename prefix and suffixed with ".txt".
     */
    private fun getCurrentLogFileName(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
        return "$LOG_FILENAME_PREFIX${dateFormat.format(Date())}.txt"
    }

    /**
     * Writes a formatted log entry to both Android Logcat and the current day's log file.
     *
     * Formats the log entry with a timestamp, log level, and tag. Supports multi-line messages with indentation and includes the stack trace if a throwable is provided. Attempts to append the entry to a daily log file in the internal logs directory; if file writing fails, logs an error to Logcat as a fallback.
     *
     * @param level The log level (e.g., "DEBUG", "INFO", "WARN", "ERROR", "VERBOSE").
     * @param entryTag The tag associated with the log entry.
     * @param message The log message, which may be multi-line.
     * @param throwable Optional throwable to include its stack trace in the log entry.
     */
    private suspend fun writeLogEntry(
        level: String,
        entryTag: String,
        message: String,
        throwable: Throwable? = null,
    ) {
        val timestamp = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss.SSS",
            Locale.US
        ).format(Date()) // Changed format for better readability
        val logPrefix = "[$timestamp][$level/$entryTag]"
        val logEntry = if (message.lines().size > 1) {
            // Handle multi-line messages: indent subsequent lines
            message.lines().mapIndexed { index, line ->
                if (index == 0) "$logPrefix $line" else "$logPrefix   $line"
            }.joinToString("\n")
        } else {
            "$logPrefix $message"
        }

        val fullLogEntry = if (throwable != null) {
            "$logEntry\n${Log.getStackTraceString(throwable)}"
        } else {
            logEntry
        }
        val filePath = "$LOG_DIR/${getCurrentLogFileName()}"

        // Log to Logcat as well for immediate visibility during development
        when (level) {
            "DEBUG" -> Log.d(entryTag, message, throwable)
            "INFO" -> Log.i(entryTag, message, throwable)
            "WARN" -> Log.w(entryTag, message, throwable)
            "ERROR" -> Log.e(entryTag, message, throwable)
            "VERBOSE" -> Log.v(entryTag, message, throwable)
        }

        val success =
            kaiService.writeToFile(filePath, fullLogEntry + "\n", isInternal = true, append = true)
        if (!success) {
            Log.e(TAG, "Failed to write log entry to file: $filePath")
            // Fallback log to Logcat if file write fails
            Log.e(TAG, "(FILE_WRITE_FAIL) $fullLogEntry")
        }
    }

    /**
     * Asynchronously logs a debug-level message with an optional throwable.
     *
     * The log entry is written to both Android Logcat and the current day's log file.
     *
     * @param tag Tag identifying the source of the log message.
     * @param message The message to log.
     * @param throwable Optional exception to include in the log entry.
     */
    fun d(tag: String, message: String, throwable: Throwable? = null) =
        loggerScope.launch { writeLogEntry("DEBUG", tag, message, throwable) }

    /**
     * Logs an informational message asynchronously with the specified tag and optional throwable.
     *
     * The log entry is written to both Android Logcat and the current day's log file.
     *
     * @param tag The tag identifying the source of the log message.
     * @param message The informational message to log.
     * @param throwable An optional throwable whose stack trace will be included in the log entry.
     */
    fun i(tag: String, message: String, throwable: Throwable? = null) =
        loggerScope.launch { writeLogEntry("INFO", tag, message, throwable) }

    /**
     * Asynchronously logs a warning message with an optional throwable.
     *
     * The log entry is written to both Android Logcat and the internal daily log file.
     *
     * @param tag The tag identifying the source of the log message.
     * @param message The warning message to log.
     * @param throwable An optional throwable whose stack trace will be included in the log entry.
     */
    fun w(tag: String, message: String, throwable: Throwable? = null) =
        loggerScope.launch { writeLogEntry("WARN", tag, message, throwable) }

    /**
     * Asynchronously logs an error message with the specified tag and optional throwable.
     *
     * The log entry is written to both Android Logcat and the current day's log file.
     *
     * @param tag The tag identifying the source of the log message.
     * @param message The error message to log.
     * @param throwable An optional throwable whose stack trace will be included in the log entry.
     */
    fun e(tag: String, message: String, throwable: Throwable? = null) =
        loggerScope.launch { writeLogEntry("ERROR", tag, message, throwable) }

    /**
     * Logs a verbose-level message asynchronously to both Logcat and the daily log file.
     *
     * @param tag The tag identifying the source of the log message.
     * @param message The message to log.
     * @param throwable Optional throwable whose stack trace will be included in the log entry.
     */
    fun v(tag: String, message: String, throwable: Throwable? = null) =
        loggerScope.launch { writeLogEntry("VERBOSE", tag, message, throwable) }

    /**
     * Reads and returns the contents of all log files in the internal logs directory.
     *
     * @return A map where each key is a log filename and the value is its content. Only files matching the log filename prefix are included. The map is sorted with the newest files first.
     */
    suspend fun readAllLogs(): Map<String, String> = withContext(Dispatchers.IO) {
        val logs = mutableMapOf<String, String>()
        // This requires KaiAIService to expose applicationContext or provide a method to list files in a dir.
        // Assuming kaiService.applicationContext is accessible as per user's snippet.
        // If not, this part needs KaiAIService to have a listLogFiles(dir: String) method.
        val logDirFile = File(kaiService.applicationContext.filesDir, LOG_DIR)
        Log.d(TAG, "Reading all logs from directory: ${logDirFile.absolutePath}")

        if (logDirFile.exists() && logDirFile.isDirectory) {
            logDirFile.listFiles()?.sortedByDescending { it.name }
                ?.forEach { file -> // Read newest first
                    if (file.isFile && file.name.startsWith(LOG_FILENAME_PREFIX)) {
                        Log.d(TAG, "Reading log file: ${file.name}")
                        val content =
                            kaiService.readFromFile("$LOG_DIR/${file.name}", isInternal = true)
                        if (content != null) {
                            logs[file.name] = content
                        } else {
                            Log.w(TAG, "Failed to read content of log file: ${file.name}")
                        }
                    }
                }
        } else {
            Log.w(
                TAG,
                "Log directory does not exist or is not a directory: ${logDirFile.absolutePath}"
            )
        }
        return@withContext logs
    }

    /**
     * Reads and returns the contents of the current day's log file.
     *
     * @return The contents of today's log file, or an empty string if the file does not exist or cannot be read.
     */
    suspend fun readCurrentDayLogs(): String = withContext(Dispatchers.IO) {
        val fileName = getCurrentLogFileName()
        Log.d(TAG, "Reading current day logs from: $LOG_DIR/$fileName")
        return@withContext kaiService.readFromFile("$LOG_DIR/$fileName", isInternal = true) ?: ""
    }

    /**
     * Deletes log files older than the retention period from the internal logs directory.
     *
     * Scans the log directory for files matching the log filename prefix and removes those whose last modified time exceeds the configured retention period.
     */
    private suspend fun cleanupOldLogs() = withContext(Dispatchers.IO) {
        // Requires applicationContext from KaiAIService to be accessible.
        val logDirFile = File(kaiService.applicationContext.filesDir, LOG_DIR)
        Log.i(TAG, "Running cleanup for old logs in: ${logDirFile.absolutePath}")

        if (logDirFile.exists() && logDirFile.isDirectory) {
            val cutoffTime =
                System.currentTimeMillis() - (LOG_RETENTION_DAYS * 24 * 60 * 60 * 1000L)
            var filesDeleted = 0
            logDirFile.listFiles()?.forEach { file ->
                if (file.isFile && file.name.startsWith(LOG_FILENAME_PREFIX)) {
                    if (file.lastModified() < cutoffTime) {
                        if (file.delete()) {
                            filesDeleted++
                            Log.d(TAG, "Cleaned up old log file: ${file.name}")
                        } else {
                            Log.w(TAG, "Failed to delete old log file: ${file.name}")
                        }
                    }
                }
            }
            Log.i(TAG, "Log cleanup finished. Deleted $filesDeleted old log file(s).")
        } else {
            Log.w(TAG, "Log directory not found for cleanup: ${logDirFile.absolutePath}")
        }
    }

    /**
     * Shuts down the logger by cancelling all ongoing logging and maintenance coroutines.
     *
     * After calling this method, no further log entries will be processed or written.
     */
    fun shutdown() {
        Log.d(TAG, "AuraFxLogger shutting down loggerScope.")
        loggerScope.cancel()
    }
}

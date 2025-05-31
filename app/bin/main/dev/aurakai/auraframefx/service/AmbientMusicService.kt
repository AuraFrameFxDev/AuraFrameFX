package dev.aurakai.auraframefx.service

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.IBinder
import kotlinx.coroutines.*
import java.io.File
import java.util.*

class AmbientMusicService : Service() {
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var mediaPlayer: MediaPlayer? = null
    private var currentTrack: String? = null
    private val musicTracks = listOf(
        "ambient1.mp3",
        "ambient2.mp3",
        "ambient3.mp3",
        "ambient4.mp3",
        "ambient5.mp3"
    )
    private val trackHistory = mutableListOf<String>()
    private var lastTrackChangeTime: Long = 0
    private var isShuffling = true

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            scope.launch {
                try {
                    startAmbientMusic()
                } catch (e: Exception) {
                    e.printStackTrace()
                    cleanupResources()
                    throw RuntimeException("Failed to start ambient music", e)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            cleanupResources()
            throw RuntimeException("Failed to start service", e)
        }
        return START_STICKY
    }

    private suspend fun startAmbientMusic() {
        try {
            // Initialize MediaPlayer
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                isLooping = true
                volume = 0.5f // Start with 50% volume
                setOnPreparedListener {
                    it.start()
                }
                setOnErrorListener { mp, what, extra ->
                    cleanupResources()
                    false
                }
            }

            // Start playing random track
            playRandomTrack()

            // Schedule periodic track changes
            scope.launch {
                while (isActive) {
                    delay(10 * 60 * 1000) // Change track every 10 minutes
                    if (System.currentTimeMillis() - lastTrackChangeTime >= 10 * 60 * 1000) {
                        playRandomTrack()
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            cleanupResources()
            throw RuntimeException("Failed to initialize music player", e)
        }
    }

    private fun playRandomTrack() {
        try {
            val track = if (isShuffling) {
                getNonRepeatingTrack()
            } else {
                musicTracks.random()
            }

            if (track != currentTrack) {
                mediaPlayer?.stop()
                mediaPlayer?.reset()

                val trackFile = File(filesDir, "music/$track")
                if (trackFile.exists()) {
                    try {
                        mediaPlayer?.setDataSource(trackFile.absolutePath)
                        mediaPlayer?.prepareAsync()
                        currentTrack = track
                        lastTrackChangeTime = System.currentTimeMillis()
                        trackHistory.add(track)
                        if (trackHistory.size > 5) {
                            trackHistory.removeAt(0)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        cleanupResources()
                        throw RuntimeException("Failed to load track", e)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            cleanupResources()
            throw RuntimeException("Failed to play track", e)
        }
    }

    private fun getNonRepeatingTrack(): String {
        val availableTracks = musicTracks.filter { track ->
            !trackHistory.contains(track)
        }
        return if (availableTracks.isEmpty()) {
            musicTracks.random()
        } else {
            availableTracks.random()
        }
    }

    override fun onDestroy() {
        try {
            cleanupResources()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            super.onDestroy()
        }
    }

    private fun cleanupResources() {
        try {
            // Release media player
            mediaPlayer?.apply {
                stop()
                reset()
                release()
            }
            mediaPlayer = null

            // Cancel coroutine scope
            scope.cancel()

            // Clear track history
            trackHistory.clear()

            // Clear temporary files
            val tempDir = File(filesDir, "temp")
            if (tempDir.exists()) {
                tempDir.listFiles()?.forEach { file ->
                    if (file.name.startsWith("temp_")) {
                        file.delete()
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setVolume(volume: Float) {
        try {
            mediaPlayer?.volume = volume
        } catch (e: Exception) {
            e.printStackTrace()
            cleanupResources()
            throw RuntimeException("Failed to set volume", e)
        }
    }

    fun pause() {
        try {
            mediaPlayer?.pause()
        } catch (e: Exception) {
            e.printStackTrace()
            cleanupResources()
            throw RuntimeException("Failed to pause playback", e)
        }
    }

    fun resume() {
        try {
            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
            cleanupResources()
            throw RuntimeException("Failed to resume playback", e)
        }
    }

    fun setShuffling(enabled: Boolean) {
        isShuffling = enabled
        trackHistory.clear()
    }

    fun getCurrentTrack(): String? = currentTrack

    fun getTrackHistory(): List<String> = trackHistory.toList()

    fun skipToNextTrack() {
        playRandomTrack()
    }

    fun skipToPreviousTrack() {
        if (trackHistory.size > 1) {
            val previousTrack = trackHistory[trackHistory.size - 2]
            playTrack(previousTrack)
        }
    }

    private fun playTrack(track: String) {
        if (track != currentTrack) {
            mediaPlayer?.stop()
            mediaPlayer?.reset()

            val trackFile = File(filesDir, "music/$track")
            if (trackFile.exists()) {
                try {
                    mediaPlayer?.setDataSource(trackFile.absolutePath)
                    mediaPlayer?.prepareAsync()
                    currentTrack = track
                    lastTrackChangeTime = System.currentTimeMillis()
                } catch (e: Exception) {
                    e.printStackTrace()
                    cleanupResources()
                    throw RuntimeException("Failed to play track", e)
                }
            }
        }
    }
}

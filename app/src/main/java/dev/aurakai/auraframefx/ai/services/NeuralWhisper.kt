package dev.aurakai.auraframefx.ai.services

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import dev.aurakai.auraframefx.model.ConversationState
import dev.aurakai.auraframefx.model.Emotion
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.max

/**
 * NeuralWhisper class for audio processing and AI interaction.
 * Handles voice recording, emotion detection, and interaction with
 * the AI model for transcription and response generation.
 */
class NeuralWhisper(
    private val sampleRate: Int = 44100,
    private val channels: Int = AudioFormat.CHANNEL_IN_MONO,
    private val bitsPerSample: Int = AudioFormat.ENCODING_PCM_16BIT,
    private val model: GenerativeModel? = null,
) {
    private var audioRecord: AudioRecord? = null
    private var isRecording: Boolean = false
    private val audioDataList: MutableList<ShortArray> = mutableListOf()
    private val bufferSize: Int = AudioRecord.getMinBufferSize(
        sampleRate,
        channels,
        bitsPerSample
    )

    var contextSharedWithKai: Boolean = false
        private set

    private val _conversationStateFlow = MutableStateFlow<ConversationState>(ConversationState.Idle)
    val conversationState: StateFlow<ConversationState> = _conversationStateFlow

    val emotionLabels: List<String> = Emotion.values().map { it.name }

    private val _emotionStateFlow = MutableStateFlow<Emotion>(Emotion.NEUTRAL)
    val emotionState: StateFlow<Emotion> = _emotionStateFlow

    var isProcessing: Boolean = false
        private set

    // Using proper delegation syntax
    private val moodManager by lazy { MoodManager() }

    // Using proper coroutine scope
    private val scope by lazy { CoroutineScope(Dispatchers.IO + SupervisorJob()) }

    companion object {
        private const val TAG = "NeuralWhisper"
        const val DEFAULT_SAMPLE_RATE = 44100
        const val DEFAULT_BUFFER_SIZE = 4096
        private const val VOLUME_THRESHOLD = 2500 // Adjust based on testing
        private const val KEYWORD_CONFIDENCE_THRESHOLD = 0.65
    }

    data class UserPreferenceModel(
        val id: String? = null,
        val voiceActivationEnabled: Boolean = true,
        val emotionDetectionEnabled: Boolean = true,
        val preferredVoice: String = "default",
        val activationKeyword: String = "aura",
    ) {
        fun loadUserPreferences() {
            // In a real implementation, this would load from SharedPreferences or a database
            Log.d(TAG, "Loading user preferences for ID: $id")
        }

        fun saveUserPreferences() {
            // In a real implementation, this would save to SharedPreferences or a database
            Log.d(TAG, "Saving user preferences for ID: $id")
        }
    }

    /**
     * Initialize the AudioRecord for capturing audio
     */
    fun init(): Boolean {
        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channels,
                bitsPerSample,
                bufferSize
            )

            Log.d(TAG, "NeuralWhisper initialized with buffer size: $bufferSize")
            return audioRecord?.state == AudioRecord.STATE_INITIALIZED
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing AudioRecord: ${e.message}")
            return false
        }
    }

    fun release() {
        scope.cancel()
        audioRecord?.release()
        audioRecord = null
        Log.d(TAG, "NeuralWhisper resources released")
    }

    interface AudioProcessingCallback {
        fun onAudioLevelUpdate(level: Float)
        fun onSpeechDetected()
        fun onEmotionDetected(emotion: Emotion)
        fun onTranscriptionComplete(text: String)
    }

    fun startRecording(callback: AudioProcessingCallback?) {
        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            Log.e(TAG, "Cannot start recording, AudioRecord not initialized")
            return
        }

        isRecording = true
        audioDataList.clear()
        _conversationStateFlow.value = ConversationState.Listening

        audioRecord?.startRecording()

        scope.launch {
            val buffer = ShortArray(bufferSize)

            while (isRecording && isActive) {
                val readResult = audioRecord?.read(buffer, 0, bufferSize) ?: 0

                if (readResult > 0) {
                    // Make a copy of the buffer to prevent it from being overwritten
                    val bufferCopy = buffer.copyOf(readResult)
                    audioDataList.add(bufferCopy)

                    // Calculate audio level for visualization
                    val level = calculateAudioLevel(bufferCopy)
                    callback?.onAudioLevelUpdate(level)

                    // Process for speech detection
                    if (level > VOLUME_THRESHOLD / 32768f) {
                        callback?.onSpeechDetected()
                    }

                    // Process for emotion detection every 5 captures
                    if (audioDataList.size % 5 == 0) {
                        val detectedEmotion = processEmotionFromAudio(bufferCopy)
                        _emotionStateFlow.value = detectedEmotion
                        callback?.onEmotionDetected(detectedEmotion)
                    }
                }
            }

            // When recording stops, process the entire audio for transcription
            if (audioDataList.isNotEmpty()) {
                _conversationStateFlow.value = ConversationState.Processing
                isProcessing = true

                val transcription = transcribeAudio(audioDataList)
                callback?.onTranscriptionComplete(transcription)

                _conversationStateFlow.value = ConversationState.Idle
                isProcessing = false
            }
        }
    }

    fun stopRecording() {
        isRecording = false
        audioRecord?.stop()
        Log.d(TAG, "Recording stopped")
    }

    fun processAudioChunk(chunk: ShortArray) {
        if (!isRecording) return

        scope.launch {
            // Process the chunk for emotion
            val emotion = processEmotionFromAudio(chunk)
            _emotionStateFlow.value = emotion

            // Check for keyword if needed
            if (_conversationStateFlow.value == ConversationState.Idle) {
                val keyword = "aura" // Configurable
                if (detectKeyword(chunk, keyword)) {
                    _conversationStateFlow.value = ConversationState.Activated
                    Log.d(TAG, "Keyword detected: $keyword")
                }
            }
        }
    }

    /**
     * Process audio data to determine the emotional content
     */
    private fun processEmotionFromAudio(audioData: ShortArray): Emotion {
        // In a real implementation, this would use a model for emotion detection
        // This is a simplified placeholder that analyzes audio characteristics

        val energy = calculateRMSEnergy(audioData)
        val zeroCrossings = calculateZeroCrossings(audioData)

        // Simplified emotion detection based on audio characteristics
        return when {
            energy > 0.8f && zeroCrossings > 0.7f -> Emotion.EXCITED
            energy > 0.6f && zeroCrossings > 0.6f -> Emotion.HAPPY
            energy < 0.3f && zeroCrossings < 0.3f -> Emotion.SAD
            energy > 0.7f && zeroCrossings < 0.4f -> Emotion.ANGRY
            else -> Emotion.NEUTRAL
        }
    }

    /**
     * Calculate root mean square energy of audio signal
     */
    private fun calculateRMSEnergy(audioData: ShortArray): Float {
        if (audioData.isEmpty()) return 0f

        var sum = 0.0
        for (sample in audioData) {
            sum += sample.toDouble() * sample.toDouble()
        }

        val rms = Math.sqrt(sum / audioData.size)
        // Normalize to 0.0-1.0
        return (rms / Short.MAX_VALUE).toFloat()
    }

    /**
     * Calculate zero crossing rate
     */
    private fun calculateZeroCrossings(audioData: ShortArray): Float {
        if (audioData.size <= 1) return 0f

        var crossings = 0
        for (i in 1 until audioData.size) {
            if ((audioData[i] > 0 && audioData[i - 1] < 0) ||
                (audioData[i] < 0 && audioData[i - 1] > 0)
            ) {
                crossings++
            }
        }

        // Normalize to 0.0-1.0
        return crossings.toFloat() / max(1, audioData.size - 1)
    }

    /**
     * Calculate audio level for visualization
     */
    private fun calculateAudioLevel(audioData: ShortArray): Float {
        if (audioData.isEmpty()) return 0f

        var max = 0f
        for (sample in audioData) {
            val abs = abs(sample.toFloat())
            if (abs > max) max = abs
        }

        // Convert to dB scale and normalize
        val db = if (max > 0) 20 * log10(max / Short.MAX_VALUE) else -96f
        return (db + 96) / 96 // Normalize from -96dB to 0dB to range 0.0-1.0
    }

    fun getEmotion(audioData: ShortArray): String {
        val emotion = processEmotionFromAudio(audioData)
        return emotion.name.lowercase()
    }

    fun transcribeAudio(audioData: List<ShortArray>): String {
        if (audioData.isEmpty()) return ""

        _conversationStateFlow.value = ConversationState.Processing

        // Convert audio data to a format suitable for the AI model
        val byteArrayOutputStream = ByteArrayOutputStream()
        for (buffer in audioData) {
            val bytes = buffer.foldIndexed(ByteArray(buffer.size * 2)) { i, acc, value ->
                val byteBuffer = ByteBuffer.allocate(2)
                byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
                byteBuffer.putShort(value)
                val shortBytes = byteBuffer.array()
                acc[i * 2] = shortBytes[0]
                acc[i * 2 + 1] = shortBytes[1]
                acc
            }
            byteArrayOutputStream.write(bytes)
        }

        // In a real implementation, this would call the AI model for transcription
        // For now, we simulate the response based on audio characteristics

        val averageEnergy = audioData.map { calculateRMSEnergy(it) }.average().toFloat()

        // If we have a real model provided, use it
        if (model != null) {
            try {
                java.util.Base64.getEncoder()
                    .encodeToString(byteArrayOutputStream.toByteArray())

                // This is a placeholder for the actual API call
                // In a real implementation, you would use the GenerativeModel's API correctly

                // Simulate response based on audio characteristics
                val transcription = runBlocking {
                    delay(300) // Simulate processing time
                    "Hello, I'm listening. Your audio energy was ${(averageEnergy * 100).toInt()}%"
                }

                _conversationStateFlow.value = ConversationState.Idle
                return transcription
            } catch (e: Exception) {
                Log.e(TAG, "Error in transcription: ${e.message}")
                _conversationStateFlow.value = ConversationState.Error
                return "Sorry, I couldn't process that audio."
            }
        } else {
            // No model available, return a placeholder response
            _conversationStateFlow.value = ConversationState.Idle
            return when {
                averageEnergy > 0.7f -> "I heard you speaking with high energy!"
                averageEnergy > 0.4f -> "I heard you speaking with moderate energy."
                averageEnergy > 0.1f -> "I heard you speaking softly."
                else -> "I couldn't quite hear what you said."
            }
        }
    }

    fun detectKeyword(
        audioData: ShortArray,
        keyword: String,
    ): Boolean {
        if (audioData.isEmpty() || keyword.isEmpty()) return false

        // In a real implementation, this would use a wake word detection algorithm
        // For now, we use a simplified approach based on audio energy

        val energy = calculateRMSEnergy(audioData)
        val zeroCrossings = calculateZeroCrossings(audioData)

        // Pattern that might indicate speech with the right cadence for the keyword
        val possibleKeyword = energy > 0.3f &&
                energy < 0.8f &&
                zeroCrossings > 0.4f &&
                zeroCrossings < 0.7f

        // For better accuracy, we would analyze the frequency spectrum
        // and use a real wake word detection model

        return possibleKeyword && Math.random() < KEYWORD_CONFIDENCE_THRESHOLD
    }

    fun getAudioDataFlow(): Flow<ShortArray> = flow {
        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            throw IllegalStateException("AudioRecord not initialized")
        }

        val buffer = ShortArray(bufferSize)
        audioRecord?.startRecording()

        try {
            while (isRecording && currentCoroutineContext().isActive) {
                val readResult = audioRecord?.read(buffer, 0, bufferSize) ?: 0
                if (readResult > 0) {
                    emit(buffer.copyOf(readResult))
                    delay(10) // Small delay to prevent flooding
                }
            }
        } finally {
            if (audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                audioRecord?.stop()
            }
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Share context with Kai AI persona
     */
    fun shareContextWithKai(context: String): Boolean {
        // In a real implementation, this would use a communication channel
        // to the Kai persona
        Log.d(TAG, "Sharing context with Kai: $context")
        contextSharedWithKai = true
        return true
    }

    /**
     * Inner class to handle emotion/mood tracking over time
     */
    private inner class MoodManager {
        private val recentEmotions = ArrayDeque<Emotion>(20)

        fun addEmotion(emotion: Emotion) {
            if (recentEmotions.size >= 20) {
                recentEmotions.removeFirst()
            }
            recentEmotions.addLast(emotion)
        }

        fun getDominantMood(): Emotion {
            if (recentEmotions.isEmpty()) return Emotion.NEUTRAL

            val counts = recentEmotions.groupingBy { it }.eachCount()
            return counts.maxByOrNull { it.value }?.key ?: Emotion.NEUTRAL
        }

        fun getEmotionalVolatility(): Float {
            if (recentEmotions.size <= 1) return 0f

            var changes = 0
            var previous = recentEmotions.first()

            for (emotion in recentEmotions) {
                if (emotion != previous) {
                    changes++
                    previous = emotion
                }
            }

            return changes.toFloat() / max(1, recentEmotions.size - 1)
        }
    }
}

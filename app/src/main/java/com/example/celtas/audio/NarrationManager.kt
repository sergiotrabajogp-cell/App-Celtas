package com.example.celtas.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class NarrationManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentParagraphIndex = MutableStateFlow(-1)
    val currentParagraphIndex: StateFlow<Int> = _currentParagraphIndex.asStateFlow()

    private val _speechRate = MutableStateFlow(0.92f)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private var onCompletionCallback: (() -> Unit)? = null

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("NarrationManager", "Error initializing TTS", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("es", "ES"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to generic Spanish
                tts?.setLanguage(Locale("es"))
            }
            tts?.setPitch(1.05f) // Friendly storytelling tone
            tts?.setSpeechRate(_speechRate.value)
            isInitialized = true

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    onCompletionCallback?.invoke()
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }
            })
        } else {
            Log.w("NarrationManager", "TTS initialization failed status: $status")
        }
    }

    fun speak(text: String, paragraphIndex: Int = -1, onDone: (() -> Unit)? = null) {
        if (!isInitialized || tts == null) {
            // Even if TTS isn't ready on emulator, let UI progress cleanly
            _currentParagraphIndex.value = paragraphIndex
            _isSpeaking.value = true
            return
        }

        stop()
        _currentParagraphIndex.value = paragraphIndex
        _isSpeaking.value = true
        onCompletionCallback = onDone

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "celtic_story_$paragraphIndex")
        }

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "celtic_story_$paragraphIndex")
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("NarrationManager", "Error stopping TTS", e)
        }
        _isSpeaking.value = false
    }

    fun setSpeed(rate: Float) {
        _speechRate.value = rate
        tts?.setSpeechRate(rate)
    }

    fun shutdown() {
        stop()
        try {
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e("NarrationManager", "Error shutting down TTS", e)
        }
        tts = null
        isInitialized = false
    }
}

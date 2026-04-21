package com.pinyinpark.util

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * 音频播放管理器
 * 支持两种模式：
 * 1. 本地音频资源播放 (MediaPlayer)
 * 2. TTS语音合成 (TextToSpeech)
 */
class AudioPlayerManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    // 音频资源映射表（拼音字符 -> 资源名）
    private val audioResourceMap = mapOf(
        // 声母
        "b" to "audio_b", "p" to "audio_p", "m" to "audio_m", "f" to "audio_f",
        "d" to "audio_d", "t" to "audio_t", "n" to "audio_n", "l" to "audio_l",
        "g" to "audio_g", "k" to "audio_k", "h" to "audio_h",
        "j" to "audio_j", "q" to "audio_q", "x" to "audio_x",
        "zh" to "audio_zh", "ch" to "audio_ch", "sh" to "audio_sh",
        "r" to "audio_r", "z" to "audio_z", "c" to "audio_c", "s" to "audio_s",
        // 韵母
        "a" to "audio_a", "o" to "audio_o", "e" to "audio_e",
        "i" to "audio_i", "u" to "audio_u", "ü" to "audio_v",
        "ai" to "audio_ai", "ei" to "audio_ei", "ui" to "audio_ui",
        "ao" to "audio_ao", "ou" to "audio_ou", "iu" to "audio_iu",
        "ie" to "audio_ie", "üe" to "audio_ve",
        "er" to "audio_er", "an" to "audio_an", "en" to "audio_en",
        "in" to "audio_in", "un" to "audio_un", "ün" to "audio_vn",
        "ang" to "audio_ang", "eng" to "audio_eng", "ing" to "audio_ing", "ong" to "audio_ong",
        // 四声
        "1" to "audio_1", "2" to "audio_2", "3" to "audio_3", "4" to "audio_4",
        // 整体认读
        "zhi" to "audio_zhi", "chi" to "audio_chi", "shi" to "audio_shi",
        "ri" to "audio_ri", "zi" to "audio_zi", "ci" to "audio_ci", "si" to "audio_si"
    )

    init {
        initTextToSpeech()
    }

    /**
     * 初始化TTS引擎
     */
    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.CHINESE)
                isTtsReady = result != TextToSpeech.LANG_MISSING_DATA &&
                             result != TextToSpeech.LANG_NOT_SUPPORTED
            }
        }
    }

    /**
     * 播放拼音发音
     * @param pinyinChar 拼音字符（如 "b", "ba", "mā"）
     * @param exampleWord 示例汉字（如 "爸"）
     */
    fun playPinyin(pinyinChar: String, exampleWord: String? = null) {
        // 先停止当前播放
        stop()

        // 优先尝试本地资源
        val resourceName = audioResourceMap[pinyinChar]
        if (resourceName != null) {
            playLocalAudio(resourceName)
        } else {
            // 使用TTS合成
            if (isTtsReady) {
                val textToSpeak = exampleWord ?: pinyinChar
                speakWithTts(textToSpeak)
            } else {
                // 降级：使用TTS读拼音字符
                speakWithTts(pinyinChar)
            }
        }
    }

    /**
     * 播放本地音频资源
     */
    private fun playLocalAudio(resourceName: String) {
        try {
            val resourceId = context.resources.getIdentifier(
                resourceName, "raw", context.packageName
            )

            if (resourceId != 0) {
                mediaPlayer = MediaPlayer.create(context, resourceId)
                mediaPlayer?.setOnCompletionListener {
                    _isPlaying.value = false
                    releaseMediaPlayer()
                }
                mediaPlayer?.setOnErrorListener { _, _, _ ->
                    _isPlaying.value = false
                    releaseMediaPlayer()
                    // 尝试TTS降级
                    false
                }
                _isPlaying.value = true
                mediaPlayer?.start()
                return
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 资源不存在，使用TTS
        speakWithTts(resourceName)
    }

    /**
     * 使用TTS语音合成
     */
    private fun speakWithTts(text: String) {
        textToSpeech?.let { tts ->
            if (isTtsReady) {
                _isPlaying.value = true
                tts.setOnUtteranceCompletedListener {
                    _isPlaying.value = false
                }
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "pinyin_${System.currentTimeMillis()}")
            }
        }
    }

    /**
     * 播放示例词发音
     */
    fun playExampleWord(word: String) {
        stop()
        if (isTtsReady) {
            _isPlaying.value = true
            textToSpeech?.setOnUtteranceCompletedListener {
                _isPlaying.value = false
            }
            textToSpeech?.speak(word, TextToSpeech.QUEUE_FLUSH, null, "word_${System.currentTimeMillis()}")
        }
    }

    /**
     * 停止播放
     */
    fun stop() {
        // 停止TTS
        textToSpeech?.stop()

        // 停止MediaPlayer
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        _isPlaying.value = false
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    /**
     * 释放资源
     */
    fun release() {
        stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isTtsReady = false
    }

    /**
     * 检查TTS是否可用
     */
    fun isTtsAvailable(): Boolean = isTtsReady

    /**
     * 获取当前播放状态
     */
    fun isCurrentlyPlaying(): Boolean = _isPlaying.value
}
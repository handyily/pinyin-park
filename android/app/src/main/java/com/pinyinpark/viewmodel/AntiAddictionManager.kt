package com.pinyinpark.viewmodel

import com.pinyinpark.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

// ============================================================
// 防沉迷状态
// ============================================================

sealed class AntiAddictionState {
    object Normal : AntiAddictionState()                      // 正常游戏
    object ShowReminder : AntiAddictionState()               // 显示休息提醒
    object ShowVerification : AntiAddictionState()            // 显示验证
    object TimeLimitReached : AntiAddictionState()           // 时间用完
    data class VerificationFailed(val attempts: Int) : AntiAddictionState()
}

// ============================================================
// 防沉迷管理器
// ============================================================

/**
 * 游戏防沉迷管理器
 * 控制幼儿游戏时长，定期弹出验证防止沉迷
 */
class AntiAddictionManager(
    private val settings: AntiAddictionSettings = AntiAddictionSettings()
) {
    // 当前状态
    private val _state = MutableStateFlow<AntiAddictionState>(AntiAddictionState.Normal)
    val state: StateFlow<AntiAddictionState> = _state.asStateFlow()

    // 当前验证码
    private val _challenge = MutableStateFlow<VerificationChallenge?>(null)
    val challenge: StateFlow<VerificationChallenge?> = _challenge.asStateFlow()

    // 游戏数据
    private val _gameData = MutableStateFlow(UserGameData())
    val gameData: StateFlow<UserGameData> = _gameData.asStateFlow()

    // 当前会话开始时间
    private var sessionStartTime: Long = 0L
    private var gamesPlayedInSession: Int = 0
    private var lastReminderTime: Long = 0L

    /**
     * 开始新的游戏会话
     */
    fun startSession() {
        sessionStartTime = System.currentTimeMillis()
        gamesPlayedInSession = 0
        lastReminderTime = sessionStartTime

        // 检查是否需要验证
        checkAndShowVerification()
    }

    /**
     * 记录完成一局游戏
     */
    fun onGameCompleted() {
        gamesPlayedInSession++

        // 检查是否需要弹出验证
        if (settings.verificationEnabled &&
            gamesPlayedInSession > 0 &&
            gamesPlayedInSession % settings.verificationInterval == 0) {
            showVerificationChallenge()
        }

        // 检查是否需要休息提醒
        checkRestReminder()
    }

    /**
     * 检查是否需要显示验证
     */
    private fun checkAndShowVerification() {
        val data = _gameData.value

        // 检查今日是否已达到限制
        if (data.todayGameTime >= settings.dailyGameTimeLimit) {
            _state.value = AntiAddictionState.TimeLimitReached
            return
        }

        // 检查单次会话是否达到限制
        val sessionMinutes = ((System.currentTimeMillis() - sessionStartTime) / 60000).toInt()
        if (sessionMinutes >= settings.singleSessionLimit) {
            _state.value = AntiAddictionState.ShowReminder
            return
        }
    }

    /**
     * 检查是否需要休息提醒
     */
    private fun checkRestReminder() {
        val timeSinceLastReminder = (System.currentTimeMillis() - lastReminderTime) / 60000
        if (timeSinceLastReminder >= settings.restReminderInterval) {
            _state.value = AntiAddictionState.ShowReminder
            lastReminderTime = System.currentTimeMillis()
        }
    }

    /**
     * 显示验证挑战
     */
    private fun showVerificationChallenge() {
        _challenge.value = VerificationChallenge.generate()
        _state.value = AntiAddictionState.ShowVerification
    }

    /**
     * 验证成功
     */
    fun onVerificationSuccess() {
        _state.value = AntiAddictionState.Normal
        _challenge.value = null
    }

    /**
     * 验证失败
     */
    fun onVerificationFailed(attempts: Int) {
        _challenge.value = null
        if (attempts >= 3) {
            _state.value = AntiAddictionState.VerificationFailed(attempts)
        } else {
            // 可以再试一次，重新生成验证
            showVerificationChallenge()
        }
    }

    /**
     * 验证超时
     */
    fun onVerificationTimeout() {
        _state.value = AntiAddictionState.VerificationFailed(0)
        _challenge.value = null
    }

    /**
     * 用户选择继续游戏
     */
    fun onContinueGame() {
        _state.value = AntiAddictionState.Normal
    }

    /**
     * 用户选择休息
     */
    fun onTakeBreak() {
        _state.value = AntiAddictionState.TimeLimitReached
    }

    /**
     * 获取当前游戏时间（分钟）
     */
    fun getCurrentGameTime(): Int {
        return ((System.currentTimeMillis() - sessionStartTime) / 60000).toInt()
    }

    /**
     * 获取剩余游戏时间
     */
    fun getRemainingTime(): Int {
        val data = _gameData.value
        val dailyRemaining = settings.dailyGameTimeLimit - data.todayGameTime
        val sessionRemaining = settings.singleSessionLimit - getCurrentGameTime()
        return minOf(dailyRemaining, sessionRemaining, 30)
    }

    /**
     * 更新游戏数据
     */
    fun updateGameData(update: UserGameData.() -> UserGameData) {
        _gameData.update { it.update() }
    }

    /**
     * 重置（用于新的一天）
     */
    fun resetDaily() {
        _gameData.update {
            it.copy(
                todayGameTime = 0,
                gameSessions = emptyList(),
                isRestricted = false,
                restrictionEndTime = 0L
            )
        }
        _state.value = AntiAddictionState.Normal
    }
}

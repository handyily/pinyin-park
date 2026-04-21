package com.pinyinpark.model

// ============================================================
// 拼音乐园 - 核心数据模型
// ============================================================

/**
 * 拼音字母类型
 */
enum class PinyinType {
    SHENG_MU,   // 声母
    YUN_MU,     // 韵母
    SHENG_DIAO, // 声调
    ZHENG_TI    // 整体认读音节
}

/**
 * 单个拼音知识点
 */
data class PinyinItem(
    val id: String,
    val character: String,      // 拼音字符，如 "b"
    val type: PinyinType,
    val audioResId: String,     // 音频资源名
    val exampleWord: String,    // 示例词，如 "爸"
    val examplePinyin: String,  // 示例拼音，如 "bà"
    val isUnlocked: Boolean = false,
    val learnProgress: Int = 0  // 0-100
)

/**
 * 学习章节
 */
data class LessonChapter(
    val id: Int,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val pinyinItems: List<PinyinItem>,
    val isUnlocked: Boolean = false,
    val completedCount: Int = 0
) {
    val totalCount: Int get() = pinyinItems.size
    val progressPercent: Float get() = if (totalCount == 0) 0f else completedCount.toFloat() / totalCount
}

/**
 * 用户学习进度
 */
data class UserProgress(
    val userId: String = "local_user",
    val totalStars: Int = 0,
    val totalDays: Int = 0,
    val streakDays: Int = 0,       // 连续学习天数
    val completedItems: Set<String> = emptySet(),
    val unlockedBadges: Set<String> = emptySet(),
    val petLevel: Int = 1,
    val petHunger: Int = 100,      // 宠物饱食度 0-100
    val lastLearnDate: Long = 0L
)

/**
 * 成就徽章
 */
data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val condition: BadgeCondition,
    var isUnlocked: Boolean = false,
    var unlockedAt: Long = 0L
)

/**
 * 徽章解锁条件
 */
data class BadgeCondition(
    val type: ConditionType,
    val targetValue: Int
)

enum class ConditionType {
    STREAK_DAYS,       // 连续学习天数
    TOTAL_STARS,       // 累计星星数
    COMPLETE_CHAPTER,  // 完成章节数
    GAME_WIN_COUNT,    // 游戏获胜次数
    PERFECT_SCORE,     // 满分次数
    FIRST_SPEAK        // 第一次语音识别成功
}

/**
 * 游戏类型
 */
enum class GameType {
    MATCHING,       // 配对连线
    WHACK_MOLE,     // 打地鼠
    SPEAK_REPEAT,   // 跟我读
    BLOCK_PUZZLE,   // 积木拼音
    CHAIN_GAME,     // 音节接龙
    MAZE_ADVENTURE  // 迷宫探险
}

/**
 * 游戏局结果
 */
data class GameResult(
    val gameType: GameType,
    val score: Int,
    val maxScore: Int,
    val starsEarned: Int,  // 1-3颗星
    val timeTaken: Long,   // 毫秒
    val isPerfect: Boolean = score == maxScore
)

// ============================================================
// 防沉迷系统 - 游戏时间控制
// ============================================================

/**
 * 防沉迷设置
 * 控制幼儿游戏时长，防止过度沉迷
 */
data class AntiAddictionSettings(
    val dailyGameTimeLimit: Int = 30,      // 每日游戏时长限制（分钟），默认30分钟
    val singleSessionLimit: Int = 15,       // 单次游戏时长限制（分钟），默认15分钟
    val restReminderInterval: Int = 10,     // 休息提醒间隔（分钟）
    val verificationEnabled: Boolean = true, // 是否启用验证
    val verificationInterval: Int = 3        // 每几局游戏验证一次
)

/**
 * 游戏会话记录
 */
data class GameSession(
    val startTime: Long,
    val endTime: Long = 0L,
    val gameType: GameType,
    val gamesPlayed: Int = 0,               // 本会话游戏局数
    val isValid: Boolean = true              // 是否通过验证
) {
    val durationMinutes: Int
        get() = if (endTime > 0) ((endTime - startTime) / 60000).toInt() else 0
}

/**
 * 用户游戏数据（包含防沉迷）
 */
data class UserGameData(
    val userId: String = "local_user",
    val totalStars: Int = 0,
    val streakDays: Int = 0,                // 连续学习天数
    val petLevel: Int = 1,
    val petHunger: Int = 100,
    // --- 防沉迷相关 ---
    val todayGameTime: Int = 0,             // 今日游戏时长（分钟）
    val totalGameTime: Int = 0,             // 累计游戏时长（分钟）
    val gameSessions: List<GameSession> = emptyList(),  // 游戏会话记录
    val lastPlayDate: Long = 0L,            // 上次游玩日期
    val consecutiveFailedVerifications: Int = 0, // 连续验证失败次数
    val isRestricted: Boolean = false,      // 是否被限制（超时）
    val restrictionEndTime: Long = 0L,       // 限制结束时间
    val completedItems: Set<String> = emptySet(),
    val unlockedBadges: Set<String> = emptySet()
) {
    companion object {
        // 默认防沉迷限制（分钟）
        const val DEFAULT_DAILY_LIMIT = 30
        const val DEFAULT_SESSION_LIMIT = 15
    }
}

/**
 * 验证码挑战（4位数字转大写汉字）
 */
data class VerificationChallenge(
    val code: String,              // 原始数字，如 "1234"
    val displayText: String,       // 显示文本，如 "壹贰叁肆"
    val options: List<String>,     // 选项列表（包含正确答案和干扰项）
    val correctAnswer: String,      // 正确答案
    val createdAt: Long = System.currentTimeMillis(),
    val isVerified: Boolean = false
) {
    companion object {
        // 数字到大写汉字的映射（0-9）
        private val digitToChinese = mapOf(
            '0' to "零", '1' to "壹", '2' to "贰", '3' to "叁",
            '4' to "肆", '5' to "伍", '6' to "陆", '7' to "柒",
            '8' to "捌", '9' to "玖"
        )

        /**
         * 生成随机验证码
         */
        fun generate(): VerificationChallenge {
            // 生成4位随机数字
            val code = (1000..9999).random().toString()
            val displayText = code.map { digitToChinese[it] }.joinToString("")

            // 生成干扰选项（其他随机大写汉字组合）
            val options = mutableListOf(displayText)
            repeat(3) {
                val fakeCode = (1000..9999).random().toString()
                val fakeDisplay = fakeCode.map { digitToChinese[it] ?: '?' }.joinToString("")
                options.add(fakeDisplay)
            }

            return VerificationChallenge(
                code = code,
                displayText = displayText,
                options = options.shuffled(),
                correctAnswer = displayText
            )
        }
    }
}

// ============================================================
// 预置数据：声母列表
// ============================================================
object PinyinData {

    val shengMuList = listOf(
        PinyinItem("b", "b", PinyinType.SHENG_MU, "audio_b", "爸", "bà"),
        PinyinItem("p", "p", PinyinType.SHENG_MU, "audio_p", "怕", "pà"),
        PinyinItem("m", "m", PinyinType.SHENG_MU, "audio_m", "妈", "mā"),
        PinyinItem("f", "f", PinyinType.SHENG_MU, "audio_f", "发", "fā"),
        PinyinItem("d", "d", PinyinType.SHENG_MU, "audio_d", "大", "dà"),
        PinyinItem("t", "t", PinyinType.SHENG_MU, "audio_t", "他", "tā"),
        PinyinItem("n", "n", PinyinType.SHENG_MU, "audio_n", "你", "nǐ"),
        PinyinItem("l", "l", PinyinType.SHENG_MU, "audio_l", "拉", "lā"),
        PinyinItem("g", "g", PinyinType.SHENG_MU, "audio_g", "哥", "gē"),
        PinyinItem("k", "k", PinyinType.SHENG_MU, "audio_k", "可", "kě"),
        PinyinItem("h", "h", PinyinType.SHENG_MU, "audio_h", "喝", "hē"),
        PinyinItem("j", "j", PinyinType.SHENG_MU, "audio_j", "鸡", "jī"),
        PinyinItem("q", "q", PinyinType.SHENG_MU, "audio_q", "去", "qù"),
        PinyinItem("x", "x", PinyinType.SHENG_MU, "audio_x", "西", "xī"),
        PinyinItem("zh", "zh", PinyinType.SHENG_MU, "audio_zh", "这", "zhè"),
        PinyinItem("ch", "ch", PinyinType.SHENG_MU, "audio_ch", "吃", "chī"),
        PinyinItem("sh", "sh", PinyinType.SHENG_MU, "audio_sh", "是", "shì"),
        PinyinItem("r", "r", PinyinType.SHENG_MU, "audio_r", "日", "rì"),
        PinyinItem("z", "z", PinyinType.SHENG_MU, "audio_z", "字", "zì"),
        PinyinItem("c", "c", PinyinType.SHENG_MU, "audio_c", "次", "cì"),
        PinyinItem("s", "s", PinyinType.SHENG_MU, "audio_s", "四", "sì")
    )

    val yunMuList = listOf(
        PinyinItem("a", "a", PinyinType.YUN_MU, "audio_a", "啊", "ā"),
        PinyinItem("o", "o", PinyinType.YUN_MU, "audio_o", "哦", "ó"),
        PinyinItem("e", "e", PinyinType.YUN_MU, "audio_e", "鹅", "é"),
        PinyinItem("i", "i", PinyinType.YUN_MU, "audio_i", "一", "yī"),
        PinyinItem("u", "u", PinyinType.YUN_MU, "audio_u", "五", "wǔ"),
        PinyinItem("ü", "ü", PinyinType.YUN_MU, "audio_v", "鱼", "yú"),
        PinyinItem("ai", "ai", PinyinType.YUN_MU, "audio_ai", "爱", "ài"),
        PinyinItem("ei", "ei", PinyinType.YUN_MU, "audio_ei", "诶", "éi"),
        PinyinItem("ui", "ui", PinyinType.YUN_MU, "audio_ui", "位", "wèi"),
        PinyinItem("ao", "ao", PinyinType.YUN_MU, "audio_ao", "澳", "ào"),
        PinyinItem("ou", "ou", PinyinType.YUN_MU, "audio_ou", "欧", "ōu"),
        PinyinItem("iu", "iu", PinyinType.YUN_MU, "audio_iu", "有", "yǒu"),
        PinyinItem("ie", "ie", PinyinType.YUN_MU, "audio_ie", "叶", "yè"),
        PinyinItem("üe", "üe", PinyinType.YUN_MU, "audio_ve", "月", "yuè"),
        PinyinItem("er", "er", PinyinType.YUN_MU, "audio_er", "二", "èr"),
        PinyinItem("an", "an", PinyinType.YUN_MU, "audio_an", "安", "ān"),
        PinyinItem("en", "en", PinyinType.YUN_MU, "audio_en", "恩", "ēn"),
        PinyinItem("in", "in", PinyinType.YUN_MU, "audio_in", "音", "yīn"),
        PinyinItem("un", "un", PinyinType.YUN_MU, "audio_un", "云", "yún"),
        PinyinItem("ün", "ün", PinyinType.YUN_MU, "audio_vn", "晕", "yùn"),
        PinyinItem("ang", "ang", PinyinType.YUN_MU, "audio_ang", "昂", "áng"),
        PinyinItem("eng", "eng", PinyinType.YUN_MU, "audio_eng", "鹰", "yīng"),
        PinyinItem("ing", "ing", PinyinType.YUN_MU, "audio_ing", "英", "yīng"),
        PinyinItem("ong", "ong", PinyinType.YUN_MU, "audio_ong", "翁", "wēng")
    )

    val allBadges = listOf(
        Badge("first_speak", "初次发声", "第一次语音识别成功", "🎤",
            BadgeCondition(ConditionType.FIRST_SPEAK, 1)),
        Badge("streak_3", "三天小勇士", "连续学习3天", "🔥",
            BadgeCondition(ConditionType.STREAK_DAYS, 3)),
        Badge("streak_7", "一周超人", "连续学习7天", "👑",
            BadgeCondition(ConditionType.STREAK_DAYS, 7)),
        Badge("streak_30", "月度学霸", "连续学习30天", "🏆",
            BadgeCondition(ConditionType.STREAK_DAYS, 30)),
        Badge("stars_50", "星光闪闪", "累计获得50颗星", "⭐",
            BadgeCondition(ConditionType.TOTAL_STARS, 50)),
        Badge("stars_100", "星河大师", "累计获得100颗星", "🌟",
            BadgeCondition(ConditionType.TOTAL_STARS, 100)),
        Badge("perfect_3", "完美三连", "连续3次满分", "💯",
            BadgeCondition(ConditionType.PERFECT_SCORE, 3)),
        Badge("game_king", "游戏小王", "游戏获胜10次", "🎮",
            BadgeCondition(ConditionType.GAME_WIN_COUNT, 10))
    )
}

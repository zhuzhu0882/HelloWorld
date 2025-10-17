



package com.example.helloworld

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import androidx.compose.ui.res.stringResource
import com.example.helloworld.ui.theme.HelloWorldTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HelloWorldTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BabyLearningApp()
                }
            }
        }
    }
}

@Composable
fun BabyLearningApp() {
    var currentScreen by remember { mutableStateOf("home") }
    var userLevel by remember { mutableStateOf(1) }
    var userScore by remember { mutableStateOf(0) }
    var selectedItem by remember { mutableStateOf<LearningItem?>(null) }
    var showAnimation by remember { mutableStateOf(false) }
    var userProgress by remember { mutableStateOf(UserProgress()) }

    // 处理返回键逻辑
    BackHandler(enabled = currentScreen != "home") {
        when (currentScreen) {
            "learning" -> {
                if (showAnimation) {
                    showAnimation = false
                    selectedItem = null
                } else {
                    currentScreen = "home"
                }
            }
            "progress", "settings", "practice" -> {
                currentScreen = "home"
            }
        }
    }

    when (currentScreen) {
        "home" -> {
            HomeScreen(
                onStartLearning = { currentScreen = "learning" },
                onViewProgress = { currentScreen = "progress" },
                onSettings = { currentScreen = "settings" },
                onPractice = { currentScreen = "practice" },
                userLevel = userLevel,
                userScore = userScore,
                userProgress = userProgress
            )
        }
        "learning" -> {
            LearningScreen(
                onBackClick = {
                    if (showAnimation) {
                        showAnimation = false
                        selectedItem = null
                    } else {
                        currentScreen = "home"
                    }
                },
                onItemSelect = { item ->
                    selectedItem = item
                    showAnimation = true
                },
                onLearningComplete = { points ->
                    userScore += points
                    val newLearnedItems = userProgress.learnedItems + 1
                    val newTotalExp = userProgress.totalExperience + points
                    val newLevel = if (newTotalExp >= userLevel * 100) userLevel + 1 else userLevel

                    userProgress = userProgress.copy(
                        learnedItems = newLearnedItems,
                        totalExperience = newTotalExp,
                        currentLevel = newLevel,
                        practiceDays = userProgress.practiceDays
                    )

                    if (userScore >= userLevel * 100) {
                        userLevel++
                    }
                    showAnimation = false
                },
                selectedItem = selectedItem,
                showAnimation = showAnimation
            )
        }
        "progress" -> {
            ProgressScreen(
                onBackClick = { currentScreen = "home" },
                userProgress = userProgress,
                userLevel = userLevel,
                userScore = userScore
            )
        }
        "settings" -> {
            SettingsScreen(
                onBackClick = { currentScreen = "home" }
            )
        }
        "practice" -> {
            PracticeScreen(
                onBackClick = { currentScreen = "home" },
                onPracticeComplete = { score, earnedPoints ->
                    userScore += earnedPoints
                    val newTotalExp = userProgress.totalExperience + earnedPoints
                    val newLevel = if (newTotalExp >= userLevel * 100) userLevel + 1 else userLevel

                    userProgress = userProgress.copy(
                        totalExperience = newTotalExp,
                        currentLevel = newLevel,
                        practiceDays = userProgress.practiceDays
                    )

                    if (userScore >= userLevel * 100) {
                        userLevel++
                    }
                }
            )
        }
    }
}

@Composable
fun HomeScreen(
    onStartLearning: () -> Unit,
    onViewProgress: () -> Unit,
    onSettings: () -> Unit,
    onPractice: () -> Unit,
    userLevel: Int,
    userScore: Int,
    userProgress: UserProgress
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // 应用标题
        Text(
            text = "宝宝学汉字",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "让学习变得有趣！",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // 用户信息卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "我的进度",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "等级",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "$userLevel",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "积分",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "$userScore",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // 功能按钮
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onStartLearning,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text("开始学习", fontSize = 18.sp)
            }

            OutlinedButton(
                onClick = onViewProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text("查看进度", fontSize = 18.sp)
            }

            OutlinedButton(
                onClick = onSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text("设置", fontSize = 18.sp)
            }

            Button(
                onClick = onPractice,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("练习考试", fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 底部信息
        Text(
            text = "适合3-8岁儿童的识字应用",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LearningScreen(
    onBackClick: () -> Unit,
    onItemSelect: (LearningItem) -> Unit,
    onLearningComplete: (Int) -> Unit,
    selectedItem: LearningItem?,
    showAnimation: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 顶部栏
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
            }
            Text(
                text = "选择一个汉字开始学习",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { /* TODO: 刷新数据 */ }) {
                Icon(Icons.Filled.Refresh, contentDescription = "刷新")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showAnimation && selectedItem != null) {
            // 显示学习动画界面
            CharacterEvolutionAnimation(
                item = selectedItem!!,
                onAnimationComplete = { onLearningComplete(20) }
            )
        } else {
            // 显示学习项目选择网格
            val learningItems = remember { generateLearningItems() }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 140.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(learningItems) { item ->
                    LearningItemCard(
                        item = item,
                        onItemClick = { onItemSelect(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun LearningItemCard(
    item: LearningItem,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onItemClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // 物体图标占位符
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getObjectEmoji(item.objectName),
                    fontSize = 32.sp
                )
            }

            // 中文名称
            Text(
                text = item.chineseCharacter,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )

            // 拼音
            Text(
                text = item.pinyin,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )

            // 英文名称
            Text(
                text = item.englishWord,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )

            // 播放按钮
            IconButton(
                onClick = onItemClick,
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(18.dp)
                    )
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "播放",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun CharacterEvolutionAnimation(
    item: LearningItem,
    onAnimationComplete: () -> Unit
) {
    var animationStep by remember { mutableStateOf(0) }
    val steps = listOf("物体", "象形文字", "现代汉字", "发音")
    val context = LocalContext.current
    var showReward by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        for (i in steps.indices) {
            animationStep = i
            kotlinx.coroutines.delay(2500)
        }
        showReward = true
        kotlinx.coroutines.delay(2000)
        onAnimationComplete()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${steps[animationStep]} - ${item.chineseCharacter}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            when (animationStep) {
                0 -> {
                    val scale by animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .scale(scale)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getObjectEmoji(item.objectName),
                            fontSize = 120.sp
                        )
                    }
                }
                1 -> {
                    val scale by animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = tween(1000)
                    )
                    // 显示象形文字阶段
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = getPictographCharacter(item.chineseCharacter),
                            fontSize = 100.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.scale(scale)
                        )
                        Text(
                            text = "象形文字",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = getPictographDescription(item.chineseCharacter),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                2 -> {
                    val scale by animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = tween(1000)
                    )
                    Text(
                        text = item.chineseCharacter,
                        fontSize = 120.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.scale(scale)
                    )
                }
                3 -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = item.chineseCharacter,
                            fontSize = 80.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = item.pinyin,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = item.englishWord,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        val coroutineScope = rememberCoroutineScope()
                        var isPlayingChinese by remember { mutableStateOf(false) }
                        var isPlayingEnglish by remember { mutableStateOf(false) }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        isPlayingChinese = true
                                        playChineseAudio(context, item.chineseCharacter)
                                        delay(2000) // 模拟音频播放时间
                                        isPlayingChinese = false
                                    }
                                },
                                modifier = Modifier.size(80.dp),
                                enabled = !isPlayingChinese && !isPlayingEnglish
                            ) {
                                if (isPlayingChinese) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Column {
                                        Text("中", fontSize = 16.sp)
                                        Text("🔊", fontSize = 12.sp)
                                    }
                                }
                            }
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        isPlayingEnglish = true
                                        playEnglishAudio(context, item.englishWord)
                                        delay(2000) // 模拟音频播放时间
                                        isPlayingEnglish = false
                                    }
                                },
                                modifier = Modifier.size(80.dp),
                                enabled = !isPlayingChinese && !isPlayingEnglish
                            ) {
                                if (isPlayingEnglish) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Column {
                                        Text("EN", fontSize = 14.sp)
                                        Text("🔊", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 奖励弹窗
        if (showReward) {
            RewardAnimation(
                onDismiss = { showReward = false }
            )
        }
    }
}

@Composable
fun RewardAnimation(onDismiss: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .scale(scale)
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🎉 太棒了！",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = "学习完成！",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "星星",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Text(
                    text = "+20 经验值",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun getObjectEmoji(objectName: String): String {
    return when (objectName) {
        "苹果" -> "🍎"
        "香蕉" -> "🍌"
        "太阳" -> "☀️"
        "月亮" -> "🌙"
        "水" -> "💧"
        "山" -> "⛰️"
        else -> "📦"
    }
}

private fun getPictographCharacter(chineseChar: String): String {
    return when (chineseChar) {
        "山" -> "⛰️"
        "水" -> "〰️"
        "日" -> "☀️"
        "月" -> "🌙"
        "木" -> "🌳"
        "火" -> "🔥"
        "人" -> "🚶"
        "口" -> "👄"
        "门" -> "🚪"
        "田" -> "⬜"
        else -> "📝"
    }
}

private fun getPictographDescription(chineseChar: String): String {
    return when (chineseChar) {
        "山" -> "古文字像山峰连绵的形状"
        "水" -> "古文字像流水蜿蜒的形状"
        "日" -> "古文字像太阳的圆形，中间有一点代表发光"
        "月" -> "古文字像月亮的弯月形状"
        "木" -> "古文字像树木，上有枝干，下有根系"
        "火" -> "古文字像火焰跳动的形状"
        "人" -> "古文字像站立的人的侧面形状"
        "口" -> "古文字像人张开的嘴巴"
        "门" -> "古文字像双扇门的形状"
        "田" -> "古文字像田地被分割成小块的形状"
        else -> "古文字描摹物体的自然形态"
    }
}

private fun playChineseAudio(context: Context, character: String) {
    // 使用TTS或预录制的音频文件播放中文发音
    try {
        // 这里模拟中文发音播放
        // 在实际应用中，可以集成TTS引擎或预录制的音频文件
        playSystemSound(context)

        // 可以根据不同字符播放不同的音频
        // val audioResourceId = getChineseAudioResource(character)
        // val mediaPlayer = MediaPlayer.create(context, audioResourceId)
        // mediaPlayer?.start()
        // mediaPlayer?.release()
    } catch (e: Exception) {
        // 处理音频播放异常
    }
}

private fun playEnglishAudio(context: Context, word: String) {
    // 使用TTS或预录制的音频文件播放英文发音
    try {
        // 这里模拟英文发音播放
        // 在实际应用中，可以集成TTS引擎或预录制的音频文件
        playSystemSound(context)

        // 可以根据不同单词播放不同的音频
        // val audioResourceId = getEnglishAudioResource(word)
        // val mediaPlayer = MediaPlayer.create(context, audioResourceId)
        // mediaPlayer?.start()
        // mediaPlayer?.release()
    } catch (e: Exception) {
        // 处理音频播放异常
    }
}

private fun playSystemSound(context: Context) {
    // 使用系统默认提示音作为音频播放的反馈
    try {
        val mediaPlayer = MediaPlayer.create(context, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
        mediaPlayer?.start()
        mediaPlayer?.release()
    } catch (e: Exception) {
        // 忽略音频播放错误
    }
}

// 预留函数：获取中文音频资源ID
private fun getChineseAudioResource(character: String): Int {
    // 这里可以根据字符返回对应的音频资源ID
    // 例如：return R.raw.audio_chinese_${character}
    return android.provider.Settings.System.DEFAULT_NOTIFICATION_URI.hashCode()
}

// 预留函数：获取英文音频资源ID
private fun getEnglishAudioResource(word: String): Int {
    // 这里可以根据单词返回对应的音频资源ID
    // 例如：return R.raw.audio_english_${word}
    return android.provider.Settings.System.DEFAULT_NOTIFICATION_URI.hashCode()
}

data class LearningItem(
    val objectName: String,
    val chineseCharacter: String,
    val pinyin: String,
    val englishWord: String,
    val difficulty: Int
)

data class UserProgress(
    val learnedItems: Int = 0,
    val totalItems: Int = 1000,
    val currentLevel: Int = 1,
    val totalExperience: Int = 0,
    val practiceDays: Int = 1,
    val achievements: List<Achievement> = emptyList()
)

data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean = false
)

private fun generateLearningItems(): List<LearningItem> {
    return listOf(
        // 水果类
        LearningItem("苹果", "苹果", "píng guǒ", "apple", 1),
        LearningItem("香蕉", "香蕉", "xiāng jiāo", "banana", 1),
        LearningItem("橙子", "橙子", "chéng zi", "orange", 1),
        LearningItem("葡萄", "葡萄", "pú táo", "grape", 1),
        LearningItem("西瓜", "西瓜", "xī guā", "watermelon", 1),
        LearningItem("草莓", "草莓", "cǎo méi", "strawberry", 1),
        LearningItem("梨", "梨", "lí", "pear", 1),
        LearningItem("桃子", "桃子", "táo zi", "peach", 1),
        LearningItem("樱桃", "樱桃", "yīng táo", "cherry", 1),
        LearningItem("柠檬", "柠檬", "níng méng", "lemon", 1),

        // 动物类
        LearningItem("猫", "猫", "māo", "cat", 1),
        LearningItem("狗", "狗", "gǒu", "dog", 1),
        LearningItem("兔子", "兔子", "tù zi", "rabbit", 1),
        LearningItem("鸟", "鸟", "niǎo", "bird", 1),
        LearningItem("鱼", "鱼", "yú", "fish", 1),
        LearningItem("老虎", "老虎", "lǎo hǔ", "tiger", 2),
        LearningItem("狮子", "狮子", "shī zi", "lion", 2),
        LearningItem("大象", "大象", "dà xiàng", "elephant", 2),
        LearningItem("熊猫", "熊猫", "xióng māo", "panda", 2),
        LearningItem("猴子", "猴子", "hóu zi", "monkey", 1),

        // 自然类
        LearningItem("太阳", "太阳", "tài yáng", "sun", 1),
        LearningItem("月亮", "月亮", "yuè liang", "moon", 1),
        LearningItem("星星", "星星", "xīng xing", "star", 1),
        LearningItem("云", "云", "yún", "cloud", 1),
        LearningItem("雨", "雨", "yǔ", "rain", 1),
        LearningItem("雪", "雪", "xuě", "snow", 1),
        LearningItem("风", "风", "fēng", "wind", 1),
        LearningItem("山", "山", "shān", "mountain", 1),
        LearningItem("水", "水", "shuǐ", "water", 1),
        LearningItem("火", "火", "huǒ", "fire", 1),

        // 颜色类
        LearningItem("红", "红", "hóng", "red", 1),
        LearningItem("蓝", "蓝", "lán", "blue", 1),
        LearningItem("黄", "黄", "huáng", "yellow", 1),
        LearningItem("绿", "绿", "lǜ", "green", 1),
        LearningItem("白", "白", "bái", "white", 1),
        LearningItem("黑", "黑", "hēi", "black", 1),
        LearningItem("紫", "紫", "zǐ", "purple", 2),
        LearningItem("橙", "橙", "chéng", "orange", 1),
        LearningItem("粉", "粉", "fěn", "pink", 2),
        LearningItem("灰", "灰", "huī", "gray", 2),

        // 数字类
        LearningItem("一", "一", "yī", "one", 1),
        LearningItem("二", "二", "èr", "two", 1),
        LearningItem("三", "三", "sān", "three", 1),
        LearningItem("四", "四", "sì", "four", 1),
        LearningItem("五", "五", "wǔ", "five", 1),
        LearningItem("六", "六", "liù", "six", 1),
        LearningItem("七", "七", "qī", "seven", 1),
        LearningItem("八", "八", "bā", "eight", 1),
        LearningItem("九", "九", "jiǔ", "nine", 1),
        LearningItem("十", "十", "shí", "ten", 1),

        // 身体部位
        LearningItem("头", "头", "tóu", "head", 1),
        LearningItem("眼", "眼", "yǎn", "eye", 1),
        LearningItem("鼻", "鼻", "bí", "nose", 1),
        LearningItem("口", "口", "kǒu", "mouth", 1),
        LearningItem("手", "手", "shǒu", "hand", 1),
        LearningItem("脚", "脚", "jiǎo", "foot", 1),
        LearningItem("心", "心", "xīn", "heart", 1),
        LearningItem("耳", "耳", "ěr", "ear", 1),
        LearningItem("牙", "牙", "yá", "tooth", 1),
        LearningItem("发", "发", "fà", "hair", 1),

        // 家庭成员
        LearningItem("爸", "爸", "bà", "dad", 1),
        LearningItem("妈", "妈", "mā", "mom", 1),
        LearningItem("哥", "哥", "gē", "brother", 1),
        LearningItem("姐", "姐", "jiě", "sister", 1),
        LearningItem("爷", "爷", "yé", "grandpa", 2),
        LearningItem("奶", "奶", "nǎi", "grandma", 2),
        LearningItem("家", "家", "jiā", "home", 1),
        LearningItem("人", "人", "rén", "person", 1),

        // 交通工具
        LearningItem("车", "车", "chē", "car", 1),
        LearningItem("船", "船", "chuán", "boat", 1),
        LearningItem("飞机", "飞机", "fēi jī", "airplane", 2),
        LearningItem("自行车", "自行车", "zì xíng chē", "bicycle", 2),
        LearningItem("火车", "火车", "huǒ chē", "train", 2),
        LearningItem("桥", "桥", "qiáo", "bridge", 2),

        // 食物类
        LearningItem("饭", "饭", "fàn", "rice", 1),
        LearningItem("面", "面", "miàn", "noodles", 1),
        LearningItem("肉", "肉", "ròu", "meat", 1),
        LearningItem("蛋", "蛋", "dàn", "egg", 1),
        LearningItem("奶", "奶", "nǎi", "milk", 1),
        LearningItem("茶", "茶", "chá", "tea", 1),
        LearningItem("糖", "糖", "táng", "sugar", 2),
        LearningItem("盐", "盐", "yán", "salt", 2),

        // 自然景物
        LearningItem("花", "花", "huā", "flower", 1),
        LearningItem("草", "草", "cǎo", "grass", 1),
        LearningItem("树", "树", "shù", "tree", 1),
        LearningItem("林", "林", "lín", "forest", 2),
        LearningItem("河", "河", "hé", "river", 1),
        LearningItem("海", "海", "hǎi", "sea", 1),
        LearningItem("湖", "湖", "hú", "lake", 2),
        LearningItem("田", "田", "tián", "field", 1),
        LearningItem("园", "园", "yuán", "garden", 2),
        LearningItem("路", "路", "lù", "road", 1),

        // 天气类
        LearningItem("天", "天", "tiān", "sky", 1),
        LearningItem("地", "地", "dì", "ground", 1),
        LearningItem("春", "春", "chūn", "spring", 2),
        LearningItem("夏", "夏", "xià", "summer", 2),
        LearningItem("秋", "秋", "qiū", "autumn", 2),
        LearningItem("冬", "冬", "dōng", "winter", 2),

        // 基础汉字
        LearningItem("大", "大", "dà", "big", 1),
        LearningItem("小", "小", "xiǎo", "small", 1),
        LearningItem("高", "高", "gāo", "tall", 1),
        LearningItem("低", "低", "dī", "low", 2),
        LearningItem("长", "长", "cháng", "long", 1),
        LearningItem("短", "短", "duǎn", "short", 2),
        LearningItem("多", "多", "duō", "many", 1),
        LearningItem("少", "少", "shǎo", "few", 2),
        LearningItem("新", "新", "xīn", "new", 1),
        LearningItem("旧", "旧", "jiù", "old", 2),

        // 常用动作
        LearningItem("看", "看", "kàn", "look", 1),
        LearningItem("听", "听", "tīng", "listen", 1),
        LearningItem("说", "说", "shuō", "speak", 1),
        LearningItem("吃", "吃", "chī", "eat", 1),
        LearningItem("喝", "喝", "hē", "drink", 1),
        LearningItem("走", "走", "zǒu", "walk", 1),
        LearningItem("跑", "跑", "pǎo", "run", 1),
        LearningItem("坐", "坐", "zuò", "sit", 1),
        LearningItem("睡", "睡", "shuì", "sleep", 1),
        LearningItem("玩", "玩", "wán", "play", 1),

        // 学习相关
        LearningItem("书", "书", "shū", "book", 1),
        LearningItem("笔", "笔", "bǐ", "pen", 1),
        LearningItem("字", "字", "zì", "word", 1),
        LearningItem("学", "学", "xué", "learn", 1),
        LearningItem("读", "读", "dú", "read", 1),
        LearningItem("写", "写", "xiě", "write", 1),
        LearningItem("画", "画", "huà", "draw", 1),
        LearningItem("问", "问", "wèn", "ask", 1),

        // 方位类
        LearningItem("东", "东", "dōng", "east", 2),
        LearningItem("南", "南", "nán", "south", 2),
        LearningItem("西", "西", "xī", "west", 2),
        LearningItem("北", "北", "běi", "north", 2),
        LearningItem("上", "上", "shàng", "up", 1),
        LearningItem("下", "下", "xià", "down", 1),
        LearningItem("左", "左", "zuǒ", "left", 1),
        LearningItem("右", "右", "yòu", "right", 1),
        LearningItem("中", "中", "zhōng", "middle", 1),
        LearningItem("前", "前", "qián", "front", 1),
        LearningItem("后", "后", "hòu", "back", 1),

        // 时间类
        LearningItem("年", "年", "nián", "year", 2),
        LearningItem("月", "月", "yuè", "month", 1),
        LearningItem("日", "日", "rì", "day", 1),
        LearningItem("时", "时", "shí", "hour", 2),
        LearningItem("分", "分", "fēn", "minute", 2),
        LearningItem("早", "早", "zǎo", "morning", 1),
        LearningItem("晚", "晚", "wǎn", "evening", 1),
        LearningItem("午", "午", "wǔ", "noon", 2),

        // 常见物品
        LearningItem("门", "门", "mén", "door", 1),
        LearningItem("窗", "窗", "chuāng", "window", 1),
        LearningItem("桌", "桌", "zhuō", "table", 1),
        LearningItem("椅", "椅", "yǐ", "chair", 1),
        LearningItem("床", "床", "chuáng", "bed", 1),
        LearningItem("灯", "灯", "dēng", "lamp", 1),
        LearningItem("杯", "杯", "bēi", "cup", 1),
        LearningItem("碗", "碗", "wǎn", "bowl", 1),
        LearningItem("包", "包", "bāo", "bag", 1),
        LearningItem("伞", "伞", "sǎn", "umbrella", 2)
    )
}

@Composable
fun ProgressScreen(
    onBackClick: () -> Unit,
    userProgress: UserProgress,
    userLevel: Int,
    userScore: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 顶部栏
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
            }
            Text(
                text = "学习进度",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 总体进度卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🎯 学习总进度",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                // 进度环
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = userProgress.learnedItems.toFloat() / userProgress.totalItems,
                        modifier = Modifier.size(120.dp),
                        strokeWidth = 8.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${userProgress.learnedItems}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "/ ${userProgress.totalItems}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "已完成 ${userProgress.learnedItems}/${userProgress.totalItems} 个汉字",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 统计信息网格
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            item {
                StatCard(
                    title = "当前等级",
                    value = "$userLevel",
                    icon = "🌟",
                    color = MaterialTheme.colorScheme.secondaryContainer
                )
            }
            item {
                StatCard(
                    title = "总积分",
                    value = "$userScore",
                    icon = "💎",
                    color = MaterialTheme.colorScheme.tertiaryContainer
                )
            }
            item {
                StatCard(
                    title = "经验值",
                    value = "${userProgress.totalExperience}",
                    icon = "⚡",
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            }
            item {
                StatCard(
                    title = "学习天数",
                    value = "${userProgress.practiceDays}",
                    icon = "📅",
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 成就展示
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "🏆 成就",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (userProgress.achievements.isEmpty()) {
                    Text(
                        text = "继续学习，解锁更多成就！",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(userProgress.achievements) { achievement ->
                            AchievementItem(achievement = achievement)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = icon,
                fontSize = 32.sp
            )
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AchievementItem(achievement: Achievement) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    if (achievement.isUnlocked)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        Color.Gray.copy(alpha = 0.3f),
                    RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = achievement.icon,
                fontSize = 24.sp
            )
        }
        Text(
            text = achievement.name,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 顶部栏
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
            }
            Text(
                text = "设置",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 设置选项
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                SettingItem(
                    title = "音效开关",
                    subtitle = "开启/关闭学习音效",
                    icon = Icons.Default.Settings,
                    onClick = { /* TODO: 实现音效开关 */ }
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                SettingItem(
                    title = "背景音乐",
                    subtitle = "控制背景音乐播放",
                    icon = Icons.Default.Settings,
                    onClick = { /* TODO: 实现背景音乐控制 */ }
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                SettingItem(
                    title = "学习难度",
                    subtitle = "调整学习内容难度",
                    icon = Icons.Default.Settings,
                    onClick = { /* TODO: 实现难度调整 */ }
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                SettingItem(
                    title = "家长控制",
                    subtitle = "设置家长控制选项",
                    icon = Icons.Default.Settings,
                    onClick = { /* TODO: 实现家长控制 */ }
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                SettingItem(
                    title = "关于我们",
                    subtitle = "应用版本和开发者信息",
                    icon = Icons.Default.Info,
                    onClick = { /* TODO: 实现关于页面 */ }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 版本信息
        Text(
            text = "宝宝学汉字 v1.0.0",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SettingItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "进入",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PracticeScreen(
    onBackClick: () -> Unit,
    onPracticeComplete: (Int, Int) -> Unit
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var isPracticeComplete by remember { mutableStateOf(false) }

    val allItems = remember { generateLearningItems() }
    val practiceQuestions = remember {
        allItems.shuffled().take(10) // 取10个随机题目
    }

    val currentQuestion = practiceQuestions[currentQuestionIndex]
    val options = remember(currentQuestionIndex) {
        val wrongOptions = allItems.filter { it.chineseCharacter != currentQuestion.chineseCharacter }
            .shuffled().take(3)
        (wrongOptions + currentQuestion).shuffled()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 顶部栏
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
            }
            Text(
                text = "练习考试",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${currentQuestionIndex + 1}/${practiceQuestions.size}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 进度条
        LinearProgressIndicator(
            progress = (currentQuestionIndex + 1).toFloat() / practiceQuestions.size,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 题目区域
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "选择正确的汉字",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = getObjectEmoji(currentQuestion.objectName),
                    fontSize = 80.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = currentQuestion.englishWord,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = currentQuestion.pinyin,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 选项区域
        if (!showResult) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(options) { option ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedAnswer = option.chineseCharacter
                                showResult = true

                                if (option.chineseCharacter == currentQuestion.chineseCharacter) {
                                    score++
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedAnswer == option.chineseCharacter) {
                                MaterialTheme.colorScheme.secondaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = option.chineseCharacter,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        } else {
            // 结果显示
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedAnswer == currentQuestion.chineseCharacter) {
                        Color(0xFF4CAF50)
                    } else {
                        Color(0xFFF44336)
                    }
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (selectedAnswer == currentQuestion.chineseCharacter) {
                            "🎉 回答正确！"
                        } else {
                            "❌ 回答错误"
                        },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    if (selectedAnswer != currentQuestion.chineseCharacter) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "正确答案: ${currentQuestion.chineseCharacter}",
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (currentQuestionIndex < practiceQuestions.size - 1) {
                                currentQuestionIndex++
                                selectedAnswer = null
                                showResult = false
                            } else {
                                isPracticeComplete = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = if (selectedAnswer == currentQuestion.chineseCharacter) {
                                Color(0xFF4CAF50)
                            } else {
                                Color(0xFFF44336)
                            }
                        )
                    ) {
                        Text(
                            text = if (currentQuestionIndex < practiceQuestions.size - 1) {
                                "下一题"
                            } else {
                                "完成练习"
                            },
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        if (isPracticeComplete) {
            // 练习完成弹窗
            PracticeCompleteDialog(
                score = score,
                totalQuestions = practiceQuestions.size,
                onDismiss = {
                    val earnedPoints = score * 10
                    onPracticeComplete(score, earnedPoints)
                }
            )
        }
    }
}

@Composable
fun PracticeCompleteDialog(
    score: Int,
    totalQuestions: Int,
    onDismiss: () -> Unit
) {
    val percentage = (score.toFloat() / totalQuestions * 100).toInt()
    val stars = when {
        percentage >= 90 -> 3
        percentage >= 70 -> 2
        percentage >= 50 -> 1
        else -> 0
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🎊 练习完成！",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = "得分: $score/$totalQuestions",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = "正确率: $percentage%",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) { index ->
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "星星",
                            tint = if (index < stars) {
                                Color(0xFFFFD700)
                            } else {
                                Color.Gray
                            },
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Text(
                    text = "获得经验值: ${score * 10}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                val message = when {
                    percentage >= 90 -> "太棒了！你是学习小天才！"
                    percentage >= 70 -> "做得很好！继续加油！"
                    percentage >= 50 -> "还不错！多多练习会更好！"
                    else -> "继续努力！你一定可以的！"
                }

                Text(
                    text = message,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("返回首页", fontSize = 18.sp)
                }
            }
        }
    }
}
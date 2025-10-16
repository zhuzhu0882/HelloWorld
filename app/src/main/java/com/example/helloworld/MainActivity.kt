



package com.example.helloworld

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.helloworld.ui.theme.HelloWorldTheme
import kotlinx.coroutines.delay

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

    when (currentScreen) {
        "home" -> {
            HomeScreen(
                onStartLearning = { currentScreen = "learning" },
                userLevel = userLevel,
                userScore = userScore
            )
        }
        "learning" -> {
            LearningScreen(
                onBackClick = { currentScreen = "home" },
                onItemSelect = { item ->
                    selectedItem = item
                    showAnimation = true
                },
                onLearningComplete = { points ->
                    userScore += points
                    if (userScore >= userLevel * 100) {
                        userLevel++
                    }
                    showAnimation = false
                },
                selectedItem = selectedItem,
                showAnimation = showAnimation
            )
        }
    }
}

@Composable
fun HomeScreen(
    onStartLearning: () -> Unit,
    userLevel: Int,
    userScore: Int
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
                onClick = { /* TODO: 实现进度查看 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text("查看进度", fontSize = 18.sp)
            }

            OutlinedButton(
                onClick = { /* TODO: 实现设置 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text("设置", fontSize = 18.sp)
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
                Icon(Icons.Filled.Refresh, contentDescription = "返回")
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
            val learningItems = remember {
                listOf(
                    LearningItem("苹果", "苹果", "píng guǒ", "apple", 1),
                    LearningItem("香蕉", "香蕉", "xiāng jiāo", "banana", 1),
                    LearningItem("太阳", "太阳", "tài yáng", "sun", 1),
                    LearningItem("月亮", "月亮", "yuè liang", "moon", 1),
                    LearningItem("水", "水", "shuǐ", "water", 1),
                    LearningItem("山", "山", "shān", "mountain", 1)
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // 物体图标占位符
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🍎",
                    fontSize = 40.sp
                )
            }

            // 中文名称
            Text(
                text = item.chineseCharacter,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // 拼音
            Text(
                text = item.pinyin,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 英文名称
            Text(
                text = item.englishWord,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 播放按钮
            IconButton(
                onClick = onItemClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(24.dp)
                    )
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "播放",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
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
                    Text(
                        text = "📝",
                        fontSize = 120.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.scale(scale)
                    )
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

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { playSound(context, "chinese") },
                                modifier = Modifier.size(60.dp)
                            ) {
                                Text("中", fontSize = 16.sp)
                            }
                            Button(
                                onClick = { playSound(context, "english") },
                                modifier = Modifier.size(60.dp)
                            ) {
                                Text("EN", fontSize = 16.sp)
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

private fun playSound(context: Context, type: String) {
    // 这里使用系统默认提示音作为示例
    try {
        when (type) {
            "chinese" -> {
                // 播放中文发音提示音
                playSystemSound(context)
            }
            "english" -> {
                // 播放英文发音提示音
                playSystemSound(context)
            }
        }
    } catch (e: Exception) {
        // 处理音频播放异常
    }
}

private fun playSystemSound(context: Context) {
    // 使用系统默认提示音
    try {
        val mediaPlayer = MediaPlayer.create(context, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
        mediaPlayer?.start()
        mediaPlayer?.release()
    } catch (e: Exception) {
        // 忽略音频播放错误
    }
}

data class LearningItem(
    val objectName: String,
    val chineseCharacter: String,
    val pinyin: String,
    val englishWord: String,
    val difficulty: Int
)
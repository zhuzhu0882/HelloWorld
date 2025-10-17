package com.example.helloworld

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
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
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BabyLearningApp()
        }
    }
}

data class LearningItem(
    val objectName: String,
    val chineseCharacter: String,
    val pinyin: String,
    val englishWord: String,
    val category: String,
    val difficulty: Int
)

// 扩展的学习数据库 - 180个物体
private val learningDatabase = listOf(
    // 水果类 (30个)
    LearningItem("苹果", "苹果", "píng guǒ", "apple", "水果", 1),
    LearningItem("香蕉", "香蕉", "xiāng jiāo", "banana", "水果", 1),
    LearningItem("橙子", "橙子", "chéng zi", "orange", "水果", 1),
    LearningItem("葡萄", "葡萄", "pú táo", "grape", "水果", 1),
    LearningItem("草莓", "草莓", "cǎo méi", "strawberry", "水果", 1),
    LearningItem("西瓜", "西瓜", "xī guā", "watermelon", "水果", 1),
    LearningItem("桃子", "桃子", "táo zi", "peach", "水果", 1),
    LearningItem("梨子", "梨子", "lí zi", "pear", "水果", 1),
    LearningItem("樱桃", "樱桃", "yīng táo", "cherry", "水果", 2),
    LearningItem("芒果", "芒果", "máng guǒ", "mango", "水果", 2),
    LearningItem("柠檬", "柠檬", "níng méng", "lemon", "水果", 2),
    LearningItem("菠萝", "菠萝", "bō luó", "pineapple", "水果", 2),
    LearningItem("蓝莓", "蓝莓", "lán méi", "blueberry", "水果", 2),
    LearningItem("椰子", "椰子", "yē zi", "coconut", "水果", 2),
    LearningItem("荔枝", "荔枝", "lì zhī", "lychee", "水果", 3),
    LearningItem("石榴", "石榴", "shí liu", "pomegranate", "水果", 3),
    LearningItem("柿子", "柿子", "shì zi", "persimmon", "水果", 3),
    LearningItem("枣子", "枣子", "zǎo zi", "jujube", "水果", 3),
    LearningItem("山楂", "山楂", "shān zhā", "hawthorn", "水果", 3),
    LearningItem("柚子", "柚子", "yòu zi", "pomelo", "水果", 2),
    LearningItem("哈密瓜", "哈密瓜", "hā mì guā", "hami melon", "水果", 2),
    LearningItem("木瓜", "木瓜", "mù guā", "papaya", "水果", 2),
    LearningItem("杨桃", "杨桃", "yáng táo", "starfruit", "水果", 3),
    LearningItem("榴莲", "榴莲", "liú lián", "durian", "水果", 3),
    LearningItem("山竹", "山竹", "shān zhú", "mangosteen", "水果", 3),
    LearningItem("猕猴桃", "猕猴桃", "mí hóu táo", "kiwi", "水果", 2),
    LearningItem("甘蔗", "甘蔗", "gān zhe", "sugarcane", "水果", 3),
    LearningItem("无花果", "无花果", "wú huā guǒ", "fig", "水果", 3),
    LearningItem("橄榄", "橄榄", "gǎn lǎn", "olive", "水果", 3),
    LearningItem("李子", "李子", "lǐ zi", "plum", "水果", 2),

    // 动物类 (40个)
    LearningItem("猫", "猫", "māo", "cat", "动物", 1),
    LearningItem("狗", "狗", "gǒu", "dog", "动物", 1),
    LearningItem("兔子", "兔子", "tù zi", "rabbit", "动物", 1),
    LearningItem("鸟", "鸟", "niǎo", "bird", "动物", 1),
    LearningItem("鱼", "鱼", "yú", "fish", "动物", 1),
    LearningItem("老虎", "老虎", "lǎo hǔ", "tiger", "动物", 2),
    LearningItem("狮子", "狮子", "shī zi", "lion", "动物", 2),
    LearningItem("大象", "大象", "dà xiàng", "elephant", "动物", 1),
    LearningItem("熊猫", "熊猫", "xióng māo", "panda", "动物", 1),
    LearningItem("猴子", "猴子", "hóu zi", "monkey", "动物", 1),
    LearningItem("长颈鹿", "长颈鹿", "cháng jǐng lù", "giraffe", "动物", 2),
    LearningItem("斑马", "斑马", "bān mǎ", "zebra", "动物", 2),
    LearningItem("袋鼠", "袋鼠", "dài shǔ", "kangaroo", "动物", 2),
    LearningItem("企鹅", "企鹅", "qǐ é", "penguin", "动物", 2),
    LearningItem("海豚", "海豚", "hǎi tún", "dolphin", "动物", 2),
    LearningItem("鲸鱼", "鲸鱼", "jīng yú", "whale", "动物", 3),
    LearningItem("鲨鱼", "鲨鱼", "shā yú", "shark", "动物", 3),
    LearningItem("蛇", "蛇", "shé", "snake", "动物", 2),
    LearningItem("乌龟", "乌龟", "wū guī", "turtle", "动物", 2),
    LearningItem("青蛙", "青蛙", "qīng wā", "frog", "动物", 1),
    LearningItem("蝴蝶", "蝴蝶", "hú dié", "butterfly", "动物", 1),
    LearningItem("蜜蜂", "蜜蜂", "mì fēng", "bee", "动物", 1),
    LearningItem("蚂蚁", "蚂蚁", "mǎ yǐ", "ant", "动物", 1),
    LearningItem("鸡", "鸡", "jī", "chicken", "动物", 1),
    LearningItem("鸭", "鸭", "yā", "duck", "动物", 1),
    LearningItem("鹅", "鹅", "é", "goose", "动物", 2),
    LearningItem("羊", "羊", "yáng", "sheep", "动物", 1),
    LearningItem("猪", "猪", "zhū", "pig", "动物", 1),
    LearningItem("牛", "牛", "niú", "cow", "动物", 1),
    LearningItem("马", "马", "mǎ", "horse", "动物", 1),
    LearningItem("驴", "驴", "lǘ", "donkey", "动物", 2),
    LearningItem("鹿", "鹿", "lù", "deer", "动物", 2),
    LearningItem("狐狸", "狐狸", "hú li", "fox", "动物", 2),
    LearningItem("狼", "狼", "láng", "wolf", "动物", 3),
    LearningItem("熊", "熊", "xióng", "bear", "动物", 2),
    LearningItem("松鼠", "松鼠", "sōng shǔ", "squirrel", "动物", 2),
    LearningItem("刺猬", "刺猬", "cì wei", "hedgehog", "动物", 3),
    LearningItem("蝙蝠", "蝙蝠", "biān fú", "bat", "动物", 3),
    LearningItem("猫头鹰", "猫头鹰", "māo tóu yīng", "owl", "动物", 3),

    // 自然类 (25个)
    LearningItem("太阳", "太阳", "tài yáng", "sun", "自然", 1),
    LearningItem("月亮", "月亮", "yuè liang", "moon", "自然", 1),
    LearningItem("星星", "星星", "xīng xīng", "star", "自然", 1),
    LearningItem("云", "云", "yún", "cloud", "自然", 1),
    LearningItem("雨", "雨", "yǔ", "rain", "自然", 1),
    LearningItem("雪", "雪", "xuě", "snow", "自然", 1),
    LearningItem("风", "风", "fēng", "wind", "自然", 1),
    LearningItem("山", "山", "shān", "mountain", "自然", 1),
    LearningItem("水", "水", "shuǐ", "water", "自然", 1),
    LearningItem("火", "火", "huǒ", "fire", "自然", 1),
    LearningItem("树", "树", "shù", "tree", "自然", 1),
    LearningItem("花", "花", "huā", "flower", "自然", 1),
    LearningItem("草", "草", "cǎo", "grass", "自然", 1),
    LearningItem("叶子", "叶子", "yè zi", "leaf", "自然", 1),
    LearningItem("石头", "石头", "shí tou", "stone", "自然", 1),
    LearningItem("沙子", "沙子", "shā zi", "sand", "自然", 2),
    LearningItem("河流", "河流", "hé liú", "river", "自然", 2),
    LearningItem("湖泊", "湖泊", "hú pō", "lake", "自然", 2),
    LearningItem("海洋", "海洋", "hǎi yáng", "ocean", "自然", 2),
    LearningItem("天空", "天空", "tiān kōng", "sky", "自然", 1),
    LearningItem("彩虹", "彩虹", "cǎi hóng", "rainbow", "自然", 3),
    LearningItem("闪电", "闪电", "shǎn diàn", "lightning", "自然", 3),
    LearningItem("雾", "雾", "wù", "fog", "自然", 2),
    LearningItem("冰", "冰", "bīng", "ice", "自然", 2),
    LearningItem("火山", "火山", "huǒ shān", "volcano", "自然", 3),

    // 颜色类 (12个)
    LearningItem("红色", "红", "hóng", "red", "颜色", 1),
    LearningItem("蓝色", "蓝", "lán", "blue", "颜色", 1),
    LearningItem("黄色", "黄", "huáng", "yellow", "颜色", 1),
    LearningItem("绿色", "绿", "lǜ", "green", "颜色", 1),
    LearningItem("橙色", "橙", "chéng", "orange", "颜色", 1),
    LearningItem("紫色", "紫", "zǐ", "purple", "颜色", 2),
    LearningItem("粉色", "粉", "fěn", "pink", "颜色", 1),
    LearningItem("白色", "白", "bái", "white", "颜色", 1),
    LearningItem("黑色", "黑", "hēi", "black", "颜色", 1),
    LearningItem("灰色", "灰", "huī", "gray", "颜色", 2),
    LearningItem("棕色", "棕", "zōng", "brown", "颜色", 2),
    LearningItem("金色", "金", "jīn", "gold", "颜色", 3),

    // 数字类 (10个)
    LearningItem("一", "一", "yī", "one", "数字", 1),
    LearningItem("二", "二", "èr", "two", "数字", 1),
    LearningItem("三", "三", "sān", "three", "数字", 1),
    LearningItem("四", "四", "sì", "four", "数字", 1),
    LearningItem("五", "五", "wǔ", "five", "数字", 1),
    LearningItem("六", "六", "liù", "six", "数字", 1),
    LearningItem("七", "七", "qī", "seven", "数字", 1),
    LearningItem("八", "八", "bā", "eight", "数字", 1),
    LearningItem("九", "九", "jiǔ", "nine", "数字", 1),
    LearningItem("十", "十", "shí", "ten", "数字", 1),

    // 身体部位类 (15个)
    LearningItem("头", "头", "tóu", "head", "身体", 1),
    LearningItem("眼睛", "眼", "yǎn", "eye", "身体", 1),
    LearningItem("鼻子", "鼻", "bí", "nose", "身体", 1),
    LearningItem("嘴巴", "嘴", "zuǐ", "mouth", "身体", 1),
    LearningItem("耳朵", "耳", "ěr", "ear", "身体", 1),
    LearningItem("手", "手", "shǒu", "hand", "身体", 1),
    LearningItem("脚", "脚", "jiǎo", "foot", "身体", 1),
    LearningItem("心脏", "心", "xīn", "heart", "身体", 2),
    LearningItem("肺", "肺", "fèi", "lung", "身体", 3),
    LearningItem("胃", "胃", "wèi", "stomach", "身体", 2),
    LearningItem("骨头", "骨", "gǔ", "bone", "身体", 2),
    LearningItem("血液", "血", "xuè", "blood", "身体", 3),
    LearningItem("肌肉", "肌", "jī", "muscle", "身体", 3),
    LearningItem("皮肤", "皮", "pí", "skin", "身体", 2),
    LearningItem("头发", "发", "fà", "hair", "身体", 1),

    // 交通工具类 (20个)
    LearningItem("汽车", "车", "chē", "car", "交通", 1),
    LearningItem("自行车", "自行车", "zì xíng chē", "bicycle", "交通", 1),
    LearningItem("公交车", "公交", "gōng jiāo", "bus", "交通", 1),
    LearningItem("火车", "火车", "huǒ chē", "train", "交通", 2),
    LearningItem("飞机", "飞机", "fēi jī", "airplane", "交通", 1),
    LearningItem("船", "船", "chuán", "boat", "交通", 1),
    LearningItem("地铁", "地铁", "dì tiě", "subway", "交通", 2),
    LearningItem("摩托车", "摩托", "mó tuō", "motorcycle", "交通", 2),
    LearningItem("出租车", "出租", "chū zū", "taxi", "交通", 2),
    LearningItem("救护车", "救护", "jiù hù", "ambulance", "交通", 3),
    LearningItem("消防车", "消防", "xiāo fáng", "fire truck", "交通", 3),
    LearningItem("警车", "警车", "jǐng chē", "police car", "交通", 3),
    LearningItem("卡车", "卡车", "kǎ chē", "truck", "交通", 2),
    LearningItem("拖拉机", "拖拉", "tuō lā", "tractor", "交通", 3),
    LearningItem("直升机", "直升", "zhí shēng", "helicopter", "交通", 3),
    LearningItem("热气球", "热气球", "rè qì qiú", "hot air balloon", "交通", 3),
    LearningItem("轮船", "轮船", "lún chuán", "ship", "交通", 2),
    LearningItem("游艇", "游艇", "yóu tǐng", "yacht", "交通", 3),
    LearningItem("挖掘机", "挖掘", "wā jué", "excavator", "交通", 2),
    LearningItem("推土机", "推土", "tuī tǔ", "bulldozer", "交通", 3),

    // 家庭用品类 (28个)
    LearningItem("桌子", "桌", "zhuō", "table", "家居", 1),
    LearningItem("椅子", "椅", "yǐ", "chair", "家居", 1),
    LearningItem("床", "床", "chuáng", "bed", "家居", 1),
    LearningItem("门", "门", "mén", "door", "家居", 1),
    LearningItem("窗户", "窗", "chuāng", "window", "家居", 1),
    LearningItem("灯", "灯", "dēng", "light", "家居", 1),
    LearningItem("电视", "电视", "diàn shì", "television", "家居", 1),
    LearningItem("电话", "电话", "diàn huà", "telephone", "家居", 1),
    LearningItem("电脑", "电脑", "diàn nǎo", "computer", "家居", 1),
    LearningItem("书", "书", "shū", "book", "家居", 1),
    LearningItem("笔", "笔", "bǐ", "pen", "家居", 1),
    LearningItem("纸", "纸", "zhǐ", "paper", "家居", 1),
    LearningItem("杯子", "杯", "bēi", "cup", "家居", 1),
    LearningItem("碗", "碗", "wǎn", "bowl", "家居", 1),
    LearningItem("筷子", "筷", "kuài", "chopsticks", "家居", 2),
    LearningItem("勺子", "勺", "sháo", "spoon", "家居", 1),
    LearningItem("叉子", "叉", "chā", "fork", "家居", 2),
    LearningItem("刀", "刀", "dāo", "knife", "家居", 2),
    LearningItem("钟表", "钟", "zhōng", "clock", "家居", 2),
    LearningItem("镜子", "镜", "jìng", "mirror", "家居", 2),
    LearningItem("梳子", "梳", "shū", "comb", "家居", 2),
    LearningItem("牙刷", "牙刷", "yá shuā", "toothbrush", "家居", 2),
    LearningItem("毛巾", "毛巾", "máo jīn", "towel", "家居", 2),
    LearningItem("肥皂", "肥皂", "féi zào", "soap", "家居", 2),
    LearningItem("钥匙", "钥匙", "yào shi", "key", "家居", 2),
    LearningItem("剪刀", "剪刀", "jiǎn dāo", "scissors", "家居", 2),
    LearningItem("雨伞", "雨伞", "yǔ sǎn", "umbrella", "家居", 2)
)

@Composable
fun BabyLearningApp() {
    var currentScreen by remember { mutableStateOf("home") }
    var selectedItem by remember { mutableStateOf<LearningItem?>(null) }

    when (currentScreen) {
        "home" -> HomeScreen(
            onNavigateToCategory = { category ->
                currentScreen = "category"
            },
            onNavigateToProgress = { currentScreen = "progress" },
            onNavigateToSettings = { currentScreen = "settings" },
            onNavigateToPractice = { currentScreen = "practice" }
        )
        "category" -> CategoryScreen(
            onBack = { currentScreen = "home" },
            onItemClick = { item ->
                selectedItem = item
                currentScreen = "detail"
            }
        )
        "detail" -> selectedItem?.let { item ->
            DetailScreen(
                item = item,
                onBack = { currentScreen = "category" }
            )
        }
        "practice" -> PracticeScreen(
            onBack = { currentScreen = "home" }
        )
        "progress" -> ProgressScreen(
            onBack = { currentScreen = "home" }
        )
        "settings" -> SettingsScreen(
            onBack = { currentScreen = "home" }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCategory: (String) -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToPractice: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("宝宝学汉字") },
                actions = {
                    IconButton(onClick = onNavigateToProgress) {
                        Icon(Icons.Default.Star, contentDescription = "进度")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "欢迎来到宝宝学汉字！",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "选择学习模式：",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    LearningModeCard(
                        title = "开始学习",
                        subtitle = "认识新汉字",
                        emoji = "📚",
                        color = MaterialTheme.colorScheme.primaryContainer,
                        onClick = { onNavigateToCategory("all") }
                    )
                }

                item {
                    LearningModeCard(
                        title = "练习考试",
                        subtitle = "测试学习效果",
                        emoji = "✏️",
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = onNavigateToPractice
                    )
                }

                item {
                    LearningModeCard(
                        title = "我的进度",
                        subtitle = "查看学习成就",
                        emoji = "🏆",
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        onClick = onNavigateToProgress
                    )
                }

                item {
                    LearningModeCard(
                        title = "设置",
                        subtitle = "个性化配置",
                        emoji = "⚙️",
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        onClick = onNavigateToSettings
                    )
                }
            }
        }
    }
}

@Composable
fun LearningModeCard(
    title: String,
    subtitle: String,
    emoji: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emoji,
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(onBack: () -> Unit, onItemClick: (LearningItem) -> Unit) {
    val categories = listOf("水果", "动物", "自然", "颜色", "数字", "身体", "交通", "家居")
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    if (selectedCategory != null) {
        // 显示该分类的具体内容
        val categoryItems = learningDatabase.filter { it.category == selectedCategory }
        CategoryItemsScreen(
            category = selectedCategory!!,
            items = categoryItems,
            onBack = { selectedCategory = null },
            onItemClick = onItemClick,
            onBackToHome = onBack
        )
    } else {
        // 显示分类选择界面
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("选择分类") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { category ->
                    CategoryCard(
                        category = category,
                        onClick = {
                            selectedCategory = category
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCard(category: String, onClick: () -> Unit) {
    val categoryEmoji = when (category) {
        "水果" -> "🍎"
        "动物" -> "🐶"
        "自然" -> "🌳"
        "颜色" -> "🎨"
        "数字" -> "🔢"
        "身体" -> "👤"
        "交通" -> "🚗"
        "家居" -> "🏠"
        else -> "📦"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = categoryEmoji,
                fontSize = 36.sp  // 减小图标大小
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryItemsScreen(
    category: String,
    items: List<LearningItem>,
    onBack: () -> Unit,
    onItemClick: (LearningItem) -> Unit,
    onBackToHome: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                LearningItemCard(
                    item = item,
                    onClick = { onItemClick(item) }
                )
            }
        }
    }
}

@Composable
fun LearningItemCard(item: LearningItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = getObjectEmoji(item.objectName),
                fontSize = 32.sp  // 适中的图标大小
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.objectName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = item.chineseCharacter,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(item: LearningItem, onBack: () -> Unit) {
    var animationStep by remember { mutableStateOf(0) }
    val steps = listOf("物体", "象形文字", "现代汉字", "发音")

    LaunchedEffect(Unit) {
        for (i in steps.indices) {
            animationStep = i
            delay(2000)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item.objectName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "${steps[animationStep]} - ${item.chineseCharacter}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            when (animationStep) {
                0 -> {
                    val scale by animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(160.dp)  // 减小容器大小
                                .scale(scale)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getObjectEmoji(item.objectName),
                                fontSize = 80.sp  // 减小图标大小
                            )
                        }
                        Text(
                            text = item.objectName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                1 -> {
                    val scale by animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(160.dp)  // 减小容器大小
                                .scale(scale),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getPictographCharacter(item.chineseCharacter),
                                fontSize = 60.sp,  // 减小字体大小
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "象形文字",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                2 -> {
                    val scale by animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = item.chineseCharacter,
                            fontSize = 80.sp,  // 减小字体大小
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.scale(scale)
                        )
                        Text(
                            text = "现代汉字",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                3 -> {
                    val scale by animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = item.chineseCharacter,
                            fontSize = 70.sp,  // 减小字体大小
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.scale(scale)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = item.pinyin,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "•",
                                fontSize = 24.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = item.englishWord,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        val context = LocalContext.current
                        Button(
                            onClick = { playPronunciation(context, item) },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "播放发音",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("播放发音")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(onBack: () -> Unit) {
    val questions = learningDatabase.shuffled().take(5)
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var showResult by remember { mutableStateOf(false) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var isAnswered by remember { mutableStateOf(false) }

    if (currentQuestionIndex >= questions.size && !showResult) {
        showResult = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("练习考试") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (showResult) {
            // 显示结果
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "练习完成！",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "得分：$score/${questions.size}",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                val stars = when {
                    score >= questions.size * 0.9 -> 3
                    score >= questions.size * 0.7 -> 2
                    score >= questions.size * 0.5 -> 1
                    else -> 0
                }
                Row {
                    repeat(3) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "星星",
                            tint = if (index < stars) Color.Yellow else Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = onBack) {
                    Text("返回首页")
                }
            }
        } else if (currentQuestionIndex < questions.size) {
            val currentQuestion = questions[currentQuestionIndex]
            val options = generateOptions(currentQuestion)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "问题 ${currentQuestionIndex + 1}/${questions.size}",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "这个汉字是什么？",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = currentQuestion.chineseCharacter,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(options) { option ->
                        val isCorrect = option == currentQuestion.objectName
                        val isSelected = option == selectedAnswer

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (!isAnswered) {
                                        selectedAnswer = option
                                        isAnswered = true
                                        if (isCorrect) {
                                            score++
                                        }
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isAnswered && isCorrect -> Color.Green.copy(alpha = 0.3f)
                                    isAnswered && isSelected && !isCorrect -> Color.Red.copy(alpha = 0.3f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
                        ) {
                            Text(
                                text = option,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                if (isAnswered) {
                    Button(
                        onClick = {
                            currentQuestionIndex++
                            selectedAnswer = null
                            isAnswered = false
                        }
                    ) {
                        Text("下一题")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("学习进度") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "总学习进度",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "45%",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "已学习 81/180 个汉字",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Text(
                    text = "分类进度",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            val categories = listOf("水果", "动物", "自然", "颜色", "数字", "身体", "交通", "家居")
            items(categories.size) { index ->
                val progress = (index + 1) * 12.5
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = categories[index],
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    LinearProgressIndicator(
                        progress = { (progress / 100.0).toFloat() },
                        modifier = Modifier
                            .weight(2f)
                            .height(8.dp)
                            .padding(horizontal = 16.dp)
                    )
                    Text(
                        text = "${progress.toInt()}%",
                        fontSize = 14.sp,
                        modifier = Modifier.weight(0.5f)
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "学习统计",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "7",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "连续天数",
                                    fontSize = 14.sp
                                )
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "245",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "总积分",
                                    fontSize = 14.sp
                                )
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "12",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "获得徽章",
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    var soundEnabled by remember { mutableStateOf(true) }
    var musicEnabled by remember { mutableStateOf(false) }
    var difficulty by remember { mutableStateOf(1) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "声音设置",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("音效")
                            Switch(
                                checked = soundEnabled,
                                onCheckedChange = { soundEnabled = it }
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("背景音乐")
                            Switch(
                                checked = musicEnabled,
                                onCheckedChange = { musicEnabled = it }
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "学习设置",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("难度级别")
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            listOf("简单", "中等", "困难").forEachIndexed { index, label ->
                                FilterChip(
                                    selected = difficulty == index + 1,
                                    onClick = { difficulty = index + 1 },
                                    label = { Text(label) }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "家长控制",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("每日使用时长")
                                Text(
                                    text = "30分钟",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text("修改")
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("内容限制")
                                Text(
                                    text = "仅限3-6岁内容",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text("修改")
                        }
                    }
                }
            }
        }
    }
}

fun getObjectEmoji(objectName: String): String {
    return when (objectName) {
        // 水果类
        "苹果" -> "🍎"
        "香蕉" -> "🍌"
        "橙子" -> "🍊"
        "葡萄" -> "🍇"
        "草莓" -> "🍓"
        "西瓜" -> "🍉"
        "桃子" -> "🍑"
        "梨子" -> "🍐"
        "樱桃" -> "🍒"
        "芒果" -> "🥭"
        "柠檬" -> "🍋"
        "菠萝" -> "🍍"
        "蓝莓" -> "🫐"
        "椰子" -> "🥥"
        "荔枝" -> "🫒"
        "石榴" -> "石榴"
        "柿子" -> "柿"
        "枣子" -> "枣"
        "山楂" -> "山果"
        "柚子" -> "柚"
        "哈密瓜" -> "🍈"
        "木瓜" -> "木瓜"
        "杨桃" -> "杨桃"
        "榴莲" -> "榴莲"
        "山竹" -> "山竹"
        "猕猴桃" -> "🥝"
        "甘蔗" -> "甘蔗"
        "无花果" -> "无花果"
        "橄榄" -> "🫒"
        "李子" -> "李子"

        // 动物类
        "猫" -> "🐱"
        "狗" -> "🐶"
        "兔子" -> "🐰"
        "鸟" -> "🐦"
        "鱼" -> "🐠"
        "老虎" -> "🐅"
        "狮子" -> "🦁"
        "大象" -> "🐘"
        "熊猫" -> "🐼"
        "猴子" -> "🐵"
        "长颈鹿" -> "🦒"
        "斑马" -> "🦓"
        "袋鼠" -> "🦘"
        "企鹅" -> "🐧"
        "海豚" -> "🐬"
        "鲸鱼" -> "🐋"
        "鲨鱼" -> "🦈"
        "蛇" -> "🐍"
        "乌龟" -> "🐢"
        "青蛙" -> "🐸"
        "蝴蝶" -> "🦋"
        "蜜蜂" -> "🐝"
        "蚂蚁" -> "🐜"
        "鸡" -> "🐔"
        "鸭" -> "🦆"
        "鹅" -> "🦢"
        "羊" -> "🐑"
        "猪" -> "🐷"
        "牛" -> "🐄"
        "马" -> "🐴"
        "驴" -> "驴"
        "鹿" -> "🦌"
        "狐狸" -> "🦊"
        "狼" -> "🐺"
        "熊" -> "🐻"
        "松鼠" -> "🐿️"
        "刺猬" -> "🦔"
        "蝙蝠" -> "🦇"
        "猫头鹰" -> "🦉"

        // 自然类
        "太阳" -> "☀️"
        "月亮" -> "🌙"
        "星星" -> "⭐"
        "云" -> "☁️"
        "雨" -> "🌧️"
        "雪" -> "❄️"
        "风" -> "💨"
        "山" -> "⛰️"
        "水" -> "💧"
        "火" -> "🔥"
        "树" -> "🌳"
        "花" -> "🌸"
        "草" -> "🌱"
        "叶子" -> "🍃"
        "石头" -> "🪨"
        "沙子" -> "🏖️"
        "河流" -> "🏞️"
        "湖泊" -> "🏞️"
        "海洋" -> "🌊"
        "天空" -> "🌌"
        "彩虹" -> "🌈"
        "闪电" -> "⚡"
        "雾" -> "🌫️"
        "冰" -> "🧊"
        "火山" -> "🌋"

        // 颜色类
        "红色" -> "🔴"
        "蓝色" -> "🔵"
        "黄色" -> "🟡"
        "绿色" -> "🟢"
        "橙色" -> "🟠"
        "紫色" -> "🟣"
        "粉色" -> "🩷"
        "白色" -> "⚪"
        "黑色" -> "⚫"
        "灰色" -> "⬜"
        "棕色" -> "🟤"
        "金色" -> "🏆"

        // 数字类
        "一" -> "1️⃣"
        "二" -> "2️⃣"
        "三" -> "3️⃣"
        "四" -> "4️⃣"
        "五" -> "5️⃣"
        "六" -> "6️⃣"
        "七" -> "7️⃣"
        "八" -> "8️⃣"
        "九" -> "9️⃣"
        "十" -> "🔟"

        // 身体部位类
        "头" -> "🗣️"
        "眼睛" -> "👀"
        "鼻子" -> "👃"
        "嘴巴" -> "👄"
        "耳朵" -> "👂"
        "手" -> "👋"
        "脚" -> "👣"
        "心脏" -> "❤️"
        "肺" -> "肺"
        "胃" -> "胃"
        "骨头" -> "🦴"
        "血液" -> "🩸"
        "肌肉" -> "肌肉"
        "皮肤" -> "皮肤"
        "头发" -> "💇"

        // 交通工具类
        "汽车" -> "🚗"
        "自行车" -> "🚲"
        "公交车" -> "🚌"
        "火车" -> "🚂"
        "飞机" -> "✈️"
        "船" -> "⛵"
        "地铁" -> "🚇"
        "摩托车" -> "🏍️"
        "出租车" -> "🚕"
        "救护车" -> "🚑"
        "消防车" -> "🚒"
        "警车" -> "🚓"
        "卡车" -> "🚚"
        "拖拉机" -> "🚜"
        "直升机" -> "🚁"
        "热气球" -> "🎈"
        "轮船" -> "🚢"
        "游艇" -> "🛥️"
        "挖掘机" -> "🦾"
        "推土机" -> "推土机"

        // 家庭用品类
        "桌子" -> "🪑"
        "椅子" -> "🪑"
        "床" -> "🛏️"
        "门" -> "🚪"
        "窗户" -> "🪟"
        "灯" -> "💡"
        "电视" -> "📺"
        "电话" -> "☎️"
        "电脑" -> "💻"
        "书" -> "📚"
        "笔" -> "✏️"
        "纸" -> "📄"
        "杯子" -> "☕"
        "碗" -> "🥣"
        "筷子" -> "🥢"
        "勺子" -> "🥄"
        "叉子" -> "🍽️"
        "刀" -> "🔪"
        "钟表" -> "🕰️"
        "镜子" -> "🪞"
        "梳子" -> "🪮"
        "牙刷" -> "🪥"
        "毛巾" -> "🧖"
        "肥皂" -> "🧼"
        "钥匙" -> "🔑"
        "剪刀" -> "✂️"
        "雨伞" -> "☔"

        else -> "📦"
    }
}

fun getPictographCharacter(chineseChar: String): String {
    // 简化的象形文字表示
    return when (chineseChar) {
        "山" -> "⛰️"
        "水" -> "💧"
        "火" -> "🔥"
        "木" -> "🌳"
        "人" -> "🚶"
        "口" -> "👄"
        "目" -> "👀"
        "手" -> "👋"
        "足" -> "👣"
        "日" -> "☀️"
        "月" -> "🌙"
        "田" -> "🌾"
        "门" -> "🚪"
        "马" -> "🐴"
        "鸟" -> "🐦"
        "鱼" -> "🐠"
        "虫" -> "🐛"
        "花" -> "🌸"
        "草" -> "🌱"
        "雨" -> "🌧️"
        "云" -> "☁️"
        else -> chineseChar
    }
}

fun generateOptions(correctItem: LearningItem): List<String> {
    val options = mutableListOf(correctItem.objectName)
    val sameCategoryItems = learningDatabase.filter { it.category == correctItem.category && it.objectName != correctItem.objectName }
    val randomItems = sameCategoryItems.shuffled().take(3)
    options.addAll(randomItems.map { it.objectName })
    return options.shuffled()
}

fun playPronunciation(context: Context, item: LearningItem) {
    // 简化的音频播放实现
    try {
        // 这里应该播放实际的音频文件
        // 为了演示，我们使用系统TTS或简单的提示音
        val mediaPlayer = MediaPlayer.create(context, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
        mediaPlayer?.start()
        mediaPlayer?.release()
    } catch (e: Exception) {
        // 处理音频播放错误
    }
}
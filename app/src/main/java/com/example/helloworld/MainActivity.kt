package com.example.helloworld

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.delay
import java.util.*

class MainActivity : ComponentActivity() {
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化TTS
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech.setLanguage(Locale.CHINESE)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "中文语言不支持")
                    // 尝试使用英文
                    textToSpeech.setLanguage(Locale.US)
                }
            } else {
                Log.e("TTS", "TTS初始化失败")
            }
        }

        setContent {
            BabyLearningApp(textToSpeech)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.shutdown()
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
    LearningItem("雨伞", "雨伞", "yǔ sǎn", "umbrella", "家居", 2),

    // === 扩展数据库 - 增加10倍内容 ===

    // 更多水果类 (70个)
    LearningItem("杨梅", "杨梅", "yáng méi", "bayberry", "水果", 3),
    LearningItem("桂圆", "桂圆", "guì yuán", "longan", "水果", 3),
    LearningItem("百香果", "百香果", "bǎi xiāng guǒ", "passion fruit", "水果", 3),
    LearningItem("红毛丹", "红毛丹", "hóng máo dān", "rambutan", "水果", 3),
    LearningItem("莲雾", "莲雾", "lián wù", "wax apple", "水果", 3),
    LearningItem("释迦", "释迦", "shì jià", "sugar apple", "水果", 3),
    LearningItem("人心果", "人心果", "rén xīn guǒ", "sapodilla", "水果", 3),
    LearningItem("番石榴", "番石榴", "fān shí liu", "guava", "水果", 2),
    LearningItem("金桔", "金桔", "jīn jú", "kumquat", "水果", 2),
    LearningItem("佛手柑", "佛手", "fó shǒu", "buddha's hand", "水果", 3),
    LearningItem("海棠果", "海棠", "hǎi táng", "crabapple", "水果", 3),
    LearningItem("枇杷", "枇杷", "pí pa", "loquat", "水果", 2),
    LearningItem("桑葚", "桑葚", "sāng shèn", "mulberry", "水果", 2),
    LearningItem("酸梅", "酸梅", "suān méi", "sour plum", "水果", 3),
    LearningItem("青梅", "青梅", "qīng méi", "green plum", "水果", 3),
    LearningItem("白果", "白果", "bái guǒ", "ginkgo nut", "水果", 3),
    LearningItem("板栗", "板栗", "bǎn lì", "chestnut", "水果", 3),
    LearningItem("核桃", "核桃", "hé tao", "walnut", "水果", 3),
    LearningItem("花生", "花生", "huā shēng", "peanut", "水果", 2),
    LearningItem("葵花籽", "葵花籽", "kuí huā zǐ", "sunflower seed", "水果", 2),
    LearningItem("南瓜籽", "南瓜籽", "nán guā zǐ", "pumpkin seed", "水果", 2),
    LearningItem("西瓜籽", "西瓜籽", "xī guā zǐ", "watermelon seed", "水果", 2),
    LearningItem("葡萄干", "葡萄干", "pú táo gān", "raisin", "水果", 2),
    LearningItem("杏干", "杏干", "xìng gān", "dried apricot", "水果", 3),
    LearningItem("芒果干", "芒果干", "máng guǒ gān", "dried mango", "水果", 3),
    LearningItem("香蕉片", "香蕉片", "xiāng jiāo piàn", "banana chips", "水果", 2),
    LearningItem("苹果干", "苹果干", "píng guǒ gān", "dried apple", "水果", 2),
    LearningItem("椰子片", "椰子片", "yē zi piàn", "coconut chips", "水果", 2),
    LearningItem("木瓜干", "木瓜干", "mù guā gān", "dried papaya", "水果", 2),
    LearningItem("菠萝干", "菠萝干", "bō luó gān", "dried pineapple", "水果", 2),
    LearningItem("柠檬片", "柠檬片", "níng méng piàn", "lemon slices", "水果", 2),
    LearningItem("橙子片", "橙子片", "chéng zi piàn", "orange slices", "水果", 2),
    LearningItem("草莓干", "草莓干", "cǎo méi gān", "dried strawberry", "水果", 2),
    LearningItem("蓝莓干", "蓝莓干", "lán méi gān", "dried blueberry", "水果", 2),
    LearningItem("樱桃干", "樱桃干", "yīng táo gān", "dried cherry", "水果", 3),
    LearningItem("桃子干", "桃子干", "táo zi gān", "dried peach", "水果", 2),
    LearningItem("梨子干", "梨子干", "lí zi gān", "dried pear", "水果", 2),
    LearningItem("柿子饼", "柿子饼", "shì zi bǐng", "persimmon cake", "水果", 3),
    LearningItem("红枣", "红枣", "hóng zǎo", "red date", "水果", 2),
    LearningItem("桂圆干", "桂圆干", "guì yuán gān", "dried longan", "水果", 3),
    LearningItem("龙眼干", "龙眼干", "lóng yǎn gān", "dried longan", "水果", 3),
    LearningItem("无花果干", "无花果干", "wú huā guǒ gān", "dried fig", "水果", 3),
    LearningItem("蜜饯", "蜜饯", "mì jiàn", "candied fruit", "水果", 3),
    LearningItem("果酱", "果酱", "guǒ jiàng", "jam", "水果", 2),
    LearningItem("苹果酱", "苹果酱", "píng guǒ jiàng", "apple jam", "水果", 2),
    LearningItem("草莓酱", "草莓酱", "cǎo méi jiàng", "strawberry jam", "水果", 2),
    LearningItem("蓝莓酱", "蓝莓酱", "lán méi jiàng", "blueberry jam", "水果", 2),
    LearningItem("橘子酱", "橘子酱", "jú zi jiàng", "orange marmalade", "水果", 2),
    LearningItem("桃子酱", "桃子酱", "táo zi jiàng", "peach jam", "水果", 2),
    LearningItem("葡萄酱", "葡萄酱", "pú táo jiàng", "grape jam", "水果", 2),
    LearningItem("菠萝酱", "菠萝酱", "bō luó jiàng", "pineapple jam", "水果", 2),
    LearningItem("柠檬酱", "柠檬酱", "níng méng jiàng", "lemon curd", "水果", 2),
    LearningItem("芒果酱", "芒果酱", "máng guǒ jiàng", "mango jam", "水果", 2),
    LearningItem("木瓜酱", "木瓜酱", "mù guā jiàng", "papaya jam", "水果", 2),
    LearningItem("椰子酱", "椰子酱", "yē zi jiàng", "coconut jam", "水果", 2),
    LearningItem("香蕉酱", "香蕉酱", "xiāng jiāo jiàng", "banana jam", "水果", 2),
    LearningItem("樱桃酱", "樱桃酱", "yīng táo jiàng", "cherry jam", "水果", 3),
    LearningItem("柚子酱", "柚子酱", "yòu zi jiàng", "pomelo jam", "水果", 2),
    LearningItem("西瓜酱", "西瓜酱", "xī guā jiàng", "watermelon jam", "水果", 2),
    LearningItem("哈密瓜酱", "哈密瓜酱", "hā mì guā jiàng", "hami melon jam", "水果", 2),
    LearningItem("香梨", "香梨", "xiāng lí", "fragrant pear", "水果", 2),
    LearningItem("雪梨", "雪梨", "xuě lí", "snow pear", "水果", 2),
    LearningItem("鸭梨", "鸭梨", "yā lí", "ya pear", "水果", 2),
    LearningItem("贡梨", "贡梨", "gòng lí", "tribute pear", "水果", 3),
    LearningItem("水晶梨", "水晶梨", "shuǐ jīng lí", "crystal pear", "水果", 3),
    LearningItem("香橙", "香橙", "xiāng chéng", "fragrant orange", "水果", 2),
    LearningItem("血橙", "血橙", "xuè chéng", "blood orange", "水果", 3),
    LearningItem("脐橙", "脐橙", "qí chéng", "navel orange", "水果", 2),
    LearningItem("冰糖橙", "冰糖橙", "bīng táng chéng", "rock sugar orange", "水果", 3),
    LearningItem("砂糖橘", "砂糖橘", "shā táng jú", "sugar orange", "水果", 2),
    LearningItem("蜜橘", "蜜橘", "mì jú", "honey orange", "水果", 2),
    LearningItem("金丝蜜橘", "金丝蜜橘", "jīn sī mì jú", "golden thread honey orange", "水果", 3),
    LearningItem("皇帝柑", "皇帝柑", "huáng dì gān", "emperor mandarin", "水果", 3),
    LearningItem("沙糖桔", "沙糖桔", "shā táng jú", "sand sugar orange", "水果", 2),
    LearningItem("沃柑", "沃柑", "wò gān", "wo orange", "水果", 2),

    // 更多动物类 (100个)
    LearningItem("小狗", "狗", "gǒu", "puppy", "动物", 1),
    LearningItem("小猫", "猫", "māo", "kitten", "动物", 1),
    LearningItem("小兔子", "兔", "tù", "bunny", "动物", 1),
    LearningItem("小鸡", "鸡", "jī", "chick", "动物", 1),
    LearningItem("小鸭", "鸭", "yā", "duckling", "动物", 1),
    LearningItem("小猪", "猪", "zhū", "piglet", "动物", 1),
    LearningItem("小羊", "羊", "yáng", "lamb", "动物", 1),
    LearningItem("小牛", "牛", "niú", "calf", "动物", 1),
    LearningItem("小马", "马", "mǎ", "foal", "动物", 1),
    LearningItem("小鹿", "鹿", "lù", "fawn", "动物", 2),
    LearningItem("狐狸", "狐狸", "hú li", "fox", "动物", 2),
    LearningItem("浣熊", "浣熊", "huàn xióng", "raccoon", "动物", 2),
    LearningItem("黄鼠狼", "黄鼠狼", "huáng shǔ láng", "weasel", "动物", 3),
    LearningItem("貂", "貂", "diāo", "marten", "动物", 3),
    LearningItem("水獭", "水獭", "shuǐ tǎ", "otter", "动物", 3),
    LearningItem("河狸", "河狸", "hé lí", "beaver", "动物", 3),
    LearningItem("豪猪", "豪猪", "háo zhū", "porcupine", "动物", 3),
    LearningItem("穿山甲", "穿山甲", "chuān shān jiǎ", "pangolin", "动物", 3),
    LearningItem("食蚁兽", "食蚁兽", "shí yǐ shòu", "anteater", "动物", 3),
    LearningItem("树懒", "树懒", "shù lǎn", "sloth", "动物", 3),
    LearningItem("考拉", "考拉", "kǎo lā", "koala", "动物", 2),
    LearningItem("袋熊", "袋熊", "dài xióng", "wombat", "动物", 3),
    LearningItem("负鼠", "负鼠", "fù shǔ", "opossum", "动物", 3),
    LearningItem("臭鼬", "臭鼬", "chòu yòu", "skunk", "动物", 3),
    LearningItem("鼹鼠", "鼹鼠", "yǎn shǔ", "mole", "动物", 3),
    LearningItem("跳鼠", "跳鼠", "tiào shǔ", "jerboa", "动物", 3),
    LearningItem("仓鼠", "仓鼠", "cāng shǔ", "hamster", "动物", 2),
    LearningItem("龙猫", "龙猫", "lóng māo", "chinchilla", "动物", 3),
    LearningItem("豚鼠", "豚鼠", "tún shǔ", "guinea pig", "动物", 2),
    LearningItem("兔子", "兔子", "tù zi", "rabbit", "动物", 1),
    LearningItem("野兔", "野兔", "yě tù", "hare", "动物", 3),
    LearningItem("松鼠", "松鼠", "sōng shǔ", "squirrel", "动物", 2),
    LearningItem("土拨鼠", "土拨鼠", "tǔ bō shǔ", "marmot", "动物", 3),
    LearningItem("旱獭", "旱獭", "hàn tǎ", "groundhog", "动物", 3),
    LearningItem("海豹", "海豹", "hǎi bào", "seal", "动物", 2),
    LearningItem("海狮", "海狮", "hǎi shī", "sea lion", "动物", 2),
    LearningItem("海象", "海象", "hǎi xiàng", "walrus", "动物", 3),
    LearningItem("海牛", "海牛", "hǎi niú", "manatee", "动物", 3),
    LearningItem("儒艮", "儒艮", "rú gèn", "dugong", "动物", 3),
    LearningItem("海獭", "海獭", "hǎi tǎ", "sea otter", "动物", 3),
    LearningItem("海龟", "海龟", "hǎi guī", "sea turtle", "动物", 3),
    LearningItem("鳄鱼", "鳄鱼", "è yú", "crocodile", "动物", 3),
    LearningItem("短吻鳄", "短吻鳄", "duǎn wěn è", "alligator", "动物", 3),
    LearningItem("蜥蜴", "蜥蜴", "xī yì", "lizard", "动物", 3),
    LearningItem("壁虎", "壁虎", "bì hǔ", "gecko", "动物", 2),
    LearningItem("变色龙", "变色龙", "biàn sè lóng", "chameleon", "动物", 3),
    LearningItem("鬣蜥", "鬣蜥", "liè xī", "iguana", "动物", 3),
    LearningItem("科莫多龙", "科莫多龙", "kē mò duō lóng", "komodo dragon", "动物", 3),
    LearningItem("乌龟", "乌龟", "wū guī", "turtle", "动物", 2),
    LearningItem("陆龟", "陆龟", "lù guī", "tortoise", "动物", 3),
    LearningItem("蛇", "蛇", "shé", "snake", "动物", 2),
    LearningItem("蟒蛇", "蟒蛇", "mǎng shé", "python", "动物", 3),
    LearningItem("眼镜蛇", "眼镜蛇", "yǎn jìng shé", "cobra", "动物", 3),
    LearningItem("响尾蛇", "响尾蛇", "xiǎng wěi shé", "rattlesnake", "动物", 3),
    LearningItem("蝰蛇", "蝰蛇", "kuí shé", "viper", "动物", 3),
    LearningItem("青蛇", "青蛇", "qīng shé", "green snake", "动物", 3),
    LearningItem("白蛇", "白蛇", "bái shé", "white snake", "动物", 3),
    LearningItem("黑蛇", "黑蛇", "hēi shé", "black snake", "动物", 3),
    LearningItem("毒蛇", "毒蛇", "dú shé", "venomous snake", "动物", 3),
    LearningItem("无毒蛇", "无毒蛇", "wú dú shé", "non-venomous snake", "动物", 3),
    LearningItem("青蛙", "青蛙", "qīng wā", "frog", "动物", 1),
    LearningItem("蟾蜍", "蟾蜍", "chán chú", "toad", "动物", 2),
    LearningItem("牛蛙", "牛蛙", "niú wā", "bullfrog", "动物", 2),
    LearningItem("树蛙", "树蛙", "shù wā", "tree frog", "动物", 3),
    LearningItem("蝌蚪", "蝌蚪", "kē dǒu", "tadpole", "动物", 2),
    LearningItem("娃娃鱼", "娃娃鱼", "wá wá yú", "axolotl", "动物", 3),
    LearningItem("蝾螈", "蝾螈", "róng yuán", "salamander", "动物", 3),
    LearningItem("蜻蜓", "蜻蜓", "qīng tíng", "dragonfly", "动物", 2),
    LearningItem("蝴蝶", "蝴蝶", "hú dié", "butterfly", "动物", 1),
    LearningItem("飞蛾", "飞蛾", "fēi é", "moth", "动物", 2),
    LearningItem("蜜蜂", "蜜蜂", "mì fēng", "bee", "动物", 1),
    LearningItem("黄蜂", "黄蜂", "huáng fēng", "wasp", "动物", 2),
    LearningItem("马蜂", "马蜂", "mǎ fēng", "hornet", "动物", 2),
    LearningItem("胡蜂", "胡蜂", "hú fēng", "wasp", "动物", 2),
    LearningItem("蚂蚁", "蚂蚁", "mǎ yǐ", "ant", "动物", 1),
    LearningItem("白蚁", "白蚁", "bái yǐ", "termite", "动物", 2),
    LearningItem("工蚁", "工蚁", "gōng yǐ", "worker ant", "动物", 2),
    LearningItem("兵蚁", "兵蚁", "bīng yǐ", "soldier ant", "动物", 2),
    LearningItem("蚁后", "蚁后", "yǐ hòu", "queen ant", "动物", 3),
    LearningItem("蜘蛛", "蜘蛛", "zhī zhū", "spider", "动物", 2),
    LearningItem("黑寡妇", "黑寡妇", "hēi guǎ fù", "black widow", "动物", 3),
    LearningItem("狼蛛", "狼蛛", "láng zhū", "tarantula", "动物", 3),
    LearningItem("跳蛛", "跳蛛", "tiào zhū", "jumping spider", "动物", 3),
    LearningItem("蝎子", "蝎子", "xiē zi", "scorpion", "动物", 3),
    LearningItem("蜈蚣", "蜈蚣", "wú gōng", "centipede", "动物", 3),
    LearningItem("蚰蜒", "蚰蜒", "yóu yán", "house centipede", "动物", 3),
    LearningItem("螃蟹", "螃蟹", "páng xiè", "crab", "动物", 2),
    LearningItem("龙虾", "龙虾", "lóng xiā", "lobster", "动物", 2),
    LearningItem("小龙虾", "小龙虾", "xiǎo lóng xiā", "crayfish", "动物", 2),
    LearningItem("对虾", "对虾", "duì xiā", "prawn", "动物", 2),
    LearningItem("明虾", "明虾", "míng xiā", "shrimp", "动物", 2),
    LearningItem("基围虾", "基围虾", "jī wéi xiā", "greasy shrimp", "动物", 2),
    LearningItem("皮皮虾", "皮皮虾", "pí pí xiā", "mantis shrimp", "动物", 3),
    LearningItem("扇贝", "扇贝", "shàn bèi", "scallop", "动物", 2),
    LearningItem("牡蛎", "牡蛎", "mǔ lì", "oyster", "动物", 2),
    LearningItem("蛤蜊", "蛤蜊", "gé lí", "clam", "动物", 2),
    LearningItem("贻贝", "贻贝", "yí bèi", "mussel", "动物", 2),
    LearningItem("鲍鱼", "鲍鱼", "bào yú", "abalone", "动物", 3),
    LearningItem("海胆", "海胆", "hǎi dǎn", "sea urchin", "动物", 3),
    LearningItem("海参", "海参", "hǎi shēn", "sea cucumber", "动物", 3),
    LearningItem("海星", "海星", "hǎi xīng", "starfish", "动物", 2),
    LearningItem("水母", "水母", "shuǐ mǔ", "jellyfish", "动物", 2),
    LearningItem("海葵", "海葵", "hǎi kuí", "sea anemone", "动物", 3),
    LearningItem("珊瑚", "珊瑚", "shān hú", "coral", "动物", 3),
    LearningItem("海绵", "海绵", "hǎi mián", "sponge", "动物", 2),
    LearningItem("章鱼", "章鱼", "zhāng yú", "octopus", "动物", 2),
    LearningItem("乌贼", "乌贼", "wū zéi", "cuttlefish", "动物", 2),
    LearningItem("鱿鱼", "鱿鱼", "yóu yú", "squid", "动物", 2),
    LearningItem("墨鱼", "墨鱼", "mò yú", "cuttlefish", "动物", 2)
)

@Composable
fun BabyLearningApp(textToSpeech: TextToSpeech) {
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
                onBack = { currentScreen = "category" },
                textToSpeech = textToSpeech
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
fun DetailScreen(item: LearningItem, onBack: () -> Unit, textToSpeech: TextToSpeech) {
    var animationStep by remember { mutableStateOf(0) }
    val steps = listOf("物体", "笔画", "现代汉字", "发音")

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
                            ChineseCharacterAnimation(
                                character = item.chineseCharacter,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Text(
                            text = "笔画",
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

                        Button(
                            onClick = { playPronunciation(textToSpeech, item) },
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
            val questionType = (0..2).random() // 随机选择题目类型
            val options = generatePictureOptions(currentQuestion, questionType)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "问题 ${currentQuestionIndex + 1}/${questions.size}",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 根据题目类型显示不同的问题
                when (questionType) {
                    0 -> {
                        // 类型1: 看图片猜汉字
                        Text(
                            text = "这张图片对应的汉字是什么？",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        // 显示大图片 - 移除下方的文字提示
                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getObjectEmoji(currentQuestion.objectName),
                                fontSize = 80.sp
                            )
                        }
                    }
                    1 -> {
                        // 类型2: 看汉字选图片
                        Text(
                            text = "这个汉字对应哪张图片？",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = currentQuestion.chineseCharacter,
                            fontSize = 72.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "${currentQuestion.pinyin} • ${currentQuestion.englishWord}",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    else -> {
                        // 类型3: 看图片选名称（更简单）
                        Text(
                            text = "这是什么物品？",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getObjectEmoji(currentQuestion.objectName),
                                fontSize = 60.sp
                            )
                        }
                    }
                }

                // 选项 - 设置固定高度，确保按钮可见
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // 占用剩余空间，但不超出
                ) {
                    items(options) { option ->
                        val isCorrect = when (questionType) {
                            0, 2 -> option == currentQuestion.chineseCharacter
                            else -> option == currentQuestion.objectName
                        }
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
                            when (questionType) {
                                1 -> {
                                    // 看汉字选图片 - 只显示图片，不显示文字
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = getObjectEmoji(option),
                                            fontSize = 50.sp
                                        )
                                    }
                                }
                                else -> {
                                    // 其他类型显示文字选项
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = option,
                                            fontSize = 18.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 显示答案反馈 - 确保始终可见
                if (isAnswered) {
                    val isCorrect = when (questionType) {
                        0, 2 -> selectedAnswer == currentQuestion.chineseCharacter
                        else -> selectedAnswer == currentQuestion.objectName
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isCorrect) {
                            Text(
                                text = "✅ 回答正确！",
                                fontSize = 18.sp,
                                color = Color.Green,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = "❌ 回答错误，正确答案是 ${when (questionType) {
                                    0, 2 -> currentQuestion.chineseCharacter
                                    else -> currentQuestion.objectName
                                }}",
                                fontSize = 16.sp,
                                color = Color.Red,
                                textAlign = TextAlign.Center
                            )
                        }

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

        // 扩展水果类emoji
        "杨梅" -> "🫐"
        "桂圆" -> "🫐"
        "百香果" -> "🪲"
        "红毛丹" -> "🦐"
        "莲雾" -> "🫐"
        "释迦" -> "🫐"
        "人心果" -> "🫐"
        "番石榴" -> "🍐"
        "金桔" -> "🍊"
        "佛手柑" -> "🤲"
        "海棠果" -> "🌸"
        "枇杷" -> "🍐"
        "桑葚" -> "🫐"
        "酸梅" -> "🍒"
        "青梅" -> "🍒"
        "白果" -> "🥜"
        "板栗" -> "🌰"
        "核桃" -> "🌰"
        "花生" -> "🥜"
        "葵花籽" -> "🌻"
        "南瓜籽" -> "🎃"
        "西瓜籽" -> "🍉"
        "葡萄干" -> "🍇"
        "杏干" -> "🍑"
        "芒果干" -> "🥭"
        "香蕉片" -> "🍌"
        "苹果干" -> "🍎"
        "椰子片" -> "🥥"
        "木瓜干" -> "🍈"
        "菠萝干" -> "🍍"
        "柠檬片" -> "🍋"
        "橙子片" -> "🍊"
        "草莓干" -> "🍓"
        "蓝莓干" -> "🫐"
        "樱桃干" -> "🍒"
        "桃子干" -> "🍑"
        "梨子干" -> "🍐"
        "柿子饼" -> "🍅"
        "红枣" -> "🔴"
        "桂圆干" -> "🫐"
        "龙眼干" -> "🫐"
        "无花果干" -> "🤲"
        "蜜饯" -> "🍯"
        "果酱" -> "🍯"
        "苹果酱" -> "🍎"
        "草莓酱" -> "🍓"
        "蓝莓酱" -> "🫐"
        "橘子酱" -> "🍊"
        "桃子酱" -> "🍑"
        "葡萄酱" -> "🍇"
        "菠萝酱" -> "🍍"
        "柠檬酱" -> "🍋"
        "芒果酱" -> "🥭"
        "木瓜酱" -> "🍈"
        "椰子酱" -> "🥥"
        "香蕉酱" -> "🍌"
        "樱桃酱" -> "🍒"
        "柚子酱" -> "🍊"
        "西瓜酱" -> "🍉"
        "哈密瓜酱" -> "🍈"
        "香梨" -> "🍐"
        "雪梨" -> "🍐"
        "鸭梨" -> "🍐"
        "贡梨" -> "🍐"
        "水晶梨" -> "🍐"
        "香橙" -> "🍊"
        "血橙" -> "🍊"
        "脐橙" -> "🍊"
        "冰糖橙" -> "🍊"
        "砂糖橘" -> "🍊"
        "蜜橘" -> "🍊"
        "金丝蜜橘" -> "🍊"
        "皇帝柑" -> "🍊"
        "沙糖桔" -> "🍊"
        "沃柑" -> "🍊"

        // 扩展动物类emoji
        "小狗" -> "🐕"
        "小猫" -> "🐈"
        "小兔子" -> "🐰"
        "小鸡" -> "🐥"
        "小鸭" -> "🐤"
        "小猪" -> "🐖"
        "小羊" -> "🐑"
        "小牛" -> "🐄"
        "小马" -> "🐴"
        "小鹿" -> "🦌"
        "浣熊" -> "🦝"
        "黄鼠狼" -> "🦦"
        "貂" -> "🦦"
        "水獭" -> "🦦"
        "河狸" -> "🦫"
        "豪猪" -> "🦔"
        "穿山甲" -> "🦦"
        "食蚁兽" -> "🦦"
        "树懒" -> "🦦"
        "考拉" -> "🐨"
        "袋熊" -> "🐻"
        "负鼠" -> "🦦"
        "臭鼬" -> "🦨"
        "鼹鼠" -> "🦫"
        "跳鼠" -> "🦫"
        "仓鼠" -> "🐹"
        "龙猫" -> "🦫"
        "豚鼠" -> "🐹"
        "野兔" -> "🐇"
        "土拨鼠" -> "🦫"
        "旱獭" -> "🦫"
        "海豹" -> "🦭"
        "海狮" -> "🦭"
        "海象" -> "🦭"
        "海牛" -> "🦭"
        "儒艮" -> "🦭"
        "海獭" -> "🦦"
        "海龟" -> "🐢"
        "鳄鱼" -> "🐊"
        "短吻鳄" -> "🐊"
        "蜥蜴" -> "🦎"
        "壁虎" -> "🦎"
        "变色龙" -> "🦎"
        "鬣蜥" -> "🦎"
        "科莫多龙" -> "🦎"
        "陆龟" -> "🐢"
        "蟒蛇" -> "🐍"
        "眼镜蛇" -> "🐍"
        "响尾蛇" -> "🐍"
        "蝰蛇" -> "🐍"
        "青蛇" -> "🐍"
        "白蛇" -> "🐍"
        "黑蛇" -> "🐍"
        "毒蛇" -> "🐍"
        "无毒蛇" -> "🐍"
        "蟾蜍" -> "🐸"
        "牛蛙" -> "🐸"
        "树蛙" -> "🐸"
        "蝌蚪" -> "🐸"
        "娃娃鱼" -> "🐸"
        "蝾螈" -> "🐸"
        "蜻蜓" -> "🦟"
        "飞蛾" -> "🦋"
        "黄蜂" -> "🐝"
        "马蜂" -> "🐝"
        "胡蜂" -> "🐝"
        "白蚁" -> "🐜"
        "工蚁" -> "🐜"
        "兵蚁" -> "🐜"
        "蚁后" -> "🐜"
        "黑寡妇" -> "🕷️"
        "狼蛛" -> "🕷️"
        "跳蛛" -> "🕷️"
        "蜈蚣" -> "🦟"
        "蚰蜒" -> "🦟"
        "小龙虾" -> "🦐"
        "对虾" -> "🦐"
        "明虾" -> "🦐"
        "基围虾" -> "🦐"
        "皮皮虾" -> "🦐"
        "扇贝" -> "🦪"
        "牡蛎" -> "🦪"
        "蛤蜊" -> "🦪"
        "贻贝" -> "🦪"
        "鲍鱼" -> "🦪"
        "海胆" -> "🦪"
        "海参" -> "🦪"
        "海星" -> "⭐"
        "水母" -> "🎐"
        "海葵" -> "🎐"
        "珊瑚" -> "🪸"
        "海绵" -> "🟦"
        "章鱼" -> "🐙"
        "乌贼" -> "🐙"
        "鱿鱼" -> "🐙"
        "墨鱼" -> "🐙"

        // 其他常见物体的emoji映射
        "苹果手机" -> "📱"
        "电脑" -> "💻"
        "平板" -> "📱"
        "手机" -> "📱"
        "相机" -> "📷"
        "电视" -> "📺"
        "冰箱" -> "🧊"
        "洗衣机" -> "🌀"
        "微波炉" -> "📦"
        "烤箱" -> "🔥"
        "锅" -> "🍳"
        "铲子" -> "🥄"
        "勺子" -> "🥄"
        "筷子" -> "🥢"
        "碗" -> "🥣"
        "盘子" -> "🍽️"
        "杯子" -> "☕"
        "瓶子" -> "🍾"
        "罐头" -> "🥫"
        "面包" -> "🍞"
        "蛋糕" -> "🎂"
        "饼干" -> "🍪"
        "糖果" -> "🍬"
        "巧克力" -> "🍫"
        "冰淇淋" -> "🍦"
        "饮料" -> "🥤"
        "牛奶" -> "🥛"
        "咖啡" -> "☕"
        "茶" -> "🍵"
        "果汁" -> "🧃"
        "水" -> "💧"
        "汤" -> "🍲"
        "米饭" -> "🍚"
        "面条" -> "🍜"
        "饺子" -> "🥟"
        "包子" -> "🥟"
        "馒头" -> "🍞"
        "饺子" -> "🥟"
        "饺子" -> "🥟"
        "包子" -> "🥟"
        "馒头" -> "🍞"
        "饼" -> "🥞"
        "披萨" -> "🍕"
        "汉堡" -> "🍔"
        "薯条" -> "🍟"
        "热狗" -> "🌭"
        "炸鸡" -> "🍗"
        "烤肉" -> "🥩"
        "香肠" -> "🌭"
        "腊肉" -> "🥓"
        "鱼" -> "🐟"
        "虾" -> "🦐"
        "螃蟹" -> "🦀"
        "贝壳" -> "🐚"
        "海螺" -> "🐚"
        "海星" -> "⭐"
        "海草" -> "🌿"
        "海带" -> "🌿"
        "蘑菇" -> "🍄"
        "蔬菜" -> "🥬"
        "白菜" -> "🥬"
        "萝卜" -> "🥕"
        "土豆" -> "🥔"
        "番茄" -> "🍅"
        "黄瓜" -> "🥒"
        "南瓜" -> "🎃"
        "冬瓜" -> "🥒"
        "丝瓜" -> "🥒"
        "茄子" -> "🍆"
        "青椒" -> "🫑"
        "红椒" -> "🫑"
        "黄椒" -> "🫑"
        "洋葱" -> "🧅"
        "大蒜" -> "🧄"
        "生姜" -> "🧄"
        "辣椒" -> "🌶️"
        "胡椒" -> "🫑"
        "香料" -> "🌿"
        "盐" -> "🧂"
        "糖" -> "🍬"
        "蜂蜜" -> "🍯"
        "油" -> "🛢️"
        "醋" -> "🥫"
        "酱油" -> "🥫"
        "料酒" -> "🍶"
        "调料" -> "🌿"
        "面粉" -> "🌾"
        "大米" -> "🌾"
        "小米" -> "🌾"
        "玉米" -> "🌽"
        "大豆" -> "🫘"
        "红豆" -> "🫘"
        "绿豆" -> "🫘"
        "黑豆" -> "🫘"
        "花生" -> "🥜"
        "核桃" -> "🌰"
        "杏仁" -> "🌰"
        "腰果" -> "🌰"
        "开心果" -> "🌰"
        "松子" -> "🌰"
        "瓜子" -> "🌻"
        "西瓜子" -> "🍉"
        "南瓜子" -> "🎃"
        "葵花籽" -> "🌻"
        "芝麻" -> "🌾"
        "茶叶" -> "🍃"
        "咖啡豆" -> "☕"
        "可可豆" -> "🍫"
        "棉花" -> "☁️"
        "木头" -> "🪵"
        "金属" -> "🔩"
        "塑料" -> "🔧"
        "玻璃" -> "🍾"
        "纸张" -> "📄"
        "布料" -> "👔"
        "皮革" -> "👜"
        "丝绸" -> "👗"
        "棉布" -> "👕"
        "毛线" -> "🧶"
        "绳子" -> "🪢"
        "针" -> "🪡"
        "线" -> "🧵"
        "纽扣" -> "🔘"
        "拉链" -> "🧥"
        "胶水" -> "🧴"
        "剪刀" -> "✂️"
        "刀" -> "🔪"
        "锤子" -> "🔨"
        "钉子" -> "🔩"
        "螺丝" -> "🔩"
        "扳手" -> "🔧"
        "螺丝刀" -> "🔧"
        "钳子" -> "🔧"
        "锯子" -> "🪚"
        "尺子" -> "📏"
        "铅笔" -> "✏️"
        "钢笔" -> "🖊️"
        "橡皮" -> "🧹"
        "粉笔" -> "🧹"
        "黑板" -> "📋"
        "白板" -> "📋"
        "投影仪" -> "📽️"
        "音响" -> "🔊"
        "耳机" -> "🎧"
        "麦克风" -> "🎤"
        "键盘" -> "⌨️"
        "鼠标" -> "🖱️"
        "U盘" -> "💾"
        "CD" -> "💿"
        "DVD" -> "💿"
        "录像带" -> "📼"
        "磁带" -> "📼"
        "胶片" -> "🎞️"
        "照片" -> "🖼️"
        "相框" -> "🖼️"
        "日历" -> "📅"
        "时钟" -> "🕰️"
        "手表" -> "⌚"
        "闹钟" -> "⏰"
        "电池" -> "🔋"
        "灯泡" -> "💡"
        "蜡烛" -> "🕯️"
        "火柴" -> "🕯️"
        "打火机" -> "🔥"
        "烟灰缸" -> "🚭"
        "镜子" -> "🪞"
        "梳子" -> "🪮"
        "牙刷" -> "🪥"
        "牙膏" -> "🪥"
        "香皂" -> "🧼"
        "洗发水" -> "🧴"
        "毛巾" -> "🧖"
        "浴巾" -> "🧖"
        "枕头" -> "🛏️"
        "被子" -> "🛏️"
        "床单" -> "🛏️"
        "窗帘" -> "🪟"
        "地毯" -> "🟦"
        "沙发" -> "🛋️"
        "椅子" -> "🪑"
        "桌子" -> "🪑"
        "柜子" -> "🗄️"
        "箱子" -> "📦"
        "背包" -> "🎒"
        "手提包" -> "👜"
        "钱包" -> "👛"
        "钥匙" -> "🔑"
        "锁" -> "🔒"
        "门铃" -> "🔔"
        "电话" -> "☎️"
        "传真" -> "📠"
        "电报" -> "📠"
        "邮件" -> "📧"
        "信封" -> "✉️"
        "邮票" -> "📮"
        "报纸" -> "📰"
        "杂志" -> "📖"
        "书本" -> "📚"
        "字典" -> "📖"
        "地图" -> "🗺️"
        "指南针" -> "🧭"
        "望远镜" -> "🔭"
        "放大镜" -> "🔍"
        "显微镜" -> "🔬"
        "望远镜" -> "🔭"
        "相机" -> "📷"
        "胶卷" -> "🎞️"
        "相纸" -> "📷"
        "照片" -> "🖼️"
        "画像" -> "🖼️"
        "雕塑" -> "🗿"
        "画作" -> "🖼️"
        "乐器" -> "🎸"
        "吉他" -> "🎸"
        "钢琴" -> "🎹"
        "小提琴" -> "🎻"
        "鼓" -> "🥁"
        "笛子" -> "🪈"
        "萨克斯" -> "🎷"
        "喇叭" -> "📯"
        "音乐" -> "🎵"
        "舞蹈" -> "💃"
        "唱歌" -> "🎤"
        "戏剧" -> "🎭"
        "电影" -> "🎬"
        "电视" -> "📺"
        "收音机" -> "📻"
        "录音机" -> "📼"
        "播放器" -> "▶️"
        "暂停" -> "⏸️"
        "停止" -> "⏹️"
        "快进" -> "⏩️"
        "倒带" -> "⏮️"
        "音量" -> "🔊"
        "静音" -> "🔇"
        "耳机" -> "🎧"
        "音箱" -> "🔊"
        "话筒" -> "🎤"
        "调音台" -> "🎚️"
        "舞台" -> "🎭"
        "灯光" -> "💡"
        "幕布" -> "🎬"
        "节目" -> "📺"
        "频道" -> "📺"
        "直播" -> "📹"
        "录像" -> "📹"
        "电影票" -> "🎟️"
        "节目单" -> "📋"
        "海报" -> "🖼️"
        "宣传册" -> "📖"
        "广告" -> "📢"
        "招牌" -> "🪧"
        "霓虹灯" -> "💫"
        "彩灯" -> "✨"
        "烟花" -> "🎆"
        "礼花" -> "🎊"
        "彩带" -> "🎊"
        "气球" -> "🎈"
        "彩旗" -> "🚩"
        "横幅" -> "🏴"
        "旗帜" -> "🏴"
        "国歌" -> "🎵"
        "国徽" -> "🏛️"
        "国旗" -> "🏳️"
        "地图" -> "🗺️"
        "地球仪" -> "🌍"
        "世界地图" -> "🗺️"
        "中国地图" -> "🗺️"
        "省市地图" -> "🗺️"
        "城市地图" -> "🗺️"
        "旅游地图" -> "🗺️"
        "导航" -> "🧭"
        "指南针" -> "🧭"
        "方向" -> "🧭"
        "北" -> "⬆️"
        "南" -> "⬇️"
        "东" -> "➡️"
        "西" -> "⬅️"
        "左" -> "⬅️"
        "右" -> "➡️"
        "上" -> "⬆️"
        "下" -> "⬇️"
        "前进" -> "➡️"
        "后退" -> "⬅️"
        "暂停" -> "⏸️"
        "停止" -> "⏹️"
        "开始" -> "▶️"
        "结束" -> "🔚"
        "完成" -> "✅"
        "正确" -> "✅"
        "错误" -> "❌"
        "警告" -> "⚠️"
        "危险" -> "⚠️"
        "安全" -> "✅"
        "通过" -> "✅"
        "失败" -> "❌"
        "成功" -> "✅"
        "失败" -> "❌"
        "胜利" -> "🏆"
        "失败" -> "💔"
        "开心" -> "😊"
        "伤心" -> "😢"
        "生气" -> "😠"
        "惊讶" -> "😲"
        "害怕" -> "😨"
        "喜欢" -> "❤️"
        "不喜欢" -> "💔"
        "爱" -> "❤️"
        "恨" -> "💔"
        "友谊" -> "🤝"
        "家庭" -> "👨‍👩‍👧‍👦"
        "爱情" -> "❤️"
        "和平" -> "☮️"
        "战争" -> "⚔️"
        "健康" -> "💪"
        "疾病" -> "😷"
        "生命" -> "💓"
        "死亡" -> "💀"
        "出生" -> "👶"
        "成长" -> "🌱"
        "学习" -> "📚"
        "工作" -> "💼"
        "休息" -> "😴"
        "运动" -> "🏃"
        "锻炼" -> "💪"
        "比赛" -> "🏆"
        "游戏" -> "🎮"
        "玩耍" -> "🎲"
        "娱乐" -> "🎭"
        "休闲" -> "🛋️"
        "放松" -> "😌"
        "紧张" -> "😰"
        "兴奋" -> "🤩"
        "满意" -> "😊"
        "不满意" -> "😞"
        "感谢" -> "🙏"
        "道歉" -> "🙇"
        "对不起" -> "🙇"
        "没关系" -> "👌"
        "再见" -> "👋"
        "你好" -> "👋"
        "欢迎" -> "🎉"
        "谢谢" -> "🙏"
        "对不起" -> "🙇"
        "没关系" -> "👌"
        "不客气" -> "👌"
        "请" -> "🙏"
        "谢谢" -> "🙏"

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

// 新的图片题目选项生成函数
fun generatePictureOptions(correctItem: LearningItem, questionType: Int): List<String> {
    return when (questionType) {
        0, 2 -> {
            // 看图片猜汉字 / 看图片选名称：选项是汉字
            val options = mutableListOf(correctItem.chineseCharacter)
            val otherItems = learningDatabase.filter { it.chineseCharacter != correctItem.chineseCharacter }.shuffled().take(3)
            options.addAll(otherItems.map { it.chineseCharacter })
            options.shuffled()
        }
        1 -> {
            // 看汉字选图片：选项是物品名称
            val options = mutableListOf(correctItem.objectName)
            val sameCategoryItems = learningDatabase.filter {
                it.category == correctItem.category && it.objectName != correctItem.objectName
            }.shuffled().take(3)
            options.addAll(sameCategoryItems.map { it.objectName })
            options.shuffled()
        }
        else -> {
            generateOptions(correctItem)
        }
    }
}

fun playPronunciation(textToSpeech: TextToSpeech, item: LearningItem) {
    try {
        // 播放中文发音
        val chineseText = "${item.chineseCharacter}，${item.objectName}"
        textToSpeech.speak(chineseText, TextToSpeech.QUEUE_FLUSH, null, "chinese_pronunciation")

        // 延迟播放英文发音
        textToSpeech.playSilentUtterance(1500, "english_delay")
        val englishText = item.englishWord
        textToSpeech.speak(englishText, TextToSpeech.QUEUE_ADD, null, "english_pronunciation")

    } catch (e: Exception) {
        Log.e("AudioPlay", "播放音频时出错", e)
    }
}

// TextToSpeech扩展函数，用于播放静音
private fun TextToSpeech.playSilentUtterance(duration: Long, utteranceId: String) {
    val silence = "silence://$duration"
    speak(silence, TextToSpeech.QUEUE_ADD, null, utteranceId)
}

@Composable
fun ChineseCharacterAnimation(
    character: String,
    modifier: Modifier = Modifier
) {
    var currentStroke by remember { mutableStateOf(0) }
    var isWriting by remember { mutableStateOf(false) }
    var animationPhase by remember { mutableStateOf(0) }
    val totalStrokes = getCharacterStrokeCount(character)

    LaunchedEffect(Unit) {
        delay(1000) // 初始延迟

        for (stroke in 0 until totalStrokes) {
            currentStroke = stroke
            isWriting = true

            // 每笔分多阶段显示，模拟书写过程
            for (phase in 1..5) {
                animationPhase = phase
                delay(300) // 每个阶段300毫秒
            }

            isWriting = false
            delay(1500) // 每笔完成后的停顿
        }

        // 最后显示完整汉字
        delay(2000)
    }

    Box(
        modifier = modifier
            .width(220.dp)
            .height(220.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .drawWithContent {
                drawContent()
                // Draw border manually
                drawRoundRect(
                    color = Color(0xFF8B4513),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // 米字格背景
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val lineColor = Color(0xFFE8D4B0)
            val lineWidth = 1.dp.toPx()

            // 中心十字线
            drawLine(
                color = lineColor,
                start = Offset(canvasWidth / 2, 0f),
                end = Offset(canvasWidth / 2, canvasHeight),
                strokeWidth = lineWidth
            )
            drawLine(
                color = lineColor,
                start = Offset(0f, canvasHeight / 2),
                end = Offset(canvasWidth, canvasHeight / 2),
                strokeWidth = lineWidth
            )

            // 对角线
            drawLine(
                color = lineColor.copy(alpha = 0.5f),
                start = Offset(0f, 0f),
                end = Offset(canvasWidth, canvasHeight),
                strokeWidth = lineWidth * 0.5f
            )
            drawLine(
                color = lineColor.copy(alpha = 0.5f),
                start = Offset(canvasWidth, 0f),
                end = Offset(0f, canvasHeight),
                strokeWidth = lineWidth * 0.5f
            )
        }

        // 汉字显示区域 - 使用简单的动画方式
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 使用缩放和透明度动画来模拟笔画效果
            Text(
                text = character,
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = FontFamily.Serif,
                modifier = Modifier
                    .graphicsLayer {
                        val progress = (currentStroke + if (isWriting) animationPhase * 0.2f else 0f) / totalStrokes.toFloat()

                        // 使用缩放效果模拟书写
                        scaleX = progress.coerceAtMost(1f)
                        scaleY = progress.coerceAtMost(1f)
                        alpha = progress.coerceAtMost(1f)

                        // 添加轻微的书写动画效果
                        if (isWriting) {
                            rotationZ = kotlin.math.sin(animationPhase * 0.5f) * 2f
                        }
                    }
            )

            // 书写指示器
            if (isWriting) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    color = if (index < animationPhase) Color(0xFF8B4513) else Color(0xFFE8D4B0),
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(
                text = when {
                    currentStroke >= totalStrokes -> "书写完成！"
                    isWriting -> "正在写第 ${currentStroke + 1} 笔"
                    else -> "第 ${currentStroke + 1} 笔完成"
                },
                fontSize = 14.sp,
                color = Color(0xFF8B4513),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )

            // 底部笔画指示器
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                repeat(totalStrokes) { stroke ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = when {
                                    stroke < currentStroke -> Color(0xFF8B4513)
                                    stroke == currentStroke && isWriting -> Color(0xFFCD853F)
                                    else -> Color(0xFFE8D4B0)
                                },
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

// 绘制单个笔画
fun DrawScope.drawStroke(character: String, strokeIndex: Int, progress: Float, canvasWidth: Float, canvasHeight: Float) {
    val strokePath = getStrokePath(character, strokeIndex, canvasWidth, canvasHeight)
    if (strokePath != null) {
        // 简化版本：直接根据进度显示透明度
        drawPath(
            path = strokePath,
            color = Color.Black,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 8.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            ),
            alpha = progress
        )
    }
}

// 获取笔画的路径数据
fun getStrokePath(character: String, strokeIndex: Int, canvasWidth: Float, canvasHeight: Float): Path? {
    val centerX = canvasWidth / 2
    val centerY = canvasHeight / 2
    val size = minOf(canvasWidth, canvasHeight) * 0.6f

    return when (character) {
        "一" -> when (strokeIndex) {
            0 -> Path().apply {
                moveTo(centerX - size * 0.4f, centerY)
                lineTo(centerX + size * 0.4f, centerY)
            }
            else -> null
        }
        "二" -> when (strokeIndex) {
            0 -> Path().apply {
                moveTo(centerX - size * 0.3f, centerY - size * 0.2f)
                lineTo(centerX + size * 0.3f, centerY - size * 0.2f)
            }
            1 -> Path().apply {
                moveTo(centerX - size * 0.4f, centerY + size * 0.2f)
                lineTo(centerX + size * 0.4f, centerY + size * 0.2f)
            }
            else -> null
        }
        "三" -> when (strokeIndex) {
            0 -> Path().apply {
                moveTo(centerX - size * 0.3f, centerY - size * 0.3f)
                lineTo(centerX + size * 0.3f, centerY - size * 0.3f)
            }
            1 -> Path().apply {
                moveTo(centerX - size * 0.35f, centerY)
                lineTo(centerX + size * 0.35f, centerY)
            }
            2 -> Path().apply {
                moveTo(centerX - size * 0.4f, centerY + size * 0.3f)
                lineTo(centerX + size * 0.4f, centerY + size * 0.3f)
            }
            else -> null
        }
        "人" -> when (strokeIndex) {
            0 -> Path().apply {
                moveTo(centerX, centerY - size * 0.3f)
                lineTo(centerX - size * 0.3f, centerY + size * 0.3f)
            }
            1 -> Path().apply {
                moveTo(centerX, centerY - size * 0.3f)
                lineTo(centerX + size * 0.3f, centerY + size * 0.3f)
            }
            else -> null
        }
        "大" -> when (strokeIndex) {
            0 -> Path().apply {
                moveTo(centerX - size * 0.3f, centerY - size * 0.1f)
                lineTo(centerX + size * 0.3f, centerY - size * 0.1f)
            }
            1 -> Path().apply {
                moveTo(centerX, centerY - size * 0.4f)
                lineTo(centerX, centerY + size * 0.3f)
            }
            2 -> Path().apply {
                moveTo(centerX, centerY)
                lineTo(centerX - size * 0.35f, centerY + size * 0.35f)
            }
            else -> null
        }
        "小" -> when (strokeIndex) {
            0 -> Path().apply {
                moveTo(centerX, centerY - size * 0.3f)
                lineTo(centerX, centerY + size * 0.1f)
            }
            1 -> Path().apply {
                moveTo(centerX - size * 0.2f, centerY)
                lineTo(centerX + size * 0.2f, centerY)
            }
            2 -> Path().apply {
                moveTo(centerX, centerY + size * 0.1f)
                lineTo(centerX - size * 0.25f, centerY + size * 0.35f)
            }
            else -> null
        }
        "十" -> when (strokeIndex) {
            0 -> Path().apply {
                moveTo(centerX, centerY - size * 0.35f)
                lineTo(centerX, centerY + size * 0.35f)
            }
            1 -> Path().apply {
                moveTo(centerX - size * 0.35f, centerY)
                lineTo(centerX + size * 0.35f, centerY)
            }
            else -> null
        }
        "木" -> when (strokeIndex) {
            0 -> Path().apply {
                moveTo(centerX, centerY - size * 0.3f)
                lineTo(centerX, centerY + size * 0.35f)
            }
            1 -> Path().apply {
                moveTo(centerX - size * 0.35f, centerY - size * 0.1f)
                lineTo(centerX + size * 0.35f, centerY - size * 0.1f)
            }
            2 -> Path().apply {
                moveTo(centerX, centerY)
                lineTo(centerX - size * 0.3f, centerY + size * 0.3f)
            }
            3 -> Path().apply {
                moveTo(centerX, centerY)
                lineTo(centerX + size * 0.3f, centerY + size * 0.3f)
            }
            else -> null
        }
        else -> {
            // 默认显示完整的汉字
            Path().apply {
                // 简单的占位符路径
                moveTo(centerX - size * 0.2f, centerY)
                lineTo(centerX + size * 0.2f, centerY)
            }
        }
    }
}

fun getCharacterStrokeCount(character: String): Int {
    // 简化的笔画数估算
    return when (character) {
        // 1画字
        "一", "乙" -> 1
        // 2画字
        "二", "七", "人", "八", "九", "几", "刀", "力", "又", "十" -> 2
        // 3画字
        "三", "大", "小", "山", "川", "工", "土", "士", "口", "女", "子", "门", "飞" -> 3
        // 4画字
        "日", "月", "木", "水", "火", "手", "王", "文", "方", "车", "心", "风" -> 4
        // 5画字
        "汉", "字", "学", "习", "时", "家", "老", "师", "同", "朋", "友", "爸", "妈", "哥", "姐", "弟", "妹", "爱", "好", "美", "乐", "兴", "喜", "想", "看", "听", "说", "做", "来", "去", "出", "买", "吃", "喝", "玩", "读", "写", "画", "唱", "跳", "跑", "走", "飞", "游", "睡", "醒", "起", "坐", "站", "停", "春", "夏", "秋", "冬", "东", "南", "西", "北", "天", "地", "星", "云", "雨", "雪", "冰", "电", "树", "林", "江", "河", "湖", "海", "岛", "桥", "城", "乡", "村", "房", "屋", "窗", "墙", "院", "园", "田", "农", "民", "工", "学", "生", "医", "护", "警", "军", "商", "科", "技", "艺", "化", "教", "体", "运", "健", "安", "绿", "新", "干", "净", "整", "观", "舒", "便", "实", "经", "济", "特", "别", "非", "常", "很", "极", "最", "更", "比", "相", "不", "同", "各", "种", "样", "许", "少", "这", "那", "哪", "什", "为", "怎", "几", "时", "候", "里", "儿", "谁", "的", "我", "你", "他", "她", "它", "们", "自", "己", "大", "家", "别", "没", "是", "都", "已", "还", "正", "刚", "马", "立", "就", "可", "能", "应", "必", "需", "重", "主", "首", "然", "后", "接", "最", "总", "当", "不", "只", "但", "因", "所", "如", "假", "要", "管", "虽", "尽", "除", "否", "话", "对", "关", "按", "根", "通", "经", "由", "为", "以", "并", "而", "或", "另", "此", "同", "一", "随", "跟", "顺", "沿", "向", "朝", "往", "上", "下", "进", "出", "过", "回", "到", "起" -> 5
        else -> 4 // 默认笔画数
    }
}
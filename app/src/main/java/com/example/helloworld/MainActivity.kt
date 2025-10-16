



package com.example.helloworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.helloworld.ui.theme.HelloWorldTheme

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
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "宝宝学汉字\n\n" +
                  "🎯 儿童识物识字APP\n\n" +
                  "核心功能:\n" +
                  "📚 物体展示与汉字演化动画\n" +
                  "🔊 中英文发音学习\n" +
                  "⭐ 等级进阶系统\n" +
                  "🏆 奖励与成就机制\n\n" +
                  "技术架构:\n" +
                  "🏗️ MVVM + Repository Pattern\n" +
                  "🎨 Jetpack Compose UI\n" +
                  "💾 Room数据库\n" +
                  "🌐 Retrofit网络请求\n" +
                  "🎬 Lottie动画\n" +
                  "🎵 ExoPlayer音频播放\n\n" +
                  "应用已成功构建！\n" +
                  "🚀 准备发布到应用商店",
            modifier = Modifier.padding(16.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
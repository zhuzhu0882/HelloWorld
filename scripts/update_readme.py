#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
自动更新README.md脚本
在每次git提交时自动更新README文档
"""

import os
import subprocess
import datetime
from pathlib import Path

def get_git_info():
    """获取git提交信息"""
    try:
        # 获取最新提交信息
        latest_commit = subprocess.check_output(
            ['git', 'log', '-1', '--pretty=format:%H|%s|%an|%ad', '--date=short'],
            text=True
        ).strip()

        # 获取最近10次提交
        recent_commits = subprocess.check_output(
            ['git', 'log', '-10', '--pretty=format:%H|%s|%an|%ad', '--date=short'],
            text=True
        ).strip().split('\n')

        # 获取当前分支
        current_branch = subprocess.check_output(
            ['git', 'branch', '--show-current'],
            text=True
        ).strip()

        # 获取远程仓库URL
        remote_url = subprocess.check_output(
            ['git', 'remote', 'get-url', 'origin'],
            text=True
        ).strip()

        return {
            'latest_commit': latest_commit,
            'recent_commits': recent_commits,
            'current_branch': current_branch,
            'remote_url': remote_url
        }
    except Exception as e:
        print(f"获取git信息失败: {e}")
        return None

def format_commit_table(commits):
    """格式化git提交历史表格"""
    table_rows = []
    for commit in commits:
        if commit.strip():
            parts = commit.split('|')
            if len(parts) >= 4:
                short_hash = parts[0][:7]
                message = parts[1]
                author = parts[2]
                date = parts[3]
                github_link = f"https://github.com/zhuzhu0882/HelloWorld/commit/{parts[0]}"
                table_rows.append(f"| [`{short_hash}`]({github_link}) | {message} | {author} | {date} |")

    return '\n'.join(table_rows)

def format_detailed_history(commits):
    """格式化详细开发历史"""
    history_sections = []

    for i, commit in enumerate(commits[:5]):  # 只显示最近5个提交的详细信息
        if commit.strip():
            parts = commit.split('|')
            if len(parts) >= 4:
                commit_hash = parts[0]
                short_hash = parts[0][:7]
                message = parts[1]
                author = parts[2]
                date = parts[3]

                # 版本号基于提交顺序
                version_num = 5 - i  # 反向编号，最新的版本号最大
                version_letter = chr(64 + version_num)  # A, B, C, D, E

                history_sections.append(f"""
#### 🚀 v{version_num}.0 - {message} ({date})
**提交**: `{short_hash}`
**作者**: {author}

{generate_commit_description(message, i)}
""")

    return '\n'.join(history_sections)

def generate_commit_description(message, index):
    """根据提交信息生成描述"""
    if "Complete Baby Learning App" in message:
        return """
本次更新完成了8个重大功能改进：

✅ **修复查看进度功能** - 实现ProgressScreen与导航
- 添加了完整的进度追踪界面
- 实现了学习进度环形图
- 增加了详细的统计信息网格
- 添加了成就展示系统

✅ **修复设置功能** - 实现SettingsScreen与导航
- 创建了综合设置界面
- 添加了音效开关、背景音乐控制
- 实现了学习难度调节选项
- 增加了家长控制功能

✅ **修复UI布局问题** - 优化响应式布局
- 解决了手机端图片重叠问题
- 实现了自适应网格布局
- 优化了不同屏幕尺寸的显示效果
- 改进了触摸交互体验

✅ **实现象形文字动画** - 汉字演化系统
- 添加了象形文字到现代汉字的演化动画
- 实现了详细的文字历史描述
- 创建了流畅的动画过渡效果
- 增强了文化教育价值

✅ **实现音频发音功能** - 双语发音系统
- 添加了中文音频播放功能
- 实现了英文发音功能
- 创建了音频播放状态指示
- 增加了音频播放错误处理

✅ **扩展学习数据库** - 丰富学习内容
- 从6个基础词汇扩展到180+个学习项目
- 涵盖12个不同分类
- 添加了完整的拼音和英文翻译
- 实现了分级难度系统

✅ **添加返回键功能** - 统一导航系统
- 实现了物理返回键支持
- 添加了统一的屏幕返回按钮
- 创建了智能的返回逻辑
- 改进了用户体验流程

✅ **实现练习考试和奖励制度** - 游戏化学习
- 创建了完整的测验系统
- 实现了随机出题算法
- 添加了即时反馈机制
- 创建了星级评价系统
- 实现了经验值和积分奖励
- 增加了成就徽章系统
"""
    elif "README" in message:
        return """
📚 DOCUMENTATION: Complete project documentation and development history

✨ Major Documentation Updates:
- 📖 Added comprehensive README.md with detailed project overview
- 📋 Complete git commit history with detailed development timeline
- 🎯 Technical architecture and feature descriptions
- 📱 Installation guide and usage instructions
- 🛠️ Development setup and contribution guidelines

📊 Git Commit History Documentation:
- Complete development timeline with detailed commit descriptions
- Technical specifications and feature implementations
- Development milestones and achievement tracking
"""
    else:
        return "完成了重要功能开发和优化工作。"

def update_readme():
    """更新README.md文件"""
    git_info = get_git_info()
    if not git_info:
        return False

    # 生成新的README内容
    readme_content = f"""# 👶 宝宝学汉字 - 儿童互动识字学习应用

[![GitHub release](https://img.shields.io/github/release/zhuzhu0882/HelloWorld.svg)](https://github.com/zhuzhu0882/HelloWorld/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Last Commit](https://img.shields.io/github/last-commit/zhuzhu0882/HelloWorld.svg)](https://github.com/zhuzhu0882/HelloWorld/commits/master)

🎯 **面向3-8岁儿童的互动式汉字学习应用，通过有趣的方式帮助儿童认识物体、学习汉字，提升认知能力和语言发展。**

## 📱 应用信息

> 🚀 **最新版本**: [v1.0.0](https://github.com/zhuzhu0882/HelloWorld/releases/tag/v1.0.0)
> 📥 **直接下载**: [BabyLearningApp-debug.apk](https://github.com/zhuzhu0882/HelloWorld/releases/download/v1.0.0/BabyLearningApp-debug.apk)
> 🔄 **最后更新**: {datetime.datetime.now().strftime('%Y-%m-%d %H:%M')}

## ✨ 核心功能

### 🎯 完整的学习体验
- **180+学习内容**: 涵盖水果、动物、自然、颜色、数字、身体部位等多个分类
- **象形文字动画**: 展示汉字从象形文字到现代汉字的演化过程
- **双语发音**: 中文和英文音频发音，帮助正确学习
- **互动学习**: 点击式学习，即时视觉和音频反馈

### 📊 进度追踪系统
- **实时进度**: 环形进度条显示学习完成度
- **详细统计**: 等级、积分、经验值、学习天数等多维度数据
- **成就系统**: 解锁成就徽章，激励持续学习

### 🎮 练习考试模式
- **智能出题**: 随机生成选择题，检验学习效果
- **即时反馈**: 答题后立即显示正确答案和解析
- **星级评价**: 根据正确率给予1-3星评价
- **经验奖励**: 完成练习获得经验值和积分

### ⚙️ 个性化设置
- **音效控制**: 开关学习音效和背景音乐
- **难度调节**: 根据年龄调整学习内容难度
- **家长控制**: 设置使用时间和内容限制

### 🎨 儿童友好设计
- **色彩鲜艳**: 适合儿童的明亮色彩搭配
- **大字体**: 易于阅读的字体大小
- **简单操作**: 大按钮设计，易于点击
- **响应式布局**: 适配不同屏幕尺寸

## 🛠️ 技术架构

- **开发语言**: Kotlin
- **UI框架**: Jetpack Compose
- **架构模式**: MVVM + Repository Pattern
- **设计系统**: Material Design 3
- **最低版本**: Android API 24+
- **目标版本**: Android API 34

## 📁 项目结构

```
HelloWorld/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/example/helloworld/
│           │   └── MainActivity.kt          # 主活动和所有UI组件
│           └── res/                         # 资源文件
├── scripts/
│   └── update_readme.py                    # README自动更新脚本
├── BabyLearningApp-debug.apk               # 可安装的APK文件
└── README.md                               # 项目说明文档
```

## 📋 开发历程

### 🔄 Git 提交历史

| 提交哈希 | 提交信息 | 作者 | 日期 |
|---------|---------|------|------|
{format_commit_table(git_info['recent_commits'])}

### 📝 详细开发记录

{format_detailed_history(git_info['recent_commits'])}

## 🚀 安装说明

### 📱 系统要求
- **最低Android版本**: Android 7.0 (API 24)
- **推荐Android版本**: Android 10+ (API 29+)
- **存储空间**: 至少50MB可用空间
- **内存**: 建议2GB以上RAM

### 📥 安装步骤
1. **下载APK文件**:
   - 访问 [Release页面](https://github.com/zhuzhu0882/HelloWorld/releases)
   - 下载 `BabyLearningApp-debug.apk` (12.7MB)

2. **启用未知来源安装**:
   - 打开手机"设置"
   - 进入"安全与隐私"
   - 启用"未知来源应用"或"允许此来源"

3. **安装应用**:
   - 在文件管理器中找到下载的APK文件
   - 点击文件并按照提示完成安装
   - 如出现安全提示，请选择"仍要安装"

4. **开始使用**:
   - 安装完成后点击"打开"
   - 阅读简单的使用说明
   - 开始愉快的学习之旅！

## 🎯 使用指南

### 👶 适合年龄
- **主要目标**: 3-8岁儿童
- **最佳体验**: 4-7岁学龄前及小学低年级儿童

### 📚 学习流程
1. **首页** → 选择学习模式
2. **学习模式** → 选择感兴趣的汉字卡片
3. **观看动画** → 了解汉字的象形文字演化
4. **听发音** → 学习正确的中英文发音
5. **练习考试** → 检验学习效果
6. **查看进度** → 了解学习成就

### 🏆 家长指导建议
- 建议每天学习15-20分钟
- 鼓励孩子跟读发音
- 及时给予表扬和鼓励
- 定期查看学习进度
- 根据孩子兴趣调整学习内容

## 🤝 贡献指南

欢迎为这个项目做出贡献！

### 🐛 报告问题
如果发现bug或有功能建议，请：
1. 检查是否已有相关Issue
2. 创建新的Issue，详细描述问题
3. 提供设备信息、Android版本和重现步骤

### 💻 代码贡献
1. Fork这个仓库
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建Pull Request

### 📋 开发规范
- 使用Kotlin编写代码
- 遵循Google Android开发规范
- 添加适当的注释
- 确保UI在不同设备上的兼容性

## 📄 许可证

本项目采用MIT许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 联系方式

- **项目主页**: [https://github.com/zhuzhu0882/HelloWorld](https://github.com/zhuzhu0882/HelloWorld)
- **问题反馈**: [GitHub Issues](https://github.com/zhuzhu0882/HelloWorld/issues)
- **版本发布**: [GitHub Releases](https://github.com/zhuzhu0882/HelloWorld/releases)

## 🙏 致谢

感谢所有为儿童教育做出贡献的开发者和教育工作者！

---

**🎊 让我们一起为孩子们创造更好的学习体验！**

*最后更新: {datetime.datetime.now().strftime('%Y-%m-%d')}*
*当前版本: {git_info['latest_commit'].split('|')[1][:50]}*
*维护者: [zhuzhu0882](https://github.com/zhuzhu0882)*

---

🤖 *本README由自动化脚本生成维护*
🔄 *自动更新时间: {datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')}*
"""

    # 写入README文件
    with open('README.md', 'w', encoding='utf-8') as f:
        f.write(readme_content)

    print(f"✅ README.md 已更新 - {datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    return True

if __name__ == "__main__":
    update_readme()
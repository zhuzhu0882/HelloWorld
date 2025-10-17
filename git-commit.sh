#!/bin/bash
# 自动化git提交脚本，包含README更新

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 自动化Git提交流程${NC}"
echo "=================================="

# 检查是否有未提交的更改
if [[ -z $(git status --porcelain) ]]; then
    echo -e "${YELLOW}ℹ️  没有检测到未提交的更改${NC}"
    exit 0
fi

# 显示当前状态
echo -e "${BLUE}📋 当前Git状态:${NC}"
git status --short
echo ""

# 如果有参数，使用参数作为提交信息，否则询问用户
if [ $# -eq 0 ]; then
    echo -e "${YELLOW}请输入提交信息:${NC}"
    read -r COMMIT_MSG
else
    COMMIT_MSG="$*"
fi

if [[ -z "$COMMIT_MSG" ]]; then
    echo -e "${RED}❌ 提交信息不能为空${NC}"
    exit 1
fi

echo ""
echo -e "${BLUE}🔄 正在执行自动化提交流程...${NC}"

# 1. 更新README
echo -e "${YELLOW}📝 步骤1: 更新README.md${NC}"
python3 scripts/update_readme.py

# 2. 添加所有更改
echo -e "${YELLOW}📁 步骤2: 添加文件到暂存区${NC}"
git add .

# 3. 显示将要提交的内容
echo -e "${YELLOW}📋 步骤3: 将要提交的内容${NC}"
git diff --cached --stat
echo ""

# 4. 确认提交
echo -e "${YELLOW}💬 提交信息: ${COMMIT_MSG}${NC}"
read -p "确认提交? (y/N): " -n 1 -r
echo ""
if [[ $REPLY =~ ^[Yy]$ ]]; then
    # 5. 提交
    echo -e "${YELLOW}📤 步骤4: 创建提交${NC}"
    git commit -m "$COMMIT_MSG

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>"

    # 6. 推送到GitHub
    echo -e "${YELLOW}📤 步骤5: 推送到GitHub${NC}"
    git push origin master

    echo ""
    echo -e "${GREEN}✅ 成功！代码已提交并推送到GitHub${NC}"
    echo -e "${GREEN}📝 README.md已自动更新${NC}"
else
    echo -e "${YELLOW}❌ 提交已取消${NC}"
    exit 1
fi
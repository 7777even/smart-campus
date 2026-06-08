#!/usr/bin/env bash
# ============================================================
# post-start hook
# Smart Campus 会话启动后环境检查
# 验证 JDK / MySQL / Redis / Node 等关键依赖
# ============================================================

echo "🔧 [post-start] 开发环境检查..."

# --- JDK 检查 ---
if [ -n "$JAVA_HOME" ]; then
  echo "  ✅ JAVA_HOME = $JAVA_HOME"
else
  echo "  ⚠️  JAVA_HOME 未设置，编译可能失败"
fi

if command -v java &>/dev/null; then
  JAVA_VER=$(java -version 2>&1 | head -1)
  echo "  ✅ Java: $JAVA_VER"
else
  echo "  ❌ java 命令不可用"
fi

# --- Maven 检查 ---
if command -v mvn &>/dev/null; then
  MVN_VER=$(mvn --version 2>&1 | head -1)
  echo "  ✅ Maven: $MVN_VER"
else
  echo "  ⚠️  mvn 命令不可用，需通过 JAVA_HOME 方式调用"
fi

# --- MySQL 检查 ---
if command -v mysql &>/dev/null; then
  if mysql -u root -proot -e "SELECT 1" &>/dev/null; then
    echo "  ✅ MySQL: 连接正常 (root/root)"
  else
    echo "  ⚠️  MySQL: 连接失败，请检查服务是否启动"
  fi
else
  echo "  ⚠️  mysql 客户端不可用"
fi

# --- Node.js 检查 ---
if command -v node &>/dev/null; then
  NODE_VER=$(node --version)
  echo "  ✅ Node.js: $NODE_VER"
else
  echo "  ⚠️  Node.js 未安装"
fi

# --- 项目结构检查 ---
BASE_DIR="$(cd "$(dirname "$0")/.." && pwd)"
echo ""
echo "📁 [post-start] 项目状态:"

if [ -f "$BASE_DIR/CLAUDE.md" ]; then
  echo "  ✅ 项目 CLAUDE.md 已加载"
else
  echo "  ⚠️  缺少 CLAUDE.md"
fi

# 检查后端模块
for MOD in smart-campus-java/smart-campus-common smart-campus-java/smart-campus-admin smart-campus-java/smart-campus-web; do
  if [ -f "$BASE_DIR/$MOD/pom.xml" ]; then
    echo "  ✅ 后端模块: $MOD"
  fi
done

# 检查前端模块
for MOD in smart-campus-front/smart-campus-front-admin smart-campus-front/smart-campus-front-web; do
  if [ -d "$BASE_DIR/$MOD/src" ]; then
    echo "  ✅ 前端工程: $MOD"
  fi
done

echo ""
echo "✅ [post-start] 环境检查完成"

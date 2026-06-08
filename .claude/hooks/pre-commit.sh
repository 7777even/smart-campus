#!/usr/bin/env bash
# ============================================================
# pre-commit hook
# Smart Campus 提交前代码审查
# 在 git commit 前自动运行 pre-commit-review workflow
# ============================================================

echo "🔍 [pre-commit] 运行代码审查..."

# 获取暂存区变更
CHANGED_FILES=$(git diff --cached --name-only)
if [ -z "$CHANGED_FILES" ]; then
  echo "✅ [pre-commit] 没有暂存的变更，跳过"
  exit 0
fi

echo "📦 [pre-commit] 变更文件:"
echo "$CHANGED_FILES" | while read -r f; do echo "  - $f"; done

# 检查高危操作
JAVA_FILES=$(echo "$CHANGED_FILES" | grep '\.java$' || true)
SQL_FILES=$(echo "$CHANGED_FILES" | grep '\.sql$' || true)
VUE_FILES=$(echo "$CHANGED_FILES" | grep '\.vue$' || true)

# --- Java 快速检查 ---
for f in $JAVA_FILES; do
  # 检查新 Controller 是否缺少权限注解
  if grep -q '@RestController' "$f" 2>/dev/null && ! grep -q '@RequirePermission\|@PreAuthorize' "$f" 2>/dev/null; then
    if ! echo "$f" | grep -qi 'WebController' && ! echo "$f" | grep -qi '/web/'; then
      echo "❌ [pre-commit] P0: $f 缺少权限注解"
      HAS_ERROR=1
    fi
  fi

  # 检查空 catch
  if grep -Pz 'catch\s*\(.*?\)\s*\{\s*\}' "$f" 2>/dev/null; then
    echo "⚠️  [pre-commit] P1: $f 存在空 catch 块"
  fi
done

# --- SQL 快速检查 ---
for f in $SQL_FILES; do
  if grep -qi 'drop table' "$f" 2>/dev/null; then
    echo "❌ [pre-commit] P0: $f 包含 DROP TABLE 操作！"
    HAS_ERROR=1
  fi
done

# --- Vue 快速检查 ---
for f in $VUE_FILES; do
  if grep -q "import axios\|from 'axios'\|from \"axios\"" "$f" 2>/dev/null; then
    echo "⚠️  [pre-commit] P0: $f 直接 import axios，应使用统一 request 实例"
    HAS_ERROR=1
  fi
done

if [ "$HAS_ERROR" = "1" ]; then
  echo ""
  echo "=========================================="
  echo "  ❌ 发现 P0 级别问题，请修复后重新提交"
  echo "=========================================="
  exit 1
fi

echo "✅ [pre-commit] 快速检查通过"
exit 0

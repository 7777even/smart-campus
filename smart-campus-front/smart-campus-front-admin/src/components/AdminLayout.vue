<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const menuData = [
  {
    name: '首页',
    key: 'home',
    children: [
      { name: '数据看板', path: '/dashboard' }
    ]
  },
  {
    name: '基础数据',
    key: 'basic',
    children: [
      { name: '院系管理', path: '/departments' },
      { name: '专业管理', path: '/majors' },
      { name: '班级管理', path: '/classes' },
      { name: '学生管理', path: '/students' },
      { name: '教师管理', path: '/teachers' }
    ]
  },
  {
    name: '资源中心',
    key: 'resource',
    children: [
      { name: '资源管理', path: '/resources' }
    ]
  },
  {
    name: '教学业务',
    key: 'teaching',
    children: [
      { name: '课程管理', path: '/courses' },
      { name: '习题管理', path: '/exercises' },
      { name: '试卷管理', path: '/papers' },
      { name: '考试管理', path: '/exams' }
    ]
  },
  {
    name: '系统管理',
    key: 'system',
    children: [
      { name: '公告管理', path: '/announcements' },
      { name: '权限管理', path: '/permissions' }
    ]
  }
]

const activeGroup = computed(() => {
  const path = route.path
  for (const group of menuData) {
    for (const item of group.children) {
      if (item.path === path) {
        return group
      }
    }
  }
  return menuData[0]
})

const subMenus = computed(() => activeGroup.value?.children || [])

function isFirstActive(group) {
  return group.key === activeGroup.value?.key
}

function isSubActive(path) {
  return route.path === path
}

function goTo(path) {
  router.push(path)
}
</script>

<template>
  <div class="layout">
    <!-- ==================== Header ==================== -->
    <header class="topbar">
      <div class="topbar-left">
        <div class="brand">
          <svg class="brand-icon" viewBox="0 0 24 24" fill="none" stroke="#409EFF" stroke-width="2">
            <path d="M12 2L2 7l10 5 10-5-10-5z" />
            <path d="M2 17l10 5 10-5" />
            <path d="M2 12l10 5 10-5" />
          </svg>
          <span class="brand-text">智慧校园后台</span>
        </div>
      </div>

      <nav class="topbar-nav">
        <div
          v-for="g in menuData"
          :key="g.key"
          class="nav-item"
          :class="{ active: isFirstActive(g) }"
          @click="goTo(g.children[0].path)"
        >
          {{ g.name }}
        </div>
      </nav>

      <div class="topbar-right">
        <div class="user">
          <span class="user-avatar">
            <svg viewBox="0 0 24 24" fill="#909399">
              <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z" />
            </svg>
          </span>
          <span class="user-name">管理员</span>
        </div>
      </div>
    </header>

    <!-- ==================== Body ==================== -->
    <main class="body">
      <!-- Sidebar card -->
      <aside class="side-card">
        <div class="side-title">{{ activeGroup?.name }}</div>
        <div class="side-menu">
          <div
            v-for="item in subMenus"
            :key="item.path"
            class="side-item"
            :class="{ active: isSubActive(item.path) }"
            @click="goTo(item.path)"
          >
            {{ item.name }}
          </div>
        </div>
      </aside>

      <!-- Content card -->
      <section class="content-card">
        <router-view />
      </section>
    </main>
  </div>
</template>

<style lang="scss" scoped>
$primary: #409EFF;
$primary-dark: #337ecc;
$bg: #f0f5ff;
$text: #606266;
$text-dark: #303133;
$shadow-sm: 0 1px 4px rgba(0, 0, 0, 0.06);
$shadow-card: 0 2px 12px rgba(0, 0, 0, 0.06);

.layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: $bg;
  font-family: 'Microsoft YaHei', 'PingFang SC', -apple-system, sans-serif;
}

/* ---------- Header ---------- */
.topbar {
  height: 64px;
  background: #fff;
  display: flex;
  align-items: center;
  padding: 0 28px;
  box-shadow: $shadow-sm;
  z-index: 10;
  flex-shrink: 0;
}

.topbar-left {
  min-width: 180px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;

  .brand-icon {
    width: 30px;
    height: 30px;
  }

  .brand-text {
    font-size: 18px;
    font-weight: 700;
    color: $text-dark;
    letter-spacing: 2px;
  }
}

.topbar-nav {
  flex: 1;
  display: flex;
  justify-content: center;
  gap: 2px;
  height: 100%;
}

.nav-item {
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 22px;
  font-size: 15px;
  color: $text;
  cursor: pointer;
  position: relative;
  transition: color 0.25s;
  user-select: none;

  &:hover {
    color: $primary;
  }

  &.active {
    color: $primary;
    font-weight: 600;

    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 50%;
      transform: translateX(-50%);
      width: 56%;
      height: 3px;
      background: $primary;
      border-radius: 3px 3px 0 0;
    }
  }
}

.topbar-right {
  min-width: 120px;
  display: flex;
  justify-content: flex-end;
}

.user {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: default;

  .user-avatar {
    width: 32px;
    height: 32px;
    background: #ecf5ff;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 6px;

    svg {
      width: 100%;
      height: 100%;
    }
  }

  .user-name {
    font-size: 14px;
    color: $text;
  }
}

/* ---------- Body ---------- */
.body {
  flex: 1;
  display: flex;
  gap: 16px;
  padding: 16px;
  min-height: 0;
  overflow: hidden;
}

/* Sidebar card */
.side-card {
  width: 200px;
  min-width: 200px;
  background: #fff;
  border-radius: 10px;
  box-shadow: $shadow-card;
  padding: 20px 0;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.side-title {
  font-size: 15px;
  font-weight: 600;
  color: $primary;
  padding: 0 20px 14px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 10px;
  letter-spacing: 1px;
}

.side-menu {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 0 10px;
}

.side-item {
  padding: 11px 16px;
  font-size: 14px;
  color: $text;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.25s;
  user-select: none;

  &:hover {
    color: $primary;
    background: #ecf5ff;
  }

  &.active {
    color: #fff;
    background: linear-gradient(135deg, $primary, $primary-dark);
    font-weight: 500;
  }
}

/* Content card */
.content-card {
  flex: 1;
  background: #fff;
  border-radius: 10px;
  box-shadow: $shadow-card;
  padding: 28px;
  overflow-y: auto;
  min-width: 0;
}
</style>

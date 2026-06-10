<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUserInfo } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const displayName = computed(() => userStore.realName || '管理员')

onMounted(async () => {
  if (userStore.isLoggedIn && !userStore.userInfo) {
    try {
      await getUserInfo()
      // getUserInfo updates the store internally via fetchUserInfo
    } catch {
      // already logged out
    }
  }
})

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
    name: 'AI 智能',
    key: 'ai',
    children: [
      { name: 'AI 助教', path: '/ai/chat' },
      { name: '知识库', path: '/ai/knowledge' },
      { name: '学业预警', path: '/ai/warning' },
      { name: '学业画像', path: '/ai/profile' }
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

async function handleLogoutCommand(command) {
  if (command === 'logout') {
    await handleLogout()
  }
}

async function handleLogout() {
  await userStore.logout()
  router.push('/login')
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
        <el-dropdown @command="handleLogoutCommand">
          <div class="user">
            <span class="user-avatar">
              <svg viewBox="0 0 24 24" fill="#909399">
                <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z" />
              </svg>
            </span>
            <span class="user-name">{{ displayName }}</span>
            <svg style="width:12px;height:12px;margin-left:4px" viewBox="0 0 1024 1024" fill="#909399">
              <path d="M512 742.9l352.7-352.7c25-25 25-65.5 0-90.5s-65.5-25-90.5 0L512 656.9 249.8 399.7c-25-25-65.5-25-90.5 0s-25 65.5 0 90.5L461.5 742.9c12.5 12.5 28.9 18.8 45.3 18.8s32.8-6.3 45.3-18.8z"/>
            </svg>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">
                <svg style="width:14px;height:14px;vertical-align:middle;margin-right:4px" viewBox="0 0 1024 1024" fill="#909399">
                  <path d="M224 576v-128h256v-512H224C164.7 64 112 116.7 112 176v672c0 59.3 52.7 112 112 112h256V768H224V576zm704-192H640V224l192 160z"/>
                </svg>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
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
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 8px;
  transition: background 0.2s;

  &:hover {
    background: #ecf5ff;
  }

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

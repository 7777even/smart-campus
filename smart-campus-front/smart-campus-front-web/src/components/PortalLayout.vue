<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const menuItems = [
  { name: '首页', path: '/home', icon: '🏠' },
  { name: '课程中心', path: '/courses', icon: '📚' },
  { name: '校园公告', path: '/announcements', icon: '📢' },
  { name: '个人中心', path: '/profile', icon: '👤' },
]

function goTo(path) {
  router.push(path)
}

function handleLogout() {
  localStorage.removeItem('portal_token')
  localStorage.removeItem('portal_user')
  router.push('/login')
}
</script>

<template>
  <div class="portal-layout">
    <header class="portal-header">
      <div class="header-left">
        <span class="logo">🎓 智慧校园</span>
      </div>
      <nav class="header-nav">
        <span
          v-for="item in menuItems"
          :key="item.path"
          class="nav-item"
          :class="{ active: $route.path === item.path }"
          @click="goTo(item.path)"
        >
          {{ item.icon }} {{ item.name }}
        </span>
      </nav>
      <div class="header-right">
        <el-dropdown trigger="click">
          <span class="user-info">
            <el-avatar :size="32" icon="UserFilled" />
            <span class="username">{{ JSON.parse(localStorage.getItem('portal_user') || '{}').realName || '游客' }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="goTo('/profile')">个人中心</el-dropdown-item>
              <el-dropdown-item @click="handleLogout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>
    <main class="portal-main">
      <router-view />
    </main>
    <footer class="portal-footer">
      <p>© 2026 智慧校园数字基座 · Smart Campus Platform</p>
    </footer>
  </div>
</template>

<style lang="scss">
* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: 'Microsoft YaHei', 'PingFang SC', sans-serif; background: #f0f5ff; }

.portal-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.portal-header {
  height: 64px;
  background: #fff;
  display: flex;
  align-items: center;
  padding: 0 32px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  position: sticky;
  top: 0;
  z-index: 100;

  .header-left {
    .logo { font-size: 20px; font-weight: 700; color: #409EFF; letter-spacing: 2px; }
  }

  .header-nav {
    flex: 1;
    display: flex;
    justify-content: center;
    gap: 4px;

    .nav-item {
      padding: 0 20px;
      height: 64px;
      display: flex;
      align-items: center;
      font-size: 15px;
      color: #606266;
      cursor: pointer;
      border-bottom: 3px solid transparent;
      transition: all 0.25s;
      user-select: none;

      &:hover { color: #409EFF; }
      &.active { color: #409EFF; font-weight: 600; border-bottom-color: #409EFF; }
    }
  }

  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      .username { font-size: 14px; color: #606266; }
    }
  }
}

.portal-main {
  flex: 1;
  padding: 24px 32px;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
}

.portal-footer {
  text-align: center;
  padding: 20px;
  color: #c0c4cc;
  font-size: 13px;
}
</style>

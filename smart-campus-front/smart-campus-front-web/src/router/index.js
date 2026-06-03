import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: '登录',
    component: () => import('@/views/Login.vue'),
    meta: { noAuth: true },
  },
  {
    path: '/',
    component: () => import('@/components/PortalLayout.vue'),
    redirect: '/home',
    children: [
      { path: 'home', name: '首页', component: () => import('@/views/Home.vue') },
      { path: 'courses', name: '课程中心', component: () => import('@/views/Courses.vue') },
      { path: 'announcements', name: '校园公告', component: () => import('@/views/Announcements.vue') },
      { path: 'ai-chat', name: 'AI 助教', component: () => import('@/views/AiChat.vue') },
      { path: 'profile', name: '个人中心', component: () => import('@/views/Profile.vue') },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

router.beforeEach((to, from, next) => {
  if (to.meta?.noAuth) {
    next()
    return
  }
  const token = localStorage.getItem('portal_token')
  if (!token) {
    next('/login')
  } else {
    next()
  }
})

export default router

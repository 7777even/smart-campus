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
      { path: 'course/:id', name: '课程详情', component: () => import('@/views/course/Learning.vue') },
      { path: 'exams', name: '我的考试', component: () => import('@/views/exam/MyExams.vue') },
      { path: 'exam/:id/take', name: '参加考试', component: () => import('@/views/exam/TakeExam.vue') },
      { path: 'announcements', name: '校园公告', component: () => import('@/views/Announcements.vue') },
      { path: 'ai-chat', name: 'AI 助教', component: () => import('@/views/AiChat.vue') },
      { path: 'analytics', name: '学习分析', component: () => import('@/views/Analytics.vue') },
      { path: 'profile', name: '个人中心', component: () => import('@/views/Profile.vue') },
      { path: 'schedule', name: '课表', component: () => import('@/views/schedule/Index.vue') },
      { path: 'exam-result', name: '成绩查询', component: () => import('@/views/exam/Result.vue') },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

router.beforeEach((to, from) => {
  if (to.meta?.noAuth) {
    return true
  }
  const token = localStorage.getItem('portal_token')
  if (!token) {
    return '/login'
  }
  return true
})

export default router

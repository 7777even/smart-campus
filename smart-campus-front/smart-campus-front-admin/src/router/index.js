import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: '登录',
    component: () => import('@/views/login/Index.vue'),
    meta: { noAuth: true },
  },
  {
    path: '/',
    component: () => import('@/components/AdminLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: '数据看板', component: () => import('@/views/dashboard/Index.vue') },
      { path: 'departments', name: '院系管理', component: () => import('@/views/departments/Index.vue') },
      { path: 'majors', name: '专业管理', component: () => import('@/views/majors/Index.vue') },
      { path: 'classes', name: '班级管理', component: () => import('@/views/classes/Index.vue') },
      { path: 'students', name: '学生管理', component: () => import('@/views/students/Index.vue') },
      { path: 'teachers', name: '教师管理', component: () => import('@/views/teachers/Index.vue') },
      { path: 'resources', name: '资源管理', component: () => import('@/views/resources/Index.vue') },
      { path: 'courses', name: '课程管理', component: () => import('@/views/courses/Index.vue') },
      { path: 'exercises', name: '习题管理', component: () => import('@/views/exercises/Index.vue') },
      { path: 'papers', name: '试卷管理', component: () => import('@/views/papers/Index.vue') },
      { path: 'exams', name: '考试管理', component: () => import('@/views/exams/Index.vue') },
      { path: 'announcements', name: '公告管理', component: () => import('@/views/announcements/Index.vue') },
      { path: 'permissions', name: '权限管理', component: () => import('@/views/permissions/Index.vue') },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

// 路由守卫 — 未登录跳转登录页
router.beforeEach((to, from, next) => {
  if (to.meta?.noAuth) {
    next()
    return
  }
  const token = localStorage.getItem('token')
  if (!token) {
    next('/login')
  } else {
    next()
  }
})

export default router

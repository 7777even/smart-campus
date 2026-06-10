import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { login as loginApi, getUserInfo, logout as logoutApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const role = computed(() => userInfo.value?.role)
  const realName = computed(() => userInfo.value?.realName || '游客')

  async function login(username, password) {
    const res = await loginApi({ username, password })
    const { token: t, user } = res.data
    token.value = t
    userInfo.value = user
    localStorage.setItem('token', t)
    localStorage.setItem('user', JSON.stringify(user))
    return res
  }

  async function fetchUserInfo() {
    try {
      const res = await getUserInfo()
      userInfo.value = res.data
      localStorage.setItem('user', JSON.stringify(res.data))
      return res.data
    } catch {
      clearLoginData()
      return null
    }
  }

  async function logout() {
    try {
      await logoutApi()
    } finally {
      clearLoginData()
    }
  }

  function clearLoginData() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return { token, userInfo, isLoggedIn, role, realName, login, fetchUserInfo, logout, clearLoginData }
})

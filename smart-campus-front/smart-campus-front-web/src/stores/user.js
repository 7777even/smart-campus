import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { login as loginApi, getUserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('portal_token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('portal_user') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const studentId = computed(() => userInfo.value?.studentId)
  const realName = computed(() => userInfo.value?.realName || '游客')

  async function login(username, password) {
    const res = await loginApi({ username, password })
    const { token: t, user } = res.data
    token.value = t
    userInfo.value = user
    localStorage.setItem('portal_token', t)
    localStorage.setItem('portal_user', JSON.stringify(user))
    return res
  }

  async function fetchUserInfo() {
    try {
      const res = await getUserInfo()
      userInfo.value = res.data
      localStorage.setItem('portal_user', JSON.stringify(res.data))
      return res.data
    } catch {
      clearLoginData()
      return null
    }
  }

  function clearLoginData() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('portal_token')
    localStorage.removeItem('portal_user')
  }

  return { token, userInfo, isLoggedIn, studentId, realName, login, fetchUserInfo, clearLoginData }
})

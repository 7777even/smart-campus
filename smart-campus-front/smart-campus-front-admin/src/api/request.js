/**
 * Axios 实例 — 全局请求配置
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' },
})

// 请求拦截器 — 注入 Token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

// 响应拦截器 — 统一处理错误
request.interceptors.response.use(
  (response) => {
    const res = response.data
    // 业务错误
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      // 401 跳转登录
      if (res.code === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        window.location.href = '/#/login'
      }
      return Promise.reject(new Error(res.msg))
    }
    return res
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/#/login'
    }
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  },
)

export default request

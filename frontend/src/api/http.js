import axios from 'axios'
import { ElMessage } from 'element-plus'
import { clearAuthStorage, getToken } from '@/utils/token'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
})

function redirectToLogin() {
  if (window.location.pathname !== '/login') {
    window.location.href = '/login'
  }
}

function handleUnauthorized() {
  clearAuthStorage()
  redirectToLogin()
}

http.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const result = response.data
    if (result?.code === 200) {
      return result.data
    }
    if (result?.code === 401) {
      handleUnauthorized()
    }
    const message = result?.message || '请求失败'
    ElMessage.error(message)
    return Promise.reject(new Error(message))
  },
  (error) => {
    if (error.response?.status === 401) {
      handleUnauthorized()
    }
    const message = error.response?.data?.message || error.message || '网络请求异常'
    ElMessage.error(message)
    return Promise.reject(new Error(message))
  },
)

export default http

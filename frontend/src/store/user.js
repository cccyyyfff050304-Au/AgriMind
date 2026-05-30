import { defineStore } from 'pinia'
import { loginApi } from '@/api/auth'
import { getUserProfileApi } from '@/api/user'
import { clearAuthStorage, getStoredProfile, getToken, setStoredProfile, setToken } from '@/utils/token'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    profile: getStoredProfile(),
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.token),
    displayName: (state) => state.profile?.realName || state.profile?.username || '未登录用户',
    roleLabel: (state) => state.profile?.roleCode || '普通用户',
  },
  actions: {
    setToken(token) {
      this.token = token
      setToken(token)
    },
    setProfile(profile) {
      this.profile = profile
      setStoredProfile(profile)
    },
    async login(credentials) {
      const result = await loginApi(credentials)
      if (!result?.token) {
        throw new Error('登录响应缺少 token')
      }
      this.setToken(result.token)
      this.setProfile(result.user)
      return result
    },
    async fetchProfile() {
      const profile = await getUserProfileApi()
      this.setProfile(profile)
      return profile
    },
    clearSession() {
      this.token = ''
      this.profile = null
      clearAuthStorage()
    },
  },
})

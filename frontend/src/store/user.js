import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    profile: {
      username: 'demo_admin',
      displayName: 'AgriMind 管理员',
      role: '项目演示账号',
    },
  }),
  actions: {
    setToken(token) {
      this.token = token
    },
    setProfile(profile) {
      this.profile = profile
    },
    clearSession() {
      this.token = ''
      this.profile = null
    },
  },
})

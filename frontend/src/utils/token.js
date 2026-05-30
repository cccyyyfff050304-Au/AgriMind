const TOKEN_KEY = 'agrimind_token'
const PROFILE_KEY = 'agrimind_user_profile'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}

export function getStoredProfile() {
  const rawProfile = localStorage.getItem(PROFILE_KEY)
  if (!rawProfile) {
    return null
  }
  try {
    return JSON.parse(rawProfile)
  } catch {
    localStorage.removeItem(PROFILE_KEY)
    return null
  }
}

export function setStoredProfile(profile) {
  if (!profile) {
    localStorage.removeItem(PROFILE_KEY)
    return
  }
  localStorage.setItem(PROFILE_KEY, JSON.stringify(profile))
}

export function clearAuthStorage() {
  removeToken()
  setStoredProfile(null)
}

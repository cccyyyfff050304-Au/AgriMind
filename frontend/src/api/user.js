import http from './http'

export function getUserProfileApi() {
  return http.get('/user/profile')
}

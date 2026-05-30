import http from './http'

export function loginApi(data) {
  return http.post('/auth/login', data)
}

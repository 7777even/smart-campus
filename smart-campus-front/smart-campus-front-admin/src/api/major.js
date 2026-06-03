/**
 * 专业管理 API
 */
import request from './request'

export function getMajors(params) {
  return request.get('/majors/page', { params })
}

export function getMajor(id) {
  return request.get(`/majors/${id}`)
}

export function createMajor(data) {
  return request.post('/majors', data)
}

export function updateMajor(id, data) {
  return request.put('/majors', data)
}

export function deleteMajor(id) {
  return request.delete(`/majors/${id}`)
}

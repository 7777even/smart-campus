/**
 * 教师管理 API
 */
import request from './request'

export function getTeachers(params) {
  return request.get('/teachers/page', { params })
}

export function getTeacher(id) {
  return request.get(`/teachers/${id}`)
}

export function createTeacher(data) {
  return request.post('/teachers', data)
}

export function updateTeacher(id, data) {
  return request.put('/teachers', data)
}

export function deleteTeacher(id) {
  return request.delete(`/teachers/${id}`)
}

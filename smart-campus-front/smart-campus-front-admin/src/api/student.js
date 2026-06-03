/**
 * 学生管理 API
 */
import request from './request'

export function getStudents(params) {
  return request.get('/students/page', { params })
}

export function getStudent(id) {
  return request.get(`/students/${id}`)
}

export function createStudent(data) {
  return request.post('/students', data)
}

export function updateStudent(id, data) {
  return request.put('/students', data)
}

export function deleteStudent(id) {
  return request.delete(`/students/${id}`)
}

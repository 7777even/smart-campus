/**
 * 院系管理 API
 */
import request from './request'

export function getDepartments(params) {
  return request.get('/departments/page', { params })
}

export function getDepartment(id) {
  return request.get(`/departments/${id}`)
}

export function createDepartment(data) {
  return request.post('/departments', data)
}

export function updateDepartment(id, data) {
  return request.put('/departments', data)
}

export function deleteDepartment(id) {
  return request.delete(`/departments/${id}`)
}

/**
 * 资源管理 API
 */
import request from './request'

export function getResources(params) {
  return request.get('/resources/page', { params })
}

export function getResource(id) {
  return request.get(`/resources/${id}`)
}

export function updateResource(id, data) {
  return request.put('/resources', data)
}

export function deleteResource(id) {
  return request.delete(`/resources/${id}`)
}

export function uploadResource(formData) {
  return request.post('/resources/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

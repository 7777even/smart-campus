/**
 * 试卷管理 API
 */
import request from './request'

export function getPapers(params) {
  return request.get('/papers/page', { params })
}

export function getPaper(id) {
  return request.get(`/papers/${id}`)
}

export function createPaper(data) {
  return request.post('/papers', data)
}

export function updatePaper(id, data) {
  return request.put('/papers', data)
}

export function deletePaper(id) {
  return request.delete(`/papers/${id}`)
}

export function publishPaper(id) {
  return request.put(`/papers/${id}/publish`)
}

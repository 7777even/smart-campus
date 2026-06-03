/**
 * 考试管理 API
 */
import request from './request'

export function getExams(params) {
  return request.get('/exams/page', { params })
}

export function getExam(id) {
  return request.get(`/exams/${id}`)
}

export function createExam(data) {
  return request.post('/exams', data)
}

export function updateExam(id, data) {
  return request.put('/exams', data)
}

export function deleteExam(id) {
  return request.delete(`/exams/${id}`)
}

/**
 * 习题管理 API
 */
import request from './request'

export function getExercises(params) {
  return request.get('/exercises/page', { params })
}

export function getExercise(id) {
  return request.get(`/exercises/${id}`)
}

export function createExercise(data) {
  return request.post('/exercises', data)
}

export function updateExercise(id, data) {
  return request.put('/exercises', data)
}

export function deleteExercise(id) {
  return request.delete(`/exercises/${id}`)
}

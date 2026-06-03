/**
 * 课程管理 API
 */
import request from './request'

export function getCourses(params) {
  return request.get('/courses/page', { params })
}

export function getCourse(id) {
  return request.get(`/courses/${id}`)
}

export function createCourse(data) {
  return request.post('/courses', data)
}

export function updateCourse(id, data) {
  return request.put('/courses', data)
}

export function deleteCourse(id) {
  return request.delete(`/courses/${id}`)
}

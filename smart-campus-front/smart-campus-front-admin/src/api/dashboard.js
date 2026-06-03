/**
 * 数据看板 API
 */
import request from './request'

export function getOverview() {
  return request.get('/dashboard/overview')
}

export function getTeachingStats() {
  return request.get('/dashboard/teaching')
}

export function getStudentStats() {
  return request.get('/dashboard/students')
}

export function getResourceStats() {
  return request.get('/dashboard/resources')
}

export function getExamStats() {
  return request.get('/dashboard/exams')
}

export function getSystemStats() {
  return request.get('/dashboard/system')
}

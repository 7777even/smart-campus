import request from '@/api/request'

/**
 * 学生画像
 */
export function getStudentProfile(studentId) {
  return request.get(`/ai/profile/student/${studentId}`)
}

/**
 * 学业预警
 */
export function getStudentWarnings(studentId) {
  return request.get(`/ai/profile/warning/student/${studentId}`)
}

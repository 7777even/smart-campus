import request from '@/api/request'
import { getMyCourses } from '@/api/enrollment'

/**
 * 获取用户基本信息
 */
export function getUserBasic() {
  return request.get('/auth/info')
}

/**
 * 获取学习概览统计
 */
export async function getProfileStats() {
  const res = await getMyCourses()
  const courses = res.data || []
  return {
    enrolledCourses: courses.length,
    completedExams: 0,
    avgScore: 0,
  }
}

/**
 * 获取学业画像
 */
export function getAcademicProfile(studentId) {
  return request.get(`/ai/profile/student/${studentId}`)
}

/**
 * 获取学业预警
 */
export function getAcademicWarnings(studentId) {
  return request.get(`/ai/profile/warning/student/${studentId}`)
}

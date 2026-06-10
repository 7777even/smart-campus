import request from '@/api/request'
import { getMyCourses } from '@/api/enrollment'
import { getStudentProfile, getStudentWarnings } from '@/api/ai-profile'

/**
 * 获取学习统计数据
 */
export async function getLearningStats() {
  const res = await getMyCourses()
  const courses = res.data || []
  return {
    enrolledCount: courses.length,
    courseProgress: courses.map(c => ({
      courseId: c.id,
      courseName: c.name,
      completionRate: c.completionRate || 0,
    })),
  }
}

/**
 * 获取学习日志
 */
export function getLearningLogs(params) {
  return request.get('/learning/logs', { params })
}

/**
 * 获取学业画像
 */
export function getAnalyticsProfile(studentId) {
  return getStudentProfile(studentId)
}

/**
 * 获取学业预警
 */
export function getAnalyticsWarnings(studentId) {
  return getStudentWarnings(studentId)
}

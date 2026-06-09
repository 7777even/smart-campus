import request from '@/api/request'

/**
 * 课程分页列表
 */
export function getCoursePage(params) {
  return request.get('/courses/page', { params })
}

/**
 * 课程详情
 */
export function getCourseDetail(id) {
  return request.get(`/courses/${id}`)
}

/**
 * 热门课程
 */
export function getHotCourses(limit = 8) {
  return request.get('/courses/hot', { params: { limit } })
}

/**
 * 我的课程
 */
export function getMyCourses() {
  return request.get('/courses/my')
}

import request from '@/api/request'

/**
 * 课程资源列表
 */
export function getResourcesByCourse(courseId) {
  return request.get(`/resources/by-course/${courseId}`)
}

/**
 * 资源详情
 */
export function getResourceDetail(id) {
  return request.get(`/resources/${id}`)
}

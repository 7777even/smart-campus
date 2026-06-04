import request from '@/api/request'

export function getRecommendCourses(limit = 8) {
  return request.get('/recommend/courses', { params: { limit } })
}

export function getHotCourses(limit = 8) {
  return request.get('/recommend/hot-courses', { params: { limit } })
}

export function getRecommendResources(limit = 4) {
  return request.get('/recommend/resources', { params: { limit } })
}

export function getPeersAlsoEnrolled(courseId, limit = 6) {
  return request.get(`/recommend/peers-also-enrolled/${courseId}`, { params: { limit } })
}

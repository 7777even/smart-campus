import request from '@/api/request'

export function enrollCourse(courseId) {
  return request.post('/student-courses/enroll', { courseId })
}

export function dropCourse(courseId) {
  return request.post(`/student-courses/drop/${courseId}`)
}

export function getMyCourses() {
  return request.get('/courses/my')
}

export function checkEnrolled(courseId) {
  return request.get(`/courses/enrolled/${courseId}/check`)
}

export function getEnrollCount() {
  return request.get('/student-courses/count')
}

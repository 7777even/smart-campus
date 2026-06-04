import request from '@/api/request'

export function enrollCourse(courseId) {
  return request.post('/student-courses/enroll', { courseId })
}

export function dropCourse(courseId) {
  return request.post(`/student-courses/drop/${courseId}`)
}

export function getMyCourses() {
  return request.get('/student-courses/my')
}

export function checkEnrolled(courseId) {
  return request.get(`/student-courses/check/${courseId}`)
}

export function getEnrollCount() {
  return request.get('/student-courses/count')
}

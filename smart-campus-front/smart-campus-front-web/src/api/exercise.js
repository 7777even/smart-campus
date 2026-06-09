import request from '@/api/request'

/**
 * 课程习题列表
 */
export function getExercisesByCourse(courseId) {
  return request.get(`/exercises/by-course/${courseId}`)
}

/**
 * 习题详情
 */
export function getExerciseDetail(id) {
  return request.get(`/exercises/${id}`)
}

/**
 * 提交答案
 */
export function submitExerciseAnswer(id, answer) {
  return request.post(`/exercises/${id}/submit`, { answer })
}

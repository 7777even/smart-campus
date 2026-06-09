import request from '@/api/request'

/**
 * 我的考试列表
 */
export function getMyExams() {
  return request.get('/exams/my')
}

/**
 * 考试详情
 */
export function getExamDetail(id) {
  return request.get(`/exams/${id}`)
}

/**
 * 开始考试
 */
export function startExam(examId) {
  return request.post('/exams/start', { examId })
}

/**
 * 提交答案
 */
export function submitExamAnswers(data) {
  return request.post('/exams/submit', data)
}

/**
 * 考试成绩列表
 */
export function getExamResults() {
  return request.get('/exams/results')
}

/**
 * 我的课表
 */
export function getMySchedule() {
  return request.get('/courses/schedule')
}

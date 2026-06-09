import request from '@/api/request'

/**
 * 记录学习进度
 */
export function recordProgress(data) {
  return request.post('/learning/progress', data)
}

/**
 * 获取学习进度
 */
export function getProgress(courseId) {
  return request.get('/learning/progress', { params: { courseId } })
}

/**
 * 记录视频播放进度
 */
export function recordVideoProgress(data) {
  return request.post('/learning/video-progress', data)
}

/**
 * 获取视频播放进度
 */
export function getVideoProgress(lessonId) {
  return request.get('/learning/video-progress', { params: { lessonId } })
}

/**
 * 获取学习日志
 */
export function getLearningLogs(params) {
  return request.get('/learning/logs', { params })
}

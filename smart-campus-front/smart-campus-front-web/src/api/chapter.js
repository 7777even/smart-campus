import request from '@/api/request'

/**
 * 章节列表（含课时）
 */
export function getChaptersByCourse(courseId) {
  return request.get(`/chapters/by-course/${courseId}`)
}

/**
 * 课时列表
 */
export function getLessonsByChapter(chapterId) {
  return request.get(`/lessons/by-chapter/${chapterId}`)
}

/**
 * 课时详情
 */
export function getLessonDetail(id) {
  return request.get(`/lessons/${id}`)
}

/**
 * 课时资源
 */
export function getLessonResources(lessonId) {
  return request.get(`/lessons/${lessonId}/resources`)
}

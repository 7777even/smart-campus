import request from '@/api/request'

/**
 * 公告分页列表
 */
export function getAnnouncementPage(params) {
  return request.get('/announcements/page', { params })
}

/**
 * 公告详情
 */
export function getAnnouncementDetail(id) {
  return request.get(`/announcements/${id}`)
}

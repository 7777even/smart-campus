/**
 * 公告管理 API
 */
import request from './request'

export function getAnnouncements(params) {
  return request.get('/announcements/page', { params })
}

export function getAnnouncement(id) {
  return request.get(`/announcements/${id}`)
}

export function createAnnouncement(data) {
  return request.post('/announcements', data)
}

export function updateAnnouncement(id, data) {
  return request.put(`/announcements/${id}`, data)
}

export function deleteAnnouncement(id) {
  return request.delete(`/announcements/${id}`)
}

export function togglePublish(id) {
  return request.put(`/announcements/${id}/toggle-publish`)
}

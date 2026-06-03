import request from './request'

// ===================== AI 对话 =====================

export function createConversation(data) {
  return request.post('/ai/chat/create', data)
}

export function sendMessage(conversationId, data) {
  return request.post(`/ai/chat/${conversationId}/message`, data)
}

export function getConversationMessages(conversationId) {
  return request.get(`/ai/chat/${conversationId}/messages`)
}

export function getConversationList() {
  return request.get('/ai/chat/list')
}

export function deleteConversation(id) {
  return request.delete(`/ai/chat/${id}`)
}

// ===================== 知识库 =====================

export function getKnowledgePage(params) {
  return request.get('/ai/knowledge/page', { params })
}

export function getKnowledgeDoc(id) {
  return request.get(`/ai/knowledge/${id}`)
}

export function createKnowledgeDoc(data) {
  return request.post('/ai/knowledge', data)
}

export function updateKnowledgeDoc(data) {
  return request.put('/ai/knowledge', data)
}

export function deleteKnowledgeDoc(id) {
  return request.delete(`/ai/knowledge/${id}`)
}

export function searchKnowledge(keyword) {
  return request.get('/ai/knowledge/search', { params: { keyword } })
}

// ===================== 学业画像 =====================

export function getProfilePage(params) {
  return request.get('/ai/profile/page', { params })
}

export function calculateProfile(studentId) {
  return request.post(`/ai/profile/calculate/${studentId}`)
}

export function calculateAllProfiles() {
  return request.post('/ai/profile/calculate-all')
}

export function getProfileStatistics() {
  return request.get('/ai/profile/statistics')
}

// ===================== 学业预警 =====================

export function getWarningPage(params) {
  return request.get('/ai/warning/page', { params })
}

export function evaluateWarning(profileId) {
  return request.post(`/ai/warning/evaluate/${profileId}`)
}

export function evaluateAllWarnings() {
  return request.post('/ai/warning/evaluate-all')
}

export function resolveWarning(id, data) {
  return request.put(`/ai/warning/${id}/resolve`, data)
}

export function getWarningStatistics() {
  return request.get('/ai/warning/statistics')
}

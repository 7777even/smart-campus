import request from '@/api/request'

/**
 * 创建对话
 */
export function createConversation(title) {
  return request.post('/ai/chat/create', { title })
}

/**
 * 发送消息
 */
export function sendMessage(conversationId, message) {
  return request.post(`/ai/chat/${conversationId}/message`, { message })
}

/**
 * 获取消息列表
 */
export function getMessages(conversationId) {
  return request.get(`/ai/chat/${conversationId}/messages`)
}

/**
 * 对话列表
 */
export function getChatList() {
  return request.get('/ai/chat/list')
}

/**
 * 删除对话
 */
export function deleteConversation(id) {
  return request.delete(`/ai/chat/${id}`)
}

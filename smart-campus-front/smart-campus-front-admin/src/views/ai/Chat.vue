<template>
  <div class="chat-view">
    <!-- 左侧对话列表 -->
    <div class="chat-sidebar">
      <div class="sidebar-header">
        <h3>💬 AI 对话</h3>
        <el-button type="primary" size="small" @click="handleNewChat" :icon="Plus">新建对话</el-button>
      </div>
      <div class="conversation-list" v-loading="loadingList">
        <div v-for="conv in conversations" :key="conv.id"
             class="conv-item"
             :class="{ active: currentConvId === conv.id }"
             @click="switchConversation(conv.id)">
          <div class="conv-title">{{ conv.title || '新对话' }}</div>
          <div class="conv-time">{{ formatTime(conv.updateTime) }}</div>
          <el-button class="conv-delete" size="small" text type="danger"
                     @click.stop="handleDelete(conv.id)" :icon="Delete" />
        </div>
        <el-empty v-if="conversations.length === 0" description="暂无对话" :image-size="60" />
      </div>
    </div>

    <!-- 右侧消息区域 -->
    <div class="chat-main">
      <template v-if="currentConvId">
        <div class="message-list" ref="messageListRef" v-loading="loadingMessages">
          <div v-for="msg in messages" :key="msg.id"
               class="message-item"
               :class="msg.role === 'user' ? 'message-user' : 'message-assistant'">
            <div class="message-avatar">
              {{ msg.role === 'user' ? '👤' : '🤖' }}
            </div>
            <div class="message-bubble">
              <div class="message-content" v-html="renderContent(msg.content)"></div>
              <div class="message-time">{{ formatTime(msg.createTime) }}</div>
            </div>
          </div>
          <div v-if="sending" class="message-item message-assistant">
            <div class="message-avatar">🤖</div>
            <div class="message-bubble message-thinking">
              <span class="thinking-dot">.</span>
              <span class="thinking-dot">.</span>
              <span class="thinking-dot">.</span>
              <span class="thinking-text">正在思考</span>
            </div>
          </div>
        </div>

        <div class="input-area">
          <el-input
            v-model="inputText"
            type="textarea"
            :rows="3"
            placeholder="请输入您的问题，例如：高数考试范围是什么？"
            :disabled="sending"
            @keydown.enter.prevent="handleSend"
          />
          <div class="input-actions">
            <el-button type="primary" @click="handleSend" :loading="sending" :disabled="!inputText.trim()">
              发送
            </el-button>
            <el-tag type="info" size="small" effect="plain">Enter 发送</el-tag>
          </div>
        </div>
      </template>

      <template v-else>
        <div class="welcome">
          <div class="welcome-icon">🤖</div>
          <h2>智慧校园 AI 助教</h2>
          <p>您好！我是 AI 助教，可以帮您解答校园相关问题</p>
          <div class="suggestions">
            <el-tag v-for="q in quickQuestions" :key="q" @click="handleQuickQuestion(q)"
                    class="suggestion-tag" effect="plain">
              {{ q }}
            </el-tag>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import {
  createConversation, sendMessage, getConversationMessages,
  getConversationList, deleteConversation
} from '@/api/ai'

const conversations = ref([])
const currentConvId = ref(null)
const messages = ref([])
const inputText = ref('')
const sending = ref(false)
const loadingList = ref(false)
const loadingMessages = ref(false)
const messageListRef = ref(null)

const quickQuestions = ['如何查看成绩？', '考试安排在哪里查？', '怎么选课？', '学籍异动怎么办理？']

function formatTime(t) {
  if (!t) return ''
  return t.substring(0, 16).replace('T', ' ')
}

function renderContent(text) {
  if (!text) return ''
  return text.replace(/\n/g, '<br>')
}

async function fetchConversations() {
  loadingList.value = true
  try {
    const res = await getConversationList()
    conversations.value = res.data || []
  } catch (e) { /* ignored */ }
  finally { loadingList.value = false }
}

async function switchConversation(id) {
  currentConvId.value = id
  loadingMessages.value = true
  try {
    const res = await getConversationMessages(id)
    messages.value = res.data || []
    await nextTick()
    scrollToBottom()
  } catch (e) { /* ignored */ }
  finally { loadingMessages.value = false }
}

async function handleNewChat() {
  try {
    const res = await createConversation({ title: '新对话' })
    const conv = res.data
    conversations.value.unshift(conv)
    currentConvId.value = conv.id
    messages.value = []
    inputText.value = ''
  } catch (e) {
    ElMessage.error('创建对话失败')
  }
}

async function handleSend() {
  const text = inputText.value.trim()
  if (!text || !currentConvId.value || sending.value) return

  inputText.value = ''
  sending.value = true

  try {
    const res = await sendMessage(currentConvId.value, { content: text })
    await switchConversation(currentConvId.value)
    await fetchConversations()
  } catch (e) {
    ElMessage.error('发送失败，请重试')
  } finally {
    sending.value = false
    await nextTick()
    scrollToBottom()
  }
}

function handleQuickQuestion(q) {
  inputText.value = q
  if (!currentConvId.value) {
    handleNewChat().then(() => {
      nextTick(() => handleSend())
    })
  } else {
    handleSend()
  }
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定删除此对话？', '确认')
    await deleteConversation(id)
    if (currentConvId.value === id) {
      currentConvId.value = null
      messages.value = []
    }
    await fetchConversations()
    ElMessage.success('删除成功')
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

function scrollToBottom() {
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}

onMounted(fetchConversations)
</script>

<style scoped>
.chat-view {
  display: flex;
  height: calc(100vh - 64px - 32px - 56px);
  gap: 16px;
}

.chat-sidebar {
  width: 260px;
  min-width: 260px;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #f0f0f0;
  padding-right: 16px;
}

.sidebar-header {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.sidebar-header h3 { margin: 0; font-size: 16px; color: #303133; }

.conversation-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.conv-item {
  padding: 10px 12px;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  transition: background 0.2s;
}

.conv-item:hover { background: #ecf5ff; }
.conv-item.active { background: #e6f1ff; }

.conv-title {
  font-size: 14px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding-right: 24px;
}

.conv-time { font-size: 12px; color: #c0c4cc; margin-top: 2px; }
.conv-delete { position: absolute; right: 4px; top: 50%; transform: translateY(-50%); opacity: 0; }
.conv-item:hover .conv-delete { opacity: 1; }

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-item {
  display: flex;
  gap: 10px;
  max-width: 80%;
}

.message-user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-assistant { align-self: flex-start; }

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.6;
  font-size: 14px;
  word-break: break-word;
}

.message-user .message-bubble {
  background: linear-gradient(135deg, #409EFF, #337ecc);
  color: #fff;
  border-bottom-right-radius: 4px;
}

.message-assistant .message-bubble {
  background: #f5f7fa;
  color: #303133;
  border-bottom-left-radius: 4px;
}

.message-time {
  font-size: 11px;
  color: #c0c4cc;
  margin-top: 4px;
}

.message-user .message-time { text-align: right; color: rgba(255,255,255,0.6); }

.message-thinking { display: flex; align-items: center; gap: 4px; padding: 16px 20px !important; }

.thinking-dot {
  animation: blink 1.4s infinite;
  font-size: 20px;
  line-height: 0;
  color: #909399;
}

.thinking-dot:nth-child(2) { animation-delay: 0.2s; }
.thinking-dot:nth-child(3) { animation-delay: 0.4s; }

.thinking-text {
  font-size: 14px;
  color: #909399;
  margin-left: 4px;
}

@keyframes blink {
  0%, 80%, 100% { opacity: 0; }
  40% { opacity: 1; }
}

.input-area {
  border-top: 1px solid #f0f0f0;
  padding-top: 12px;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.welcome {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  color: #909399;
}

.welcome-icon { font-size: 64px; margin-bottom: 16px; }

.welcome h2 { font-size: 22px; color: #303133; margin-bottom: 8px; }

.welcome p { font-size: 14px; margin-bottom: 24px; }

.suggestions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.suggestion-tag {
  cursor: pointer;
  padding: 6px 12px;
  font-size: 13px;
}

.suggestion-tag:hover { color: #409EFF; border-color: #409EFF; }
</style>

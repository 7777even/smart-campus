<template>
  <div class="ai-chat-page">
    <div class="chat-container">
      <!-- 消息区域 -->
      <div class="message-area" ref="messageAreaRef">
        <div v-if="!currentConvId" class="welcome-section">
          <div class="welcome-icon">🤖</div>
          <h2>AI 助教</h2>
          <p>您好！我是智慧校园 AI 助教，可以帮您解答校园相关问题</p>
          <div class="quick-questions">
            <el-button v-for="q in quickQuestions" :key="q" @click="handleQuickQuestion(q)" text bg>
              {{ q }}
            </el-button>
          </div>
        </div>

        <div v-else class="messages">
          <div v-for="msg in messages" :key="msg.id"
               class="message" :class="msg.role === 'user' ? 'user-msg' : 'ai-msg'">
            <div class="msg-avatar">{{ msg.role === 'user' ? '👤' : '🤖' }}</div>
            <div class="msg-content">
              <div class="msg-bubble">{{ msg.content }}</div>
              <div class="msg-time">{{ formatTime(msg.createTime) }}</div>
            </div>
          </div>
          <div v-if="sending" class="message ai-msg">
            <div class="msg-avatar">🤖</div>
            <div class="msg-content">
              <div class="msg-bubble thinking">
                <span class="dot">.</span><span class="dot">.</span><span class="dot">.</span>
                <span style="margin-left: 4px; color: #909399;">思考中</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="input-area">
        <el-input
          v-model="inputText"
          :rows="2"
          type="textarea"
          placeholder="输入您的问题..."
          :disabled="sending"
          @keydown.enter.prevent="handleSend"
        />
        <div class="input-actions">
          <el-button v-if="currentConvId" text @click="handleNewChat">🔄 新对话</el-button>
          <el-button type="primary" @click="handleSend" :loading="sending" :disabled="!inputText.trim()">
            发送
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const currentConvId = ref(null)
const messages = ref([])
const inputText = ref('')
const sending = ref(false)
const messageAreaRef = ref(null)

const quickQuestions = [
  '如何查成绩？',
  '考试安排在哪里？',
  '怎么选课？',
  '如何办理休学？',
  '一卡通丢了怎么办？'
]

function formatTime(t) {
  if (!t) return ''
  return t.substring(0, 16).replace('T', ' ')
}

async function createConversation() {
  try {
    const res = await request.post('/ai/chat/create', { title: '学生咨询' })
    currentConvId.value = res.data.id
    messages.value = []
    return res.data
  } catch (e) {
    ElMessage.error('创建对话失败')
    return null
  }
}

async function sendMsg(convId, content) {
  sending.value = true
  try {
    await request.post(`/ai/chat/${convId}/message`, { content })
    // 刷新消息
    const res = await request.get(`/ai/chat/${convId}/messages`)
    messages.value = res.data || []
    await nextTick()
    scrollToBottom()
  } catch (e) {
    ElMessage.error('发送失败')
  } finally {
    sending.value = false
  }
}

async function handleSend() {
  const text = inputText.value.trim()
  if (!text || sending.value) return

  inputText.value = ''

  if (!currentConvId.value) {
    const conv = await createConversation()
    if (!conv) return
  }

  await sendMsg(currentConvId.value, text)
}

async function handleQuickQuestion(q) {
  inputText.value = q
  await nextTick()
  handleSend()
}

async function handleNewChat() {
  currentConvId.value = null
  messages.value = []
  inputText.value = ''
}

function scrollToBottom() {
  if (messageAreaRef.value) {
    messageAreaRef.value.scrollTop = messageAreaRef.value.scrollHeight
  }
}
</script>

<style scoped>
.ai-chat-page {
  max-width: 800px;
  margin: 0 auto;
  height: calc(100vh - 64px - 48px - 80px);
  display: flex;
  flex-direction: column;
}

.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  overflow: hidden;
}

.message-area {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

.welcome-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  color: #909399;
}

.welcome-icon { font-size: 64px; margin-bottom: 12px; }

.welcome-section h2 { font-size: 20px; color: #303133; margin-bottom: 8px; }

.welcome-section p { font-size: 14px; margin-bottom: 24px; }

.quick-questions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.messages {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message {
  display: flex;
  gap: 10px;
  max-width: 85%;
}

.user-msg { align-self: flex-end; flex-direction: row-reverse; }
.ai-msg { align-self: flex-start; }

.msg-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
  background: #f5f7fa;
}

.msg-bubble {
  padding: 10px 14px;
  border-radius: 10px;
  line-height: 1.6;
  font-size: 14px;
  word-break: break-word;
}

.user-msg .msg-bubble {
  background: linear-gradient(135deg, #409EFF, #337ecc);
  color: #fff;
  border-bottom-right-radius: 4px;
}

.ai-msg .msg-bubble {
  background: #f5f7fa;
  color: #303133;
  border-bottom-left-radius: 4px;
}

.msg-time { font-size: 11px; color: #c0c4cc; margin-top: 4px; text-align: right; }

.thinking { display: flex; align-items: center; padding: 8px 16px; }

.dot {
  animation: blink 1.4s infinite;
  font-size: 18px;
  line-height: 0;
  color: #909399;
}

.dot:nth-child(2) { animation-delay: 0.2s; }
.dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes blink {
  0%, 80%, 100% { opacity: 0; }
  40% { opacity: 1; }
}

.input-area {
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}
</style>

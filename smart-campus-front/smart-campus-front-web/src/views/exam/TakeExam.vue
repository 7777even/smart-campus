<template>
  <div class="page-take-exam" v-loading="loading">
    <template v-if="exam">
      <!-- Exam header -->
      <div class="exam-header">
        <h1>{{ exam.name }}</h1>
        <div class="timer" :class="{ urgent: timeLeft <= 300 }">
          剩余时间: {{ formatTime(timeLeft) }}
        </div>
      </div>

      <div class="exam-body">
        <!-- Question navigation -->
        <div class="nav-panel">
          <h3>题目导航</h3>
          <div class="nav-grid">
            <button
              v-for="(q, i) in questions"
              :key="q.id"
              :class="['nav-btn', { active: currentIdx === i, answered: answers[q.id] }]"
              @click="currentIdx = i"
            >
              {{ i + 1 }}
            </button>
          </div>
          <div class="nav-actions">
            <el-button type="danger" @click="handleSubmit">提交试卷</el-button>
          </div>
        </div>

        <!-- Question area -->
        <div class="question-area">
          <div class="question-header">
            <span class="question-number">第 {{ currentIdx + 1 }} 题</span>
            <span class="question-type">{{ currentQuestion.type }}</span>
          </div>

          <div class="question-body">
            <p class="question-text">{{ currentQuestion.question }}</p>

            <div v-if="currentQuestion.type !== '简答题'" class="options">
              <el-radio-group v-model="answers[currentQuestion.id]" @change="onAnswerChange">
                <div v-for="opt in currentQuestion.options" :key="opt.key" class="option-item">
                  <el-radio :value="opt.key" :label="opt.key">
                    <span class="option-label">{{ opt.key }}. {{ opt.value }}</span>
                  </el-radio>
                </div>
              </el-radio-group>
            </div>

            <div v-else class="essay-answer">
              <el-input
                v-model="answers[currentQuestion.id]"
                type="textarea"
                :rows="6"
                placeholder="请输入您的答案"
                @input="onAnswerChange"
              />
            </div>
          </div>

          <div class="question-footer">
            <el-button @click="prevQuestion" :disabled="currentIdx === 0">上一题</el-button>
            <el-button type="primary" @click="nextQuestion" :disabled="currentIdx === questions.length - 1">
              下一题
            </el-button>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { startExam, submitExamAnswers } from '@/api/exam'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const exam = ref(null)
const questions = ref([])
const answers = ref({})
const currentIdx = ref(0)
const timeLeft = ref(3600) // 60 minutes default

let timerInterval = null

const currentQuestion = computed(() => questions.value[currentIdx.value] || {})

function formatTime(seconds) {
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}

function loadExamData(examData, qs) {
  exam.value = examData
  questions.value = qs
  // Set duration from exam data
  if (exam.value.duration) {
    timeLeft.value = exam.value.duration * 60
  }
  startTimer()
}

function startTimer() {
  timerInterval = setInterval(() => {
    if (timeLeft.value <= 0) {
      handleSubmit()
      return
    }
    timeLeft.value--
  }, 1000)
}

function stopTimer() {
  if (timerInterval) {
    clearInterval(timerInterval)
    timerInterval = null
  }
}

function onAnswerChange() {
  // auto-save answer (state already updated via v-model)
}

function prevQuestion() {
  if (currentIdx.value > 0) currentIdx.value--
}

function nextQuestion() {
  if (currentIdx.value < questions.value.length - 1) currentIdx.value++
}

async function handleSubmit() {
  try {
    await ElMessageBox.confirm('确定提交试卷吗？提交后不能修改答案。', '确认提交', {
      confirmButtonText: '确定提交', cancelButtonText: '再检查一下', type: 'warning',
    })
  } catch { return }

  stopTimer()

  try {
    const answerMap = {}
    questions.value.forEach((q, i) => {
      answerMap[String(q.id)] = answers.value[q.id] || ''
    })

    const res = await submitExamAnswers({ examId: route.params.id, answers: answerMap })
    ElMessage.success(`考试提交成功！得分: ${res.data?.totalScore ?? '待教师评分'}`)
    router.push('/exams')
  } catch (e) {
    ElMessage.error(e.response?.data?.msg || '提交失败')
    stopTimer()
    router.push('/exams')
  }
}

async function fetchExamData() {
  loading.value = true
  try {
    const res = await startExam(route.params.id)
    const data = res.data
    if (data.exam) {
      loadExamData(data.exam, data.questions || [])
    } else {
      ElMessage.error('加载考试数据失败')
      router.push('/exams')
    }
  } catch {
    ElMessage.error('加载考试失败')
    router.push('/exams')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchExamData()
})

onUnmounted(() => {
  stopTimer()
})
</script>

<style lang="scss" scoped>
.page-take-exam { padding-bottom: 32px; }

.exam-header {
  background: #fff;
  border-radius: 12px;
  padding: 20px 24px;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);

  h1 { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
  .timer { font-size: 18px; font-weight: 600; color: #409EFF; font-family: monospace; }
  .timer.urgent { color: #f56c6c; animation: pulse 1s infinite; }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.exam-body {
  display: flex;
  gap: 24px;
}

.nav-panel {
  width: 200px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);

  h3 { font-size: 15px; font-weight: 600; color: #303133; margin-bottom: 12px; }

  .nav-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 8px;
    margin-bottom: 16px;
  }

  .nav-btn {
    width: 40px;
    height: 40px;
    border: 1px solid #dcdfe6;
    border-radius: 6px;
    background: #fff;
    cursor: pointer;
    font-size: 13px;
    color: #606266;
    transition: all 0.2s;

    &.active { background: #409EFF; color: #fff; border-color: #409EFF; }
    &.answered { background: #67c23a; color: #fff; border-color: #67c23a; }
    &:hover { border-color: #409EFF; color: #409EFF; }
  }

  .nav-actions { padding-top: 12px; border-top: 1px solid #f0f0f0; }
}

.question-area {
  flex: 1;
  min-width: 0;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}

.question-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  .question-number { font-size: 16px; font-weight: 600; color: #303133; }
  .question-type { font-size: 13px; color: #909399; }
}

.question-text {
  font-size: 15px;
  line-height: 1.8;
  color: #303133;
  margin-bottom: 24px;
  white-space: pre-wrap;
}

.options { margin-bottom: 24px; }

.option-item {
  margin-bottom: 12px;
  padding: 12px 16px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  transition: all 0.2s;

  &:hover { border-color: #409EFF; background: #f5f7fa; }
  .option-label { font-size: 14px; color: #303133; }
}

.essay-answer { margin-bottom: 24px; }

.question-footer {
  display: flex;
  justify-content: space-between;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

@media (max-width: 768px) {
  .exam-body { flex-direction: column; }
  .nav-panel { width: 100%; }
}
</style>

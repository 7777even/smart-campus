<template>
  <div class="page-my-exams">
    <h2 class="page-title">📝 我的考试</h2>

    <div v-loading="loading" class="exam-list">
      <template v-if="exams.length > 0">
        <el-card v-for="exam in exams" :key="exam.id" class="exam-card" shadow="hover">
          <div class="exam-header">
            <h3>{{ exam.name }}</h3>
            <el-tag :type="statusTag(exam.status)">{{ exam.status }}</el-tag>
          </div>
          <div class="exam-details">
            <div class="detail-item">
              <span class="label">考试课程</span>
              <span>{{ exam.courseName || '未知课程' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">考试时间</span>
              <span>{{ exam.examDate }} {{ exam.startTime }}</span>
            </div>
            <div class="detail-item">
              <span class="label">考试时长</span>
              <span>{{ exam.duration }} 分钟</span>
            </div>
            <div class="detail-item">
              <span class="label">考试地点</span>
              <span>{{ exam.location || '线上考试' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">监考教师</span>
              <span>{{ exam.invigilator || '待定' }}</span>
            </div>
          </div>
          <div class="exam-actions">
            <el-button
              v-if="exam.status === '进行中'"
              type="primary"
              @click="$router.push(`/exam/${exam.id}/take`)"
            >
              开始考试
            </el-button>
            <el-tag v-if="exam.totalQuestions !== undefined" type="info">
              {{ exam.totalQuestions }} 道题
            </el-tag>
          </div>
        </el-card>
      </template>
      <el-empty v-else-if="!loading" description="暂无考试安排" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyExams } from '@/api/exam'

const loading = ref(false)
const exams = ref([])

function statusTag(status) {
  const map = { '进行中': '', '待开始': 'info', '已结束': 'info' }
  return map[status] || ''
}

async function fetchExams() {
  loading.value = true
  try {
    const res = await getMyExams()
    exams.value = res.data || []
  } catch {
    ElMessage.error('加载考试列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchExams()
})
</script>

<style lang="scss" scoped>
.page-my-exams { padding-bottom: 32px; }
.page-title { font-size: 22px; font-weight: 600; color: #303133; margin-bottom: 24px; }

.exam-list { display: flex; flex-direction: column; gap: 16px; }

.exam-card {
  .exam-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    h3 { font-size: 17px; font-weight: 600; color: #303133; margin: 0; }
  }

  .exam-details {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
    margin-bottom: 16px;
  }

  .detail-item {
    font-size: 13px;
    .label { color: #909399; margin-right: 8px; }
  }

  .exam-actions {
    display: flex;
    gap: 12px;
    align-items: center;
    padding-top: 12px;
    border-top: 1px solid #f0f0f0;
  }
}
</style>

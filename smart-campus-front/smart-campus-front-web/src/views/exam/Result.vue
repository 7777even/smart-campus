<template>
  <div class="page-exam-result">
    <h2 class="page-title">📊 考试成绩查询</h2>

    <!-- Stats cards -->
    <div class="stats-row">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value">{{ stats.total }}</div>
        <div class="stat-label">考试次数</div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value">{{ stats.avgScore }}</div>
        <div class="stat-label">平均分</div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value">{{ stats.highestScore }}</div>
        <div class="stat-label">最高分</div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value">{{ stats.passRate }}%</div>
        <div class="stat-label">及格率</div>
      </el-card>
    </div>

    <!-- Result list -->
    <el-card class="section-card" shadow="hover">
      <template #header>
        <h3>考试记录</h3>
      </template>
      <div v-loading="loading" class="result-list">
        <div v-if="results.length === 0 && !loading" class="empty-text">暂无考试成绩记录</div>
        <el-card
          v-for="item in results"
          :key="item.id"
          class="result-item"
          shadow="never"
        >
          <div class="result-header">
            <span class="result-name">{{ item.examName }}</span>
            <el-tag :type="scoreTagType(item.score)">
              {{ item.score !== null ? item.score + '分' : '待评阅' }}
            </el-tag>
          </div>
          <div class="result-body">
            <div class="info-item">
              <span class="label">所属课程</span>
              <span class="value">{{ item.courseName || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">考试日期</span>
              <span class="value">{{ item.examDate || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">答题情况</span>
              <span class="value">{{ item.correctCount }}/{{ item.totalCount }} 题正确</span>
            </div>
            <div class="info-item">
              <span class="label">考试状态</span>
              <el-tag :type="statusTag(item.status)" size="small">{{ item.status || '-' }}</el-tag>
            </div>
          </div>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getExamResults } from '@/api/exam'

const loading = ref(false)
const results = ref([])

const stats = computed(() => {
  const scored = results.value.filter(r => r.score !== null && r.score !== undefined)
  if (scored.length === 0) {
    return { total: results.value.length, avgScore: 0, highestScore: 0, passRate: 0 }
  }
  const total = scored.length
  const sum = scored.reduce((s, r) => s + r.score, 0)
  const highest = Math.max(...scored.map(r => r.score))
  const passed = scored.filter(r => r.score >= 60).length
  return {
    total,
    avgScore: Math.round(sum / total),
    highestScore: highest,
    passRate: Math.round((passed / total) * 100),
  }
})

function scoreTagType(score) {
  if (score >= 80) return 'success'
  if (score >= 60) return 'warning'
  return 'danger'
}

function statusTag(status) {
  const map = { '已完成': 'success', '进行中': '', '待开始': 'info', '已结束': 'info' }
  return map[status] || 'info'
}

async function fetchResults() {
  loading.value = true
  try {
    const res = await getExamResults()
    results.value = res.data || []
  } catch {
    results.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchResults()
})
</script>

<style lang="scss" scoped>
.page-exam-result { padding-bottom: 32px; }
.page-title { font-size: 22px; font-weight: 600; color: #303133; margin-bottom: 24px; }

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  text-align: center;
  padding: 8px 0;

  .stat-value {
    display: block;
    font-size: 28px;
    font-weight: 700;
    color: #409EFF;
  }
  .stat-label {
    display: block;
    font-size: 13px;
    color: #909399;
    margin-top: 8px;
  }
}

.section-card {
  margin-bottom: 24px;

  h3 { font-size: 16px; font-weight: 600; color: #303133; margin: 0; }
}

.empty-text { text-align: center; color: #c0c4cc; padding: 40px 0; }

.result-list { display: flex; flex-direction: column; gap: 12px; }

.result-item {
  border-radius: 10px;

  .result-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .result-name {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
  }

  .result-body {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;

    .info-item {
      font-size: 13px;
      display: flex;
      align-items: center;
      .label { color: #909399; margin-right: 8px; white-space: nowrap; }
      .value { color: #303133; }
    }
  }
}

@media (max-width: 768px) {
  .stats-row { grid-template-columns: repeat(2, 1fr); }
  .result-body { grid-template-columns: 1fr; }
}
</style>

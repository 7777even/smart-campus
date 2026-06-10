<template>
  <div class="page-analytics">
    <h2 class="page-title">📊 学习分析</h2>

    <!-- Stats cards -->
    <div class="stats-row">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value">{{ enrolledCount }}</div>
        <div class="stat-label">选课数</div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value">{{ completedPercent }}%</div>
        <div class="stat-label">平均完成度</div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value">{{ examCount }}</div>
        <div class="stat-label">考试次数</div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value">{{ loggedHours }}h</div>
        <div class="stat-label">学习时长</div>
      </el-card>
    </div>

    <!-- Course progress -->
    <el-card class="section-card" shadow="hover">
      <template #header>
        <h3>课程进度</h3>
      </template>
      <div class="course-progress-list">
        <div v-if="courseProgress.length === 0" class="empty-text">暂无学习数据</div>
        <div v-for="cp in courseProgress" :key="cp.courseId" class="course-progress-item">
          <div class="cp-info">
            <span class="cp-name">{{ cp.courseName }}</span>
            <span class="cp-rate">{{ cp.completionRate }}%</span>
          </div>
          <el-progress :percentage="cp.completionRate" :stroke-width="10" :color="getProgressColor(cp.completionRate)" />
        </div>
      </div>
    </el-card>

    <!-- Learning logs -->
    <el-card class="section-card" shadow="hover">
      <template #header>
        <h3>学习日志</h3>
      </template>
      <div class="log-list">
        <div v-if="logs.length === 0" class="empty-text">暂无学习记录</div>
        <div v-for="log in logs" :key="log.id" class="log-item">
          <span class="log-icon">
            {{ log.logType === '视频播放' ? '▶' : log.logType === '章节浏览' ? '📖' : '📝' }}
          </span>
          <div class="log-content">
            <span class="log-desc">{{ log.detail }}</span>
            <span class="log-time">{{ log.createTime }}</span>
          </div>
          <el-tag size="small" type="info">{{ log.logType }}</el-tag>
        </div>
      </div>
      <div v-if="logs.length > 0" class="log-more">
        <el-button text type="primary" @click="loadMoreLogs">加载更多</el-button>
      </div>
    </el-card>

    <!-- Academic profile -->
    <el-card class="section-card" shadow="hover" v-if="profile">
      <template #header>
        <h3>学业画像</h3>
      </template>
      <div class="profile-grid">
        <div class="profile-item">
          <span class="profile-label">综合评分</span>
          <div class="profile-value">{{ profile.comprehensiveScore }}</div>
        </div>
        <div class="profile-item">
          <span class="profile-label">GPA</span>
          <div class="profile-value">{{ profile.gpa }}</div>
        </div>
        <div class="profile-item">
          <span class="profile-label">考试平均分</span>
          <div class="profile-value">{{ profile.examAvg }}</div>
        </div>
        <div class="profile-item">
          <span class="profile-label">风险等级</span>
          <el-tag :type="getRiskType(profile.riskLevel)">{{ profile.riskLevel || '无' }}</el-tag>
        </div>
        <div class="profile-item">
          <span class="profile-label">趋势</span>
          <el-tag :type="getTrendType(profile.trend)">{{ profile.trend || '稳定' }}</el-tag>
        </div>
        <div class="profile-item">
          <span class="profile-label">出勤率</span>
          <div class="profile-value">{{ profile.attendanceRate }}</div>
        </div>
      </div>
    </el-card>

    <!-- Warnings -->
    <el-card class="section-card" shadow="hover" v-if="warnings.length > 0">
      <template #header>
        <h3>学业预警</h3>
      </template>
      <div class="warning-list">
        <div v-for="w in warnings" :key="w.id" class="warning-item">
          <el-tag :type="w.level === 'red' ? 'danger' : 'warning'" size="small">{{ w.level === 'red' ? '红色预警' : '黄色预警' }}</el-tag>
          <span class="warning-desc">{{ w.description }}</span>
          <span class="warning-time">{{ w.createTime }}</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getLearningStats, getLearningLogs, getAnalyticsProfile, getAnalyticsWarnings } from '@/api/analytics'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const studentId = userStore.studentId

const enrolledCount = ref(0)
const completedPercent = ref(0)
const examCount = ref(0)
const loggedHours = ref(0)

const courseProgress = ref([])
const logs = ref([])
const profile = ref(null)
const warnings = ref([])

function getProgressColor(pct) {
  if (pct >= 80) return '#67c23a'
  if (pct >= 50) return '#e6a23c'
  return '#f56c6c'
}

function getRiskType(level) {
  const map = { 'green': 'success', 'yellow': 'warning', 'red': 'danger' }
  return map[level] || 'info'
}

function getTrendType(trend) {
  const map = { 'up': 'success', 'down': 'danger', 'stable': 'info' }
  return map[trend] || 'info'
}

async function loadStats() {
  try {
    const { enrolledCount: ec, courseProgress: cp } = await getLearningStats()
    enrolledCount.value = ec
    courseProgress.value = cp
  } catch {
    // not logged in
  }
}

async function loadLogs() {
  try {
    const res = await getLearningLogs({ pageNo: 0, pageSize: 10 })
    logs.value = res.data?.list || []
  } catch {
    // no logs
  }
}

async function loadProfile() {
  if (!studentId) return
  try {
    const res = await getAnalyticsProfile(studentId)
    profile.value = res.data
  } catch {
    // no profile
  }
  try {
    const res = await getAnalyticsWarnings(studentId)
    warnings.value = res.data || []
  } catch {
    warnings.value = []
  }
}

function loadMoreLogs() {
  // lazy load - could add pagination
}

onMounted(() => {
  loadStats()
  loadLogs()
  loadProfile()
})
</script>

<style lang="scss" scoped>
.page-analytics { padding-bottom: 32px; }
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

.course-progress-list { display: flex; flex-direction: column; gap: 16px; }

.course-progress-item {
  .cp-info {
    display: flex;
    justify-content: space-between;
    margin-bottom: 8px;
    .cp-name { font-size: 14px; color: #303133; }
    .cp-rate { font-size: 14px; color: #909399; font-weight: 600; }
  }
}

.log-list { display: flex; flex-direction: column; gap: 12px; }

.log-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;

  .log-icon { font-size: 18px; width: 24px; text-align: center; }
  .log-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 4px;
    min-width: 0;
    .log-desc { font-size: 14px; color: #303133; }
    .log-time { font-size: 12px; color: #c0c4cc; }
  }
}

.log-more { margin-top: 12px; text-align: center; }

.profile-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.profile-item {
  text-align: center;
  padding: 16px;
  background: #f8f9fc;
  border-radius: 8px;

  .profile-label {
    display: block;
    font-size: 13px;
    color: #909399;
    margin-bottom: 8px;
  }
  .profile-value {
    font-size: 20px;
    font-weight: 600;
    color: #303133;
  }
}

.warning-list { display: flex; flex-direction: column; gap: 12px; }

.warning-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #fdf6ec;
  border-radius: 8px;

  .warning-desc { flex: 1; font-size: 14px; color: #e6a23c; }
  .warning-time { font-size: 12px; color: #c0c4cc; }
}

@media (max-width: 768px) {
  .stats-row { grid-template-columns: repeat(2, 1fr); }
  .profile-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>

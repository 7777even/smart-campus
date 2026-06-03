<template>
  <div class="page-profile">
    <h2 class="page-title">👤 个人中心</h2>

    <el-row :gutter="24">
      <el-col :span="8">
        <el-card class="profile-card">
          <div class="avatar-section">
            <el-avatar :size="80" icon="UserFilled" />
            <h3>{{ user.realName || '未知用户' }}</h3>
            <p class="user-role">{{ roleLabel }}</p>
          </div>
          <el-divider />
          <div class="info-item"><span>用户名</span><span>{{ user.username }}</span></div>
          <div class="info-item"><span>邮箱</span><span>{{ user.email || '-' }}</span></div>
          <div class="info-item"><span>角色</span><span>{{ roleLabel }}</span></div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card style="margin-bottom: 16px;">
          <template #header><span>📊 学习概览</span></template>
          <div class="stats-grid">
            <div class="stat-box"><span class="num">{{ stats.enrolledCourses }}</span><span class="lbl">已选课程</span></div>
            <div class="stat-box"><span class="num">{{ stats.completedExams }}</span><span class="lbl">已完成考试</span></div>
            <div class="stat-box"><span class="num">{{ stats.avgScore }}</span><span class="lbl">平均成绩</span></div>
          </div>
        </el-card>

        <!-- 学业状态 -->
        <el-card v-if="profile" class="academic-card">
          <template #header>
            <span>📋 学业状态</span>
            <el-tag v-if="profile.riskLevel === 'green'" type="success" size="small" style="margin-left: 8px">🟢 正常</el-tag>
            <el-tag v-else-if="profile.riskLevel === 'yellow'" type="warning" size="small" style="margin-left: 8px">🟡 需关注</el-tag>
            <el-tag v-else-if="profile.riskLevel === 'red'" type="danger" size="small" style="margin-left: 8px">🔴 需干预</el-tag>
          </template>
          <div class="academic-grid" v-if="profile">
            <div class="academic-item">
              <span class="label">综合评分</span>
              <span class="value" :class="scoreClass(profile.comprehensiveScore)">{{ profile.comprehensiveScore || '-' }}</span>
            </div>
            <div class="academic-item">
              <span class="label">考试平均分</span>
              <span class="value">{{ profile.examAvg || '-' }}</span>
            </div>
            <div class="academic-item">
              <span class="label">作业平均分</span>
              <span class="value">{{ profile.homeworkAvg || '-' }}</span>
            </div>
            <div class="academic-item">
              <span class="label">出勤率</span>
              <span class="value">{{ profile.attendanceRate ? (profile.attendanceRate * 100).toFixed(1) + '%' : '-' }}</span>
            </div>
          </div>
        </el-card>

        <!-- 预警提醒 -->
        <el-card v-if="warnings.length > 0" style="margin-top: 16px;">
          <template #header><span>⚠️ 预警提醒 ({{ warnings.length }})</span></template>
          <div v-for="w in warnings" :key="w.id" class="warning-item">
            <el-tag :type="w.level === 'red' ? 'danger' : 'warning'" size="small" style="margin-right: 8px">
              {{ w.level === 'red' ? '🔴' : '🟡' }} {{ w.warningType === 'attendance' ? '出勤' : w.warningType === 'homework' ? '作业' : w.warningType === 'exam' ? '考试' : '综合' }}
            </el-tag>
            <span class="warning-desc">{{ w.description }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '@/api/request'

const user = ref(JSON.parse(localStorage.getItem('portal_user') || '{}'))

const roleLabel = computed(() => {
  const map = { super_admin: '超级管理员', admin: '管理员', teacher: '教师', student: '学生', parent: '家长' }
  return map[user.value.role] || user.value.role || '未知'
})

const stats = ref({ enrolledCourses: 0, completedExams: 0, avgScore: 0 })
const profile = ref(null)
const warnings = ref([])

function scoreClass(score) {
  if (!score) return ''
  if (score >= 80) return 'score-good'
  if (score >= 60) return 'score-warn'
  return 'score-bad'
}

async function fetchProfile() {
  try {
    const res = await request.get(`/ai/profile/student/${user.value.id}`)
    profile.value = res.data
  } catch (e) {
    // 画像不存在或 API 不可用时忽略
  }
}

async function fetchWarnings() {
  try {
    const res = await request.get('/ai/warning/page', {
      params: { pageSize: 5, status: 'pending' },
    })
    const list = res.data?.list || []
    // 过滤当前用户相关的预警
    warnings.value = list.filter(w => w.studentId === user.value.id)
  } catch (e) {
    // 忽略
  }
}

onMounted(async () => {
  stats.value = { enrolledCourses: 6, completedExams: 12, avgScore: 86 }
  if (user.value.role === 'student' || user.value.role === 'admin' || user.value.role === 'super_admin') {
    await fetchProfile()
    await fetchWarnings()
  }
})
</script>

<style lang="scss" scoped>
.page-profile { padding-bottom: 32px; }
.page-title { font-size: 22px; font-weight: 600; color: #303133; margin-bottom: 20px; }

.profile-card { text-align: center; }
.avatar-section {
  padding: 20px 0;
  h3 { margin-top: 12px; font-size: 18px; color: #303133; }
  .user-role { font-size: 13px; color: #909399; margin-top: 4px; }
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  font-size: 14px;
  color: #606266;
  border-bottom: 1px solid #f5f5f5;
  &:last-child { border-bottom: none; }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;

  .stat-box {
    text-align: center;
    padding: 20px;
    background: #f0f5ff;
    border-radius: 10px;

    .num { display: block; font-size: 28px; font-weight: 700; color: #409EFF; }
    .lbl { display: block; font-size: 13px; color: #909399; margin-top: 6px; }
  }
}

.academic-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.academic-item {
  background: #fafafa;
  padding: 12px 16px;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .label { font-size: 13px; color: #909399; }
  .value { font-size: 16px; font-weight: 600; color: #303133; }
  .score-good { color: #67C23A; }
  .score-warn { color: #E6A23C; }
  .score-bad { color: #F56C6C; }
}

.warning-item {
  display: flex;
  align-items: flex-start;
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;
  &:last-child { border-bottom: none; }
}

.warning-desc {
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
  flex: 1;
}
</style>

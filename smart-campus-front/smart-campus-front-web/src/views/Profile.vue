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
        <el-card>
          <template #header><span>📊 学习概览</span></template>
          <div class="stats-grid">
            <div class="stat-box"><span class="num">{{ stats.enrolledCourses }}</span><span class="lbl">已选课程</span></div>
            <div class="stat-box"><span class="num">{{ stats.completedExams }}</span><span class="lbl">已完成考试</span></div>
            <div class="stat-box"><span class="num">{{ stats.avgScore }}</span><span class="lbl">平均成绩</span></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const user = ref(JSON.parse(localStorage.getItem('portal_user') || '{}'))

const roleLabel = computed(() => {
  const map = { super_admin: '超级管理员', admin: '管理员', teacher: '教师', student: '学生', parent: '家长' }
  return map[user.value.role] || user.value.role || '未知'
})

const stats = ref({ enrolledCourses: 0, completedExams: 0, avgScore: 0 })

onMounted(async () => {
  // TODO: fetch student stats from API when available
  stats.value = { enrolledCourses: 6, completedExams: 12, avgScore: 86 }
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
</style>

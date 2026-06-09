<template>
  <div class="page-schedule">
    <h2 class="page-title">📅 我的课表</h2>

    <!-- Stats card -->
    <div class="stats-row">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value">{{ courses.length }}</div>
        <div class="stat-label">本学期课程</div>
      </el-card>
    </div>

    <!-- Course list -->
    <el-card class="section-card" shadow="hover">
      <template #header>
        <h3>课程列表</h3>
      </template>
      <div v-loading="loading" class="course-list">
        <div v-if="courses.length === 0 && !loading" class="empty-text">暂无课表数据</div>
        <el-card
          v-for="course in courses"
          :key="course.id"
          class="course-item"
          shadow="never"
        >
          <div class="course-header">
            <span class="course-name">{{ course.name }}</span>
            <el-tag :type="statusTag(course.status)" size="small">{{ statusLabel(course.status) }}</el-tag>
          </div>
          <div class="course-body">
            <div class="info-item">
              <span class="label">授课教师</span>
              <span class="value">{{ course.teacherName || '待定' }}</span>
            </div>
            <div class="info-item">
              <span class="label">上课时间</span>
              <span class="value">{{ course.scheduleTime || '未安排' }}</span>
            </div>
            <div class="info-item">
              <span class="label">上课地点</span>
              <span class="value">{{ course.location || '未安排' }}</span>
            </div>
            <div class="info-item">
              <span class="label">已选状态</span>
              <el-tag :type="enrolledTag(course.enrolled)" size="small">
                {{ course.enrolled ? '已选' : '未选' }}
              </el-tag>
            </div>
          </div>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMySchedule } from '@/api/exam'
import { getMyCourses } from '@/api/enrollment'

const loading = ref(false)
const courses = ref([])

function statusTag(status) {
  const map = { '正常': 'success', '停开': 'danger', '进行中': '' }
  return map[status] || 'info'
}

function statusLabel(status) {
  return status || '未知'
}

function enrolledTag(enrolled) {
  return enrolled ? 'success' : 'info'
}

async function fetchSchedule() {
  loading.value = true
  try {
    const res = await getMySchedule()
    courses.value = res.data || []
  } catch {
    // 课表接口暂未实现时，尝试从课程中心获取
    try {
      const res = await getMyCourses()
      const items = res.data || []
      courses.value = items.map(c => ({
        id: c.id,
        name: c.name,
        teacherName: c.teacherName || c.teacherId,
        scheduleTime: '未安排',
        location: '未安排',
        status: c.status || '正常',
        enrolled: true,
      }))
    } catch {
      courses.value = []
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchSchedule()
})
</script>

<style lang="scss" scoped>
.page-schedule { padding-bottom: 32px; }
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

.course-list { display: flex; flex-direction: column; gap: 12px; }

.course-item {
  border-radius: 10px;

  .course-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .course-name {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
  }

  .course-body {
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
  .course-body { grid-template-columns: 1fr; }
}
</style>

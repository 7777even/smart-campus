<template>
  <div class="page-home">
    <!-- Hero Banner -->
    <section class="hero">
      <div class="hero-content">
        <h1>欢迎回来，{{ userName }}</h1>
        <p>AI 驱动的数字校园基座 — 学习、生活、成长的一站式平台</p>
      </div>
    </section>

    <!-- Stats -->
    <section class="stats-row">
      <div v-for="s in stats" :key="s.label" class="stat-card">
        <span class="stat-value">{{ s.value }}</span>
        <span class="stat-label">{{ s.label }}</span>
      </div>
    </section>

    <!-- Recommended Courses -->
    <section class="section-card" v-if="recommendCourses.length > 0">
      <div class="section-header">
        <h3>🎯 为你推荐</h3>
        <el-button text type="primary" @click="$router.push('/courses?tab=recommend')">查看更多 →</el-button>
      </div>
      <div class="course-grid">
        <el-card v-for="c in recommendCourses" :key="c.id" class="course-card" shadow="hover" @click="$router.push('/courses?tab=recommend')">
          <h4>{{ c.name }}</h4>
          <p class="course-teacher">{{ c.teacherName || '待定' }}</p>
          <div class="course-meta">
            <el-tag size="small">{{ c.type }}</el-tag>
            <span class="course-credit">{{ c.credit }} 学分</span>
          </div>
        </el-card>
      </div>
    </section>

    <!-- Recent Announcements -->
    <section class="section-card">
      <div class="section-header">
        <h3>📢 最新公告</h3>
        <el-button text type="primary" @click="$router.push('/announcements')">查看更多 →</el-button>
      </div>
      <div v-for="item in announcements" :key="item.id" class="announce-item" @click="handleViewAnnounce(item)">
        <el-tag :type="item.level === '紧急' ? 'danger' : item.level === '重要' ? 'warning' : 'info'" size="small">
          {{ item.level }}
        </el-tag>
        <span class="announce-title">{{ item.title }}</span>
        <span class="announce-date">{{ item.createTime }}</span>
      </div>
      <el-empty v-if="announcements.length === 0" description="暂无公告" :image-size="60" />
    </section>

    <!-- Hot Courses -->
    <section class="section-card">
      <div class="section-header">
        <h3>📚 热门课程</h3>
        <el-button text type="primary" @click="$router.push('/courses')">查看更多 →</el-button>
      </div>
      <div class="course-grid">
        <el-card v-for="c in hotCourses" :key="c.id" class="course-card" shadow="hover" @click="$router.push('/courses')">
          <h4>{{ c.name }}</h4>
          <p class="course-teacher">{{ c.teacherName || '待定' }}</p>
          <div class="course-meta">
            <el-tag size="small">{{ c.type }}</el-tag>
            <span class="course-credit">{{ c.credit }} 学分</span>
            <el-tag v-if="c.enrollCount !== undefined" size="small" type="info">{{ c.enrollCount }}人选</el-tag>
          </div>
        </el-card>
      </div>
    </section>

    <!-- Recommended Resources -->
    <section class="section-card" v-if="recommendResources.length > 0">
      <div class="section-header">
        <h3>📎 推荐资源</h3>
      </div>
      <div class="resource-list">
        <div v-for="r in recommendResources" :key="r.id" class="resource-item">
          <span class="resource-icon">{{ typeIcon(r.type) }}</span>
          <div class="resource-info">
            <span class="resource-name">{{ r.name }}</span>
            <span class="resource-meta">{{ r.type }} · {{ r.downloads || 0 }} 次下载</span>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'
import { getRecommendCourses, getHotCourses, getRecommendResources } from '@/api/recommend'

const user = JSON.parse(localStorage.getItem('portal_user') || '{}')
const userName = computed(() => user.realName || '同学')

const stats = ref([])
const announcements = ref([])
const hotCourses = ref([])
const recommendCourses = ref([])
const recommendResources = ref([])

function typeIcon(type) {
  const map = { '视频': '🎬', '文档': '📄', '图片': '🖼', '音频': '🎵', '其他': '📁' }
  return map[type] || '📁'
}

async function fetchData() {
  try {
    const [overviewRes, announceRes, hotCourseRes] = await Promise.all([
      request.get('/dashboard/overview'),
      request.get('/announcements/page', { params: { pageSize: 5, status: '已发布' } }),
      getHotCourses(4),
    ])
    const ov = overviewRes.data
    stats.value = [
      { label: '在校学生', value: ov.totalStudents },
      { label: '教师队伍', value: ov.totalTeachers },
      { label: '开设课程', value: ov.totalCourses },
      { label: '院系数量', value: ov.totalDepartments },
    ]
    announcements.value = announceRes.data?.list?.slice(0, 5) || []
    hotCourses.value = hotCourseRes.data || []
  } catch (e) { /* ignored */ }
}

async function fetchRecommend() {
  try {
    const [coursesRes, resourcesRes] = await Promise.allSettled([
      getRecommendCourses(4),
      getRecommendResources(4),
    ])
    if (coursesRes.status === 'fulfilled') {
      recommendCourses.value = coursesRes.value.data || []
    }
    if (resourcesRes.status === 'fulfilled') {
      recommendResources.value = resourcesRes.value.data || []
    }
  } catch (e) { /* ignored */ }
}

function handleViewAnnounce(item) {
  ElMessage.info(`公告详情: ${item.title}`)
}

onMounted(() => {
  fetchData()
  fetchRecommend()
})
</script>

<style lang="scss" scoped>
.page-home { padding-bottom: 40px; }

.hero {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 16px;
  padding: 60px 40px;
  margin-bottom: 24px;
  text-align: center;
  color: #fff;

  h1 { font-size: 32px; font-weight: 700; margin-bottom: 12px; }
  p { font-size: 16px; opacity: 0.9; }
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);

  .stat-value { display: block; font-size: 28px; font-weight: 700; color: #409EFF; }
  .stat-label { display: block; font-size: 14px; color: #909399; margin-top: 8px; }
}

.section-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    h3 { font-size: 18px; font-weight: 600; color: #303133; margin: 0; }
  }
}

.announce-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background 0.2s;
  &:hover { background: #fafafa; }
  &:last-child { border-bottom: none; }

  .announce-title { flex: 1; font-size: 14px; color: #303133; }
  .announce-date { font-size: 12px; color: #c0c4cc; }
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.course-card {
  cursor: pointer;
  h4 { font-size: 15px; font-weight: 600; color: #303133; margin-bottom: 8px; }
  .course-teacher { font-size: 13px; color: #909399; margin-bottom: 12px; }
  .course-meta { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }
  .course-credit { font-size: 12px; color: #c0c4cc; }
}

.resource-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.resource-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #f8f9fc;
  border-radius: 10px;
  transition: background 0.2s;
  cursor: default;

  &:hover { background: #f0f2f7; }

  .resource-icon { font-size: 24px; }
  .resource-info {
    display: flex;
    flex-direction: column;
    gap: 2px;
    min-width: 0;
    .resource-name { font-size: 14px; font-weight: 500; color: #303133; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
    .resource-meta { font-size: 12px; color: #909399; }
  }
}
</style>

<template>
  <div class="page-learning">
    <div v-loading="loading" class="learning-container">
      <template v-if="course">
        <!-- Header -->
        <div class="course-header">
          <h1>{{ course.name }}</h1>
          <div class="course-meta">
            <el-tag>{{ course.type }}</el-tag>
            <span>{{ course.teacherName || '待定' }}</span>
            <span>{{ course.credit }} 学分 · {{ course.hours }} 课时</span>
          </div>
          <div class="progress-bar-wrap">
            <el-progress :percentage="progressPercent" :stroke-width="8" />
            <span class="progress-text">{{ completedLessons }} / {{ totalLessons }} 课时已完成</span>
          </div>
        </div>

        <div class="learning-body">
          <!-- Left: Chapter/lesson sidebar -->
          <div class="sidebar">
            <div class="sidebar-header">
              <h3>课程目录</h3>
            </div>
            <div v-if="!loading" class="chapter-list">
              <div v-for="ch in chapters" :key="ch.id" class="chapter-group">
                <div class="chapter-title" @click="ch.expanded = !ch.expanded">
                  <span class="expand-icon">{{ ch.expanded ? '▾' : '▸' }}</span>
                  {{ ch.name }}
                </div>
                <div v-show="ch.expanded" class="lesson-list">
                  <div
                    v-for="lesson in ch.lessons"
                    :key="lesson.id"
                    class="lesson-item"
                    :class="{ active: currentLesson?.id === lesson.id, completed: lessonCompleted(lesson.id) }"
                    @click="loadLesson(lesson)"
                  >
                    <span class="lesson-icon">▶</span>
                    <span class="lesson-name">{{ lesson.name }}</span>
                    <span v-if="lesson.duration" class="lesson-duration">{{ lesson.duration }}分钟</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Right: Content area -->
          <div class="content-area">
            <div v-if="currentLesson" class="lesson-content">
              <!-- Video player -->
              <div v-if="currentLesson.resourceType === '视频' || currentLesson.resourceType === 'video'" class="video-player">
                <video
                  ref="videoRef"
                  controls
                  crossorigin
                  preload="auto"
                  :src="currentLesson.resourceUrl"
                  @timeupdate="onTimeUpdate"
                  @loadedmetadata="onVideoLoaded"
                >
                  您的浏览器不支持视频播放
                </video>
              </div>

              <!-- Document/resource preview -->
              <div v-else class="resource-preview">
                <div class="resource-icon">{{ typeIcon(currentLesson.resourceType) }}</div>
                <h3>{{ currentLesson.name }}</h3>
                <p>{{ currentLesson.description || '' }}</p>
                <a v-if="currentLesson.resourceUrl" :href="currentLesson.resourceUrl" target="_blank" class="resource-link">
                  查看资源 →
                </a>
              </div>

              <div class="lesson-info">
                <h2>{{ currentLesson.name }}</h2>
                <p>{{ currentLesson.description || '暂无描述' }}</p>
              </div>
            </div>

            <div v-else class="empty-state">
              <el-empty description="请选择一个课时开始学习" />
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCourseDetail } from '@/api/course'
import { getChaptersByCourse } from '@/api/chapter'
import { getProgress, recordVideoProgress } from '@/api/learning'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const course = ref(null)
const chapters = ref([])
const currentLesson = ref(null)
const videoRef = ref(null)

const completedCount = ref(0)
const totalCount = ref(0)
const studentId = computed(() => userStore.studentId)

const progressPercent = computed(() => {
  if (totalCount.value === 0) return 0
  return Math.round((completedCount.value / totalCount.value) * 100)
})

const completedLessons = computed(() => completedCount.value)
const totalLessons = computed(() => totalCount.value)

function lessonCompleted(lessonId) {
  // Simplified: check if progress was recorded
  return false
}

function typeIcon(type) {
  const map = { '视频': '🎬', '文档': '📄', '图片': '🖼', '音频': '🎵', '其他': '📁' }
  return map[type] || '📁'
}

async function loadCourse() {
  loading.value = true
  try {
    const res = await getCourseDetail(route.params.id)
    course.value = res.data || {}
  } catch {
    ElMessage.error('加载课程失败')
  } finally {
    loading.value = false
  }
}

async function loadChapters() {
  try {
    const res = await getChaptersByCourse(route.params.id)
    const list = res.data || []
    chapters.value = list.map(ch => ({ ...ch, expanded: false, lessons: [] }))
    // Lazy load lessons when expanding
  } catch {
    chapters.value = []
  }
}

async function loadProgress() {
  if (!studentId.value) return
  try {
    const res = await getProgress(course.value?.id)
    const data = res.data
    if (data) {
      completedCount.value = data.completedLessons || 0
      totalCount.value = data.totalLessons || 0
    }
  } catch {
    // progress not yet recorded
  }
}

async function loadLesson(lesson) {
  currentLesson.value = lesson

  // Load video progress for resume
  try {
    const res = await getVideoProgress(lesson.id)
    if (res.data && res.data.progressPoint) {
      // Will seek after video loads
      setTimeout(() => {
        if (videoRef.value) {
          videoRef.value.currentTime = res.data.progressPoint
        }
      }, 500)
    }
  } catch { /* no progress recorded */ }
}

let progressTimer = null
function onTimeUpdate() {
  if (!videoRef.value || !currentLesson.value) return
  const time = videoRef.value.currentTime
  const dur = videoRef.value.duration || 0

  if (progressTimer) clearInterval(progressTimer)
  progressTimer = setInterval(async () => {
    try {
      await recordVideoProgress({
        lessonId: currentLesson.value.id,
        progressPoint: time,
        duration: Math.round(dur),
      })
    } catch { /* silent */ }
    if (progressTimer) clearInterval(progressTimer)
    progressTimer = null
  }, 5000)
}

function onVideoLoaded() {
  // Could load saved progress here
}

onMounted(() => {
  loadCourse()
  loadChapters()
  loadProgress()
})
</script>

<style lang="scss" scoped>
.page-learning { padding-bottom: 32px; }

.course-header {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);

  h1 { font-size: 22px; font-weight: 600; color: #303133; margin-bottom: 12px; }
  .course-meta { display: flex; gap: 12px; align-items: center; color: #606266; font-size: 14px; margin-bottom: 16px; }

  .progress-bar-wrap { margin-top: 8px; }
  .progress-text { font-size: 12px; color: #909399; margin-top: 4px; display: block; }
}

.learning-body {
  display: flex;
  gap: 24px;
}

.sidebar {
  width: 280px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  max-height: calc(100vh - 140px);
  overflow-y: auto;

  .sidebar-header { margin-bottom: 16px; }
  .sidebar-header h3 { font-size: 16px; font-weight: 600; color: #303133; }
}

.chapter-group { margin-bottom: 4px; }

.chapter-title {
  padding: 10px 12px;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  cursor: pointer;
  border-radius: 6px;
  transition: background 0.2s;

  &:hover { background: #f5f7fa; }

  .expand-icon { display: inline-block; width: 20px; transition: transform 0.2s; }
}

.chapter-title.is-expanded .expand-icon { transform: rotate(0deg); }

.lesson-list { padding-left: 24px; }

.lesson-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  font-size: 13px;
  color: #606266;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;

  &:hover { background: #f5f7fa; color: #409EFF; }
  &.active { background: #ecf5ff; color: #409EFF; font-weight: 500; }
  &.completed { color: #67c23a; }

  .lesson-icon { font-size: 12px; }
  .lesson-name { flex: 1; }
  .lesson-duration { font-size: 11px; color: #c0c4cc; }
}

.content-area {
  flex: 1;
  min-width: 0;
}

.video-player {
  background: #000;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 16px;

  video {
    width: 100%;
    max-height: 480px;
    display: block;
  }
}

.resource-preview {
  background: #fff;
  border-radius: 12px;
  padding: 48px;
  text-align: center;
  margin-bottom: 16px;

  .resource-icon { font-size: 64px; margin-bottom: 16px; }
  h3 { font-size: 18px; font-weight: 600; color: #303133; margin-bottom: 12px; }
  p { font-size: 14px; color: #909399; margin-bottom: 24px; }

  .resource-link {
    display: inline-block;
    color: #409EFF;
    font-size: 14px;
    text-decoration: none;
    &:hover { text-decoration: underline; }
  }
}

.lesson-info {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);

  h2 { font-size: 18px; font-weight: 600; color: #303133; margin-bottom: 12px; }
  p { font-size: 14px; color: #606266; line-height: 1.6; }
}

.empty-state {
  background: #fff;
  border-radius: 12px;
  padding: 60px;
  text-align: center;
}

@media (max-width: 768px) {
  .learning-body { flex-direction: column; }
  .sidebar { width: 100%; max-height: 300px; }
}
</style>

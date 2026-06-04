<template>
  <div class="page-courses">
    <h2 class="page-title">📚 课程中心</h2>

    <el-tabs v-model="activeTab" class="course-tabs" @tab-change="handleTabChange">
      <!-- Tab 1: 所有课程 -->
      <el-tab-pane label="所有课程" name="all">
        <div class="search-bar">
          <el-input v-model="keyword" placeholder="搜索课程名称" style="width: 240px" clearable @clear="fetchAllCourses" />
          <el-select v-model="typeFilter" placeholder="课程类型" clearable @change="fetchAllCourses" style="width: 140px">
            <el-option label="必修" value="必修" />
            <el-option label="选修" value="选修" />
            <el-option label="公共" value="公共" />
          </el-select>
          <el-button type="primary" @click="fetchAllCourses">搜索</el-button>
        </div>

        <div v-loading="loading" class="course-grid">
          <el-card v-for="c in allList" :key="c.id" class="course-card" shadow="hover">
            <h4>{{ c.name }}</h4>
            <p class="teacher">{{ c.teacherName || '待定' }}</p>
            <div class="meta">
              <el-tag size="small">{{ c.type }}</el-tag>
              <span class="credit">{{ c.credit }} 学分 / {{ c.hours }} 课时</span>
            </div>
            <p class="desc">{{ c.description || '暂无描述' }}</p>
            <div class="card-actions">
              <el-button
                :type="enrolledMap[c.id] ? 'success' : 'primary'"
                size="small"
                :disabled="enrolledMap[c.id]"
                :loading="enrollingId === c.id"
                @click="handleEnroll(c)"
              >
                {{ enrolledMap[c.id] ? '已选课' : '选课' }}
              </el-button>
            </div>
          </el-card>
          <el-empty v-if="!loading && allList.length === 0" description="暂无课程" />
        </div>

        <div class="pagination-wrap" v-if="allTotal > allPageSize">
          <el-pagination
            v-model:current-page="allPageNo"
            v-model:page-size="allPageSize"
            :total="allTotal"
            :page-sizes="[8, 12, 20]"
            layout="total, sizes, prev, pager, next"
            background
            @change="fetchAllCourses"
          />
        </div>
      </el-tab-pane>

      <!-- Tab 2: 推荐课程 -->
      <el-tab-pane label="推荐课程" name="recommend">
        <div v-loading="recommendLoading" class="course-grid">
          <template v-if="recommendList.length > 0">
            <el-card v-for="c in recommendList" :key="c.id" class="course-card" shadow="hover">
              <h4>{{ c.name }}</h4>
              <p class="teacher">{{ c.teacherName || '待定' }}</p>
              <div class="meta">
                <el-tag size="small">{{ c.type }}</el-tag>
                <span class="credit">{{ c.credit }} 学分 / {{ c.hours }} 课时</span>
                <el-tag size="small" type="info" v-if="c.enrollCount !== undefined">选课 {{ c.enrollCount }} 人</el-tag>
              </div>
              <p class="desc">{{ c.description || '暂无描述' }}</p>
              <div class="card-actions">
                <el-button
                  :type="enrolledMap[c.id] ? 'success' : 'primary'"
                  size="small"
                  :disabled="enrolledMap[c.id]"
                  :loading="enrollingId === c.id"
                  @click="handleEnroll(c)"
                >
                  {{ enrolledMap[c.id] ? '已选课' : '选课' }}
                </el-button>
              </div>
            </el-card>
          </template>
          <el-empty v-else-if="!recommendLoading" description="暂无推荐，先去选一些课程吧" />
        </div>
      </el-tab-pane>

      <!-- Tab 3: 我的课程 -->
      <el-tab-pane label="我的课程" name="my">
        <div v-loading="myLoading" class="course-grid">
          <template v-if="myList.length > 0">
            <el-card v-for="c in myList" :key="c.id" class="course-card" shadow="hover">
              <h4>{{ c.name }}</h4>
              <p class="teacher">{{ c.teacherName || '待定' }}</p>
              <div class="meta">
                <el-tag size="small">{{ c.type }}</el-tag>
                <span class="credit">{{ c.credit }} 学分 / {{ c.hours }} 课时</span>
              </div>
              <p class="desc">{{ c.description || '暂无描述' }}</p>
              <div class="card-actions">
                <el-button
                  type="danger"
                  size="small"
                  plain
                  :loading="droppingId === c.id"
                  @click="handleDrop(c)"
                >退课</el-button>
              </div>
            </el-card>
          </template>
          <el-empty v-else-if="!myLoading" description="还没有选课，去课程列表看看吧" />
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'
import { enrollCourse, dropCourse, getMyCourses } from '@/api/enrollment'
import { getRecommendCourses } from '@/api/recommend'

const route = useRoute()

const activeTab = ref(route.query.tab === 'recommend' ? 'recommend' : 'all')

// All courses
const keyword = ref('')
const typeFilter = ref('')
const loading = ref(false)
const allList = ref([])
const allTotal = ref(0)
const allPageNo = ref(1)
const allPageSize = ref(8)

// Recommend courses
const recommendLoading = ref(false)
const recommendList = ref([])

// My courses
const myLoading = ref(false)
const myList = ref([])

// Enrollment state
const enrolledMap = ref({})
const enrollingId = ref(null)
const droppingId = ref(null)

// Fetch all enrolled course IDs to build enrolledMap
async function fetchEnrolledMap() {
  try {
    const res = await getMyCourses()
    const courses = res.data || []
    const map = {}
    courses.forEach(c => { map[c.id] = true })
    enrolledMap.value = map
    myList.value = courses
  } catch (e) {
    // not logged in or no enrollments
  }
}

async function fetchAllCourses() {
  loading.value = true
  try {
    const res = await request.get('/courses/page', {
      params: { keyword: keyword.value, type: typeFilter.value, pageNo: allPageNo.value, pageSize: allPageSize.value },
    })
    allList.value = res.data?.list || []
    allTotal.value = res.data?.totalCount || 0
  } catch (e) { /* ignored */ }
  finally { loading.value = false }
}

async function fetchRecommend() {
  recommendLoading.value = true
  try {
    const res = await getRecommendCourses(8)
    recommendList.value = res.data || []
  } catch (e) { recommendList.value = [] }
  finally { recommendLoading.value = false }
}

async function fetchMyCourses() {
  myLoading.value = true
  try {
    const res = await getMyCourses()
    myList.value = res.data || []
    const map = {}
    myList.value.forEach(c => { map[c.id] = true })
    enrolledMap.value = { ...enrolledMap.value, ...map }
  } catch (e) { myList.value = [] }
  finally { myLoading.value = false }
}

async function handleEnroll(course) {
  enrollingId.value = course.id
  try {
    await enrollCourse(course.id)
    ElMessage.success(`选课成功：${course.name}`)
    enrolledMap.value = { ...enrolledMap.value, [course.id]: true }
    // Refresh my courses
    await fetchMyCourses()
  } catch (e) {
    const msg = e.response?.data?.msg || e.message || '选课失败'
    ElMessage.error(msg)
  }
  finally { enrollingId.value = null }
}

async function handleDrop(course) {
  try {
    await ElMessageBox.confirm(`确定退课「${course.name}」？`, '确认退课', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
    })
  } catch { return }

  droppingId.value = course.id
  try {
    await dropCourse(course.id)
    ElMessage.success(`退课成功：${course.name}`)
    delete enrolledMap.value[course.id]
    enrolledMap.value = { ...enrolledMap.value }
    // Refresh
    await fetchMyCourses()
    if (activeTab.value === 'all') await fetchAllCourses()
    else if (activeTab.value === 'recommend') await fetchRecommend()
  } catch (e) {
    ElMessage.error(e.response?.data?.msg || '退课失败')
  }
  finally { droppingId.value = null }
}

function handleTabChange(tab) {
  if (tab === 'all' && allList.value.length === 0) fetchAllCourses()
  if (tab === 'recommend' && recommendList.value.length === 0) fetchRecommend()
  if (tab === 'my' && myList.value.length === 0) fetchMyCourses()
}

onMounted(async () => {
  await Promise.all([fetchAllCourses(), fetchEnrolledMap()])
  if (activeTab.value === 'recommend') fetchRecommend()
})
</script>

<style lang="scss" scoped>
.page-courses { padding-bottom: 32px; }
.page-title { font-size: 22px; font-weight: 600; color: #303133; margin-bottom: 4px; }

.course-tabs {
  :deep(.el-tabs__header) { margin-bottom: 20px; }
}

.search-bar {
  display: flex; gap: 12px; margin-bottom: 20px; align-items: center;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  min-height: 200px;
}

.course-card {
  display: flex;
  flex-direction: column;

  h4 { font-size: 16px; font-weight: 600; color: #303133; margin-bottom: 8px; }
  .teacher { font-size: 13px; color: #909399; margin-bottom: 12px; }
  .meta { display: flex; gap: 8px; align-items: center; margin-bottom: 10px; flex-wrap: wrap; }
  .credit { font-size: 12px; color: #c0c4cc; }
  .desc { font-size: 13px; color: #606266; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; flex: 1; }

  .card-actions {
    margin-top: 12px;
    padding-top: 12px;
    border-top: 1px solid #f0f0f0;
  }
}

.pagination-wrap {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>

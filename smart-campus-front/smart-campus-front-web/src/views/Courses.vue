<template>
  <div class="page-courses">
    <h2 class="page-title">📚 课程中心</h2>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索课程名称" style="width: 240px" clearable @clear="fetchData" />
      <el-select v-model="type" placeholder="课程类型" clearable @change="fetchData" style="width: 140px">
        <el-option label="必修" value="必修" />
        <el-option label="选修" value="选修" />
        <el-option label="公共" value="公共" />
      </el-select>
      <el-button type="primary" @click="fetchData">搜索</el-button>
    </div>

    <div v-loading="loading" class="course-grid">
      <el-card v-for="c in list" :key="c.id" class="course-card" shadow="hover">
        <h4>{{ c.name }}</h4>
        <p class="teacher">{{ c.teacherName || '待定' }}</p>
        <div class="meta">
          <el-tag size="small">{{ c.type }}</el-tag>
          <span class="credit">{{ c.credit }} 学分 / {{ c.hours }} 课时</span>
        </div>
        <p class="desc">{{ c.description || '暂无描述' }}</p>
      </el-card>
      <el-empty v-if="!loading && list.length === 0" description="暂无课程" />
    </div>

    <div class="pagination-wrap" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="pageNo"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[8, 12, 20]"
        layout="total, sizes, prev, pager, next"
        background
        @change="fetchData"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'

const keyword = ref('')
const type = ref('')
const loading = ref(false)
const list = ref([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(8)

async function fetchData() {
  loading.value = true
  try {
    const res = await request.get('/courses/page', {
      params: { keyword: keyword.value, type: type.value, pageNo: pageNo.value, pageSize: pageSize.value },
    })
    list.value = res.data?.list || []
    total.value = res.data?.totalCount || 0
  } catch (e) { /* ignored */ }
  finally { loading.value = false }
}

onMounted(fetchData)
</script>

<style lang="scss" scoped>
.page-courses { padding-bottom: 32px; }
.page-title { font-size: 22px; font-weight: 600; color: #303133; margin-bottom: 20px; }

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
  h4 { font-size: 16px; font-weight: 600; color: #303133; margin-bottom: 8px; }
  .teacher { font-size: 13px; color: #909399; margin-bottom: 12px; }
  .meta { display: flex; gap: 8px; align-items: center; margin-bottom: 10px; }
  .credit { font-size: 12px; color: #c0c4cc; }
  .desc { font-size: 13px; color: #606266; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
}

.pagination-wrap {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>

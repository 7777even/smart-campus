<template>
  <div class="page-announcements">
    <h2 class="page-title">📢 校园公告</h2>

    <div class="filter-bar">
      <el-select v-model="level" placeholder="公告级别" clearable @change="fetchData" style="width: 140px">
        <el-option label="紧急" value="紧急" />
        <el-option label="重要" value="重要" />
        <el-option label="普通" value="普通" />
      </el-select>
    </div>

    <div v-loading="loading">
      <div v-for="item in list" :key="item.id" class="announce-card" @click="handleDetail(item)">
        <div class="announce-head">
          <el-tag :type="tagType(item.level)" size="small">{{ item.level }}</el-tag>
          <span class="announce-title">{{ item.title }}</span>
          <span class="announce-date">{{ item.createTime }}</span>
        </div>
        <p class="announce-content">{{ item.content?.substring(0, 120) }}...</p>
        <span class="announce-publisher">发布人: {{ item.publisher || '教务处' }}</span>
      </div>
      <el-empty v-if="!loading && list.length === 0" description="暂无公告" />
    </div>

    <el-dialog v-model="detailVisible" :title="detailItem.title" width="700px">
      <p style="color: #909399; font-size: 13px; margin-bottom: 16px;">
        {{ detailItem.publisher }} · {{ detailItem.createTime }}
        <el-tag :type="tagType(detailItem.level)" size="small" style="margin-left: 8px">{{ detailItem.level }}</el-tag>
      </p>
      <div style="white-space: pre-wrap; line-height: 1.8; color: #303133;">{{ detailItem.content }}</div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'

const level = ref('')
const loading = ref(false)
const list = ref([])
const detailVisible = ref(false)
const detailItem = ref({})

function tagType(lv) {
  return lv === '紧急' ? 'danger' : lv === '重要' ? 'warning' : 'info'
}

async function fetchData() {
  loading.value = true
  try {
    const res = await request.get('/announcements', {
      params: { level: level.value, status: '已发布', pageSize: 50 },
    })
    list.value = res.data?.list || []
  } catch (e) { /* ignored */ }
  finally { loading.value = false }
}

function handleDetail(item) {
  detailItem.value = item
  detailVisible.value = true
}

onMounted(fetchData)
</script>

<style lang="scss" scoped>
.page-announcements { padding-bottom: 32px; }
.page-title { font-size: 22px; font-weight: 600; color: #303133; margin-bottom: 20px; }
.filter-bar { margin-bottom: 16px; }

.announce-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 12px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
  cursor: pointer;
  transition: all 0.25s;

  &:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.08); transform: translateY(-1px); }
}

.announce-head {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;

  .announce-title { flex: 1; font-size: 15px; font-weight: 600; color: #303133; }
  .announce-date { font-size: 12px; color: #c0c4cc; white-space: nowrap; }
}

.announce-content {
  font-size: 14px; color: #606266; line-height: 1.6; margin-bottom: 8px;
}

.announce-publisher {
  font-size: 12px; color: #c0c4cc;
}
</style>

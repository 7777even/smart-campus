<template>
  <div class="page">
    <div class="page-header">
      <h2 class="page-title">资源管理</h2>
      <div class="page-header__actions">
        <el-upload :show-file-list="false" :before-upload="handleUpload">
          <el-button type="primary">上传资源</el-button>
        </el-upload>
      </div>
    </div>
    <div class="search-bar">
      <el-input v-model="searchForm.keyword" placeholder="资源名称" style="width:200px" size="default" clearable @clear="fetchData" />
      <el-select v-model="searchForm.type" placeholder="资源类型" style="width:130px" size="default" clearable @change="fetchData">
        <el-option label="视频" value="视频" />
        <el-option label="文档" value="文档" />
        <el-option label="图片" value="图片" />
        <el-option label="音频" value="音频" />
        <el-option label="其他" value="其他" />
      </el-select>
      <el-select v-model="searchForm.category" placeholder="资源分类" style="width:130px" size="default" clearable @change="fetchData">
        <el-option label="课程资料" value="课程资料" />
        <el-option label="课件" value="课件" />
        <el-option label="习题" value="习题" />
        <el-option label="参考书" value="参考书" />
      </el-select>
      <el-button type="primary" @click="fetchData">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>
    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card"><span class="stat-label">资源总数</span><span class="stat-value">{{ tableData.totalCount }}</span></div>
      <div class="stat-card"><span class="stat-label">视频</span><span class="stat-value" style="color:#409EFF">{{ videoCount }}</span></div>
      <div class="stat-card"><span class="stat-label">文档</span><span class="stat-value" style="color:#67C23A">{{ docCount }}</span></div>
      <div class="stat-card"><span class="stat-label">总存储</span><span class="stat-value" style="color:#E6A23C">2.3 GB</span></div>
    </div>
    <BaseDataTable
      ref="tableRef"
      :columns="columns"
      :data="tableData"
      :loading="loading"
      :selectable="true"
      @page-change="onPageChange"
      @selection-change="onSelectionChange"
    >
      <template #type="{ row }">
        <el-tag :type="typeTag(row.type)" size="small">{{ row.type }}</el-tag>
      </template>
      <template #fileSize="{ row }">{{ formatSize(row.fileSize) }}</template>
      <template #status="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
          {{ row.status === 1 ? '已发布' : '草稿' }}
        </el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link size="small" @click="handleView(row)">查看</el-button>
        <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
        <el-button type="primary" link size="small" @click="handleDownload(row)">下载</el-button>
        <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
      </template>
    </BaseDataTable>

    <BaseDialog
      v-model:visible="dialogVisible"
      :title="isEditing ? '编辑资源' : '资源详情'"
      width="600px"
      :confirm-loading="confirmLoading"
      :show-cancel="isEditing"
      :show-confirm="isEditing"
      confirm-text="保存"
      @confirm="handleConfirm"
      @cancel="dialogVisible = false"
    >
      <el-form v-if="isEditing" :model="form" label-width="100px" label-position="right">
        <el-form-item label="资源名称">
          <el-input v-model="form.name" placeholder="请输入资源名称" />
        </el-form-item>
        <el-form-item label="资源类型">
          <el-select v-model="form.type" style="width:100%">
            <el-option label="视频" value="视频" />
            <el-option label="文档" value="文档" />
            <el-option label="图片" value="图片" />
            <el-option label="音频" value="音频" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="资源分类">
          <el-select v-model="form.category" style="width:100%">
            <el-option label="课程资料" value="课程资料" />
            <el-option label="课件" value="课件" />
            <el-option label="习题" value="习题" />
            <el-option label="参考书" value="参考书" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">已发布</el-radio>
            <el-radio :value="0">草稿</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入资源描述" />
        </el-form-item>
      </el-form>
      <el-descriptions v-else :column="1" border>
        <el-descriptions-item label="资源名称">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="资源类型">{{ detailData.type }}</el-descriptions-item>
        <el-descriptions-item label="资源分类">{{ detailData.category }}</el-descriptions-item>
        <el-descriptions-item label="文件大小">{{ formatSize(detailData.fileSize) }}</el-descriptions-item>
        <el-descriptions-item label="上传者">{{ detailData.uploader }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detailData.status === 1 ? '已发布' : '草稿' }}</el-descriptions-item>
        <el-descriptions-item label="上传时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="描述">{{ detailData.description || '-' }}</el-descriptions-item>
      </el-descriptions>
    </BaseDialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getResources, updateResource, deleteResource, uploadResource } from '@/api/resource.js'
import BaseDataTable from '@/components/BaseDataTable.vue'
import BaseDialog from '@/components/BaseDialog.vue'

const tableRef = ref(null)
const loading = ref(false)
const dialogVisible = ref(false)
const confirmLoading = ref(false)
const isEditing = ref(false)
const editingId = ref(null)

const searchForm = reactive({ keyword: '', type: '', category: '' })
const form = reactive({ name: '', type: '文档', category: '课件', status: 1, description: '' })
const detailData = reactive({})

const videoCount = computed(() => tableData.list.filter(r => r.type === '视频').length)
const docCount = computed(() => tableData.list.filter(r => r.type === '文档').length)

function typeTag(t) {
  const map = { '视频': 'primary', '文档': 'success', '图片': 'warning', '音频': 'info', '其他': 'info' }
  return map[t] || 'info'
}

function formatSize(bytes) {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / 1024 / 1024).toFixed(1) + ' MB'
}

const columns = [
  { label: '资源名称', prop: 'name', minWidth: 200 },
  { label: '资源类型', prop: 'type', width: 100 },
  { label: '分类', prop: 'category', width: 100 },
  { label: '文件大小', prop: 'fileSize', width: 100 },
  { label: '上传者', prop: 'uploader', width: 120 },
  { label: '状态', prop: 'status', width: 90 },
  { label: '上传时间', prop: 'createTime', width: 170 },
  { label: '操作', prop: 'action', width: 260, fixed: 'right' },
]

const tableData = reactive({ totalCount: 0, pageSize: 15, pageNo: 1, pageTotal: 0, list: [] })

async function fetchData() {
  loading.value = true
  try {
    const params = { pageNo: tableData.pageNo, pageSize: tableData.pageSize }
    if (searchForm.keyword) params.keyword = searchForm.keyword
    if (searchForm.type) params.type = searchForm.type
    if (searchForm.category) params.category = searchForm.category
    const res = await getResources(params)
    tableData.list = res.data.list || []
    tableData.totalCount = res.data.totalCount || 0
    tableData.pageTotal = res.data.pageTotal || Math.ceil((res.data.totalCount || 0) / tableData.pageSize) || 0
  } catch (e) {
    // 错误由响应拦截器统一处理
  } finally {
    loading.value = false
  }
}

function resetSearch() { searchForm.keyword = ''; searchForm.type = ''; searchForm.category = ''; fetchData() }
function onPageChange({ pageNo, pageSize }) { tableData.pageNo = pageNo; tableData.pageSize = pageSize; fetchData() }
function onSelectionChange(selection) { console.log('选中:', selection) }

async function handleUpload(file) {
  try {
    const formData = new FormData()
    formData.append('file', file)
    await uploadResource(formData)
    ElMessage.success(`文件 "${file.name}" 上传成功`)
    fetchData()
  } catch (e) {
    // 错误由响应拦截器统一处理
  }
  return false
}

function handleEdit(row) {
  isEditing.value = true
  editingId.value = row.id
  Object.assign(form, row)
  dialogVisible.value = true
}

function handleView(row) {
  isEditing.value = false
  Object.assign(detailData, row)
  dialogVisible.value = true
}

function handleDownload(row) {
  ElMessage.success(`正在下载「${row.name}」（模拟）`)
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除资源「${row.name}」吗？`, '删除确认', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
    })
    await deleteResource(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) {
    // 取消操作或删除失败，错误由响应拦截器处理
  }
}

async function handleConfirm() {
  confirmLoading.value = true
  try {
    await updateResource(editingId.value, { ...form })
    ElMessage.success('编辑成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    // 错误由响应拦截器统一处理
  } finally {
    confirmLoading.value = false
  }
}

onMounted(fetchData)
</script>

<style lang="scss" scoped>
.page {
  height: 100%; display: flex; flex-direction: column;
  .page-header {
    display: flex; justify-content: space-between; align-items: center; flex-shrink: 0; margin-bottom: 16px;
    &__actions { display: flex; gap: 10px; }
  }
  .page-title { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
  .search-bar { display: flex; gap: 10px; margin-bottom: 16px; flex-shrink: 0; align-items: center; }
}
.stat-cards { display: flex; gap: 16px; margin-bottom: 16px; flex-shrink: 0;
  .stat-card {
    flex: 1; background: #f9fafc; border-radius: 8px; padding: 16px 20px; display: flex; flex-direction: column; gap: 6px;
    .stat-label { font-size: 13px; color: #909399; }
    .stat-value { font-size: 24px; font-weight: 700; color: #303133; }
  }
}
</style>

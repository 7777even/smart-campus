<template>
  <div class="page">
    <div class="page-header">
      <h2 class="page-title">公告管理</h2>
      <div class="page-header__actions">
        <el-button type="primary" @click="openAddDialog">发布公告</el-button>
      </div>
    </div>
    <div class="search-bar">
      <el-input v-model="searchForm.keyword" placeholder="公告标题" style="width:200px" size="default" clearable @clear="fetchData" />
      <el-select v-model="searchForm.level" placeholder="紧急程度" style="width:120px" size="default" clearable @change="fetchData">
        <el-option label="紧急" value="紧急" />
        <el-option label="重要" value="重要" />
        <el-option label="普通" value="普通" />
      </el-select>
      <el-select v-model="searchForm.status" placeholder="状态" style="width:100px" size="default" clearable @change="fetchData">
        <el-option label="已发布" value="已发布" />
        <el-option label="草稿" value="草稿" />
      </el-select>
      <el-button type="primary" @click="fetchData">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
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
      <template #title="{ row }">
        <div class="title-cell">
          <el-tag v-if="row.level === '紧急'" size="small" type="danger" effect="dark" class="level-tag">紧急</el-tag>
          <el-tag v-else-if="row.level === '重要'" size="small" type="warning" class="level-tag">重要</el-tag>
          <span class="title-text">{{ row.title }}</span>
        </div>
      </template>
      <template #status="{ row }">
        <el-tag :type="row.status === '已发布' ? 'success' : 'info'" size="small">{{ row.status }}</el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link size="small" @click="handleView(row)">查看</el-button>
        <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
        <el-button type="primary" link size="small" @click="handleTogglePublish(row)">
          {{ row.status === '已发布' ? '下架' : '发布' }}
        </el-button>
        <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
      </template>
    </BaseDataTable>

    <BaseDialog
      v-model:visible="dialogVisible"
      :title="isEditing ? '编辑公告' : '发布公告'"
      width="800px"
      :confirm-loading="confirmLoading"
      confirm-text="保存"
      @confirm="handleConfirm"
      @cancel="dialogVisible = false"
    >
      <el-form :model="form" label-width="100px" label-position="right">
        <el-form-item label="公告标题">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="紧急程度">
          <el-radio-group v-model="form.level">
            <el-radio label="普通">普通</el-radio>
            <el-radio label="重要">重要</el-radio>
            <el-radio label="紧急">紧急</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="已发布">立即发布</el-radio>
            <el-radio label="草稿">保存草稿</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="公告内容">
          <el-input v-model="form.content" type="textarea" :rows="10" placeholder="请输入公告内容" />
        </el-form-item>
      </el-form>
    </BaseDialog>

    <!-- 公告详情 -->
    <BaseDrawer
      v-model:visible="drawerVisible"
      title="公告详情"
      size="600px"
      :show-confirm="false"
      cancel-text="关闭"
      @cancel="drawerVisible = false"
    >
      <div class="announcement-detail">
        <div class="ad-header">
          <h3>{{ detailData.title }}</h3>
          <div class="ad-meta">
            <el-tag v-if="detailData.level === '紧急'" type="danger" size="small">紧急</el-tag>
            <el-tag v-else-if="detailData.level === '重要'" type="warning" size="small">重要</el-tag>
            <el-tag v-else size="small">普通</el-tag>
            <span>{{ detailData.publisher }}</span>
            <span>{{ detailData.createTime }}</span>
            <el-tag :type="detailData.status === '已发布' ? 'success' : 'info'" size="small">{{ detailData.status }}</el-tag>
          </div>
        </div>
        <div class="ad-body">{{ detailData.content }}</div>
      </div>
    </BaseDrawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import BaseDataTable from '@/components/BaseDataTable.vue'
import BaseDialog from '@/components/BaseDialog.vue'
import BaseDrawer from '@/components/BaseDrawer.vue'
import { getAnnouncements, createAnnouncement, updateAnnouncement, deleteAnnouncement, togglePublish } from '@/api/announcement'

const tableRef = ref(null)
const loading = ref(false)
const dialogVisible = ref(false)
const drawerVisible = ref(false)
const confirmLoading = ref(false)
const isEditing = ref(false)
const editingId = ref(null)

const searchForm = reactive({ keyword: '', level: '', status: '' })
const form = reactive({ title: '', level: '普通', status: '已发布', content: '' })
const detailData = reactive({})

const columns = [
  { label: '公告标题', prop: 'title', minWidth: 280 },
  { label: '发布人', prop: 'publisher', width: 120 },
  { label: '状态', prop: 'status', width: 90 },
  { label: '发布时间', prop: 'createTime', width: 170 },
  { label: '操作', prop: 'action', width: 280, fixed: 'right' },
]

const tableData = reactive({ totalCount: 0, pageSize: 15, pageNo: 1, pageTotal: 0, list: [] })

async function fetchData() {
  loading.value = true
  try {
    const params = { pageNo: tableData.pageNo, pageSize: tableData.pageSize }
    if (searchForm.keyword) params.keyword = searchForm.keyword
    if (searchForm.level) params.level = searchForm.level
    if (searchForm.status) params.status = searchForm.status
    const res = await getAnnouncements(params)
    Object.assign(tableData, res.data)
  } catch (e) {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

function resetSearch() { searchForm.keyword = ''; searchForm.level = ''; searchForm.status = ''; fetchData() }
function onPageChange({ pageNo, pageSize }) { tableData.pageNo = pageNo; tableData.pageSize = pageSize; fetchData() }
function onSelectionChange(selection) { console.log('选中:', selection) }

function openAddDialog() {
  isEditing.value = false; editingId.value = null
  form.title = ''; form.level = '普通'; form.status = '已发布'; form.content = ''
  dialogVisible.value = true
}
function handleEdit(row) {
  isEditing.value = true; editingId.value = row.id
  Object.assign(form, row)
  dialogVisible.value = true
}
function handleView(row) {
  Object.assign(detailData, row)
  drawerVisible.value = true
}
async function handleTogglePublish(row) {
  const action = row.status === '已发布' ? '下架' : '发布'
  try {
    await ElMessageBox.confirm(`确定${action}公告「${row.title}」？`, '确认', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'info',
    })
    await togglePublish(row.id)
    ElMessage.success(`${action}成功`)
    fetchData()
  } catch (e) {
    if (e !== 'cancel') {
      // handled by interceptor
    }
  }
}
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除公告「${row.title}」？`, '删除确认', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
    })
    await deleteAnnouncement(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) {
    if (e !== 'cancel') {
      // handled by interceptor
    }
  }
}
async function handleConfirm() {
  confirmLoading.value = true
  try {
    if (isEditing.value) {
      await updateAnnouncement(editingId.value, { ...form })
      ElMessage.success('编辑成功')
    } else {
      await createAnnouncement({ ...form })
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    // handled by interceptor
  } finally {
    confirmLoading.value = false
  }
}
onMounted(fetchData)
</script>

<style lang="scss" scoped>
.page { height: 100%; display: flex; flex-direction: column;
  .page-header { display: flex; justify-content: space-between; align-items: center; flex-shrink: 0; margin-bottom: 16px;
    &__actions { display: flex; gap: 10px; } }
  .page-title { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
  .search-bar { display: flex; gap: 10px; margin-bottom: 16px; flex-shrink: 0; align-items: center; } }
.title-cell { display: flex; align-items: center; gap: 6px;
  .level-tag { flex-shrink: 0; }
  .title-text { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; } }
.announcement-detail {
  .ad-header { margin-bottom: 20px;
    h3 { font-size: 18px; color: #303133; margin: 0 0 12px; }
    .ad-meta { display: flex; gap: 12px; align-items: center; font-size: 13px; color: #909399; flex-wrap: wrap; } }
  .ad-body { white-space: pre-wrap; font-size: 14px; line-height: 1.8; color: #303133; padding: 16px; background: #f5f7fa; border-radius: 8px; } }
</style>

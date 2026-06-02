<template>
  <div class="page">
    <div class="page-header">
      <h2 class="page-title">班级管理</h2>
      <div class="page-header__actions">
        <el-button type="primary" @click="openAddDialog">新增班级</el-button>
      </div>
    </div>
    <div class="search-bar">
      <el-input v-model="searchForm.keyword" placeholder="班级名称/编码" style="width:200px" size="default" clearable @clear="fetchData" />
      <el-select v-model="searchForm.departmentId" placeholder="所属院系" style="width:160px" size="default" clearable @change="fetchData">
        <el-option v-for="d in departmentOptions" :key="d.id" :label="d.name" :value="d.id" />
      </el-select>
      <el-select v-model="searchForm.majorId" placeholder="所属专业" style="width:160px" size="default" clearable @change="fetchData">
        <el-option v-for="m in majorOptions" :key="m.id" :label="m.name" :value="m.id" />
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
      <template #departmentId="{ row }">
        {{ getDeptName(row.departmentId) }}
      </template>
      <template #majorId="{ row }">
        {{ getMajorName(row.majorId) }}
      </template>
      <template #status="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
          {{ row.status === 1 ? '启用' : '禁用' }}
        </el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link size="small" @click="handleView(row)">查看</el-button>
        <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
      </template>
    </BaseDataTable>

    <BaseDialog
      v-model:visible="dialogVisible"
      :title="isEditing ? '编辑班级' : '新增班级'"
      width="600px"
      :confirm-loading="confirmLoading"
      confirm-text="保存"
      @confirm="handleConfirm"
      @cancel="dialogVisible = false"
    >
      <el-form :model="form" label-width="100px" label-position="right">
        <el-form-item label="班级名称">
          <el-input v-model="form.name" placeholder="如：2026级软件工程1班" />
        </el-form-item>
        <el-form-item label="班级编码">
          <el-input v-model="form.code" placeholder="请输入班级编码" />
        </el-form-item>
        <el-form-item label="所属院系">
          <el-select v-model="form.departmentId" placeholder="请选择院系" style="width:100%" @change="form.majorId = null">
            <el-option v-for="d in departmentOptions" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属专业">
          <el-select v-model="form.majorId" placeholder="请选择专业" style="width:100%">
            <el-option v-for="m in majorOptions" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="入学年份">
          <el-input-number v-model="form.year" :min="2020" :max="2030" />
        </el-form-item>
        <el-form-item label="学生人数">
          <el-input-number v-model="form.studentCount" :min="0" :max="200" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入班级描述" />
        </el-form-item>
      </el-form>
    </BaseDialog>

    <BaseDrawer
      v-model:visible="drawerVisible"
      title="班级详情"
      size="500px"
      :show-confirm="false"
      cancel-text="关闭"
      @cancel="drawerVisible = false"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="班级名称">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="班级编码">{{ detailData.code }}</el-descriptions-item>
        <el-descriptions-item label="所属院系">{{ getDeptName(detailData.departmentId) }}</el-descriptions-item>
        <el-descriptions-item label="所属专业">{{ getMajorName(detailData.majorId) }}</el-descriptions-item>
        <el-descriptions-item label="入学年份">{{ detailData.year }}</el-descriptions-item>
        <el-descriptions-item label="学生人数">{{ detailData.studentCount }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detailData.status === 1 ? '启用' : '禁用' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="描述">{{ detailData.description || '-' }}</el-descriptions-item>
      </el-descriptions>
    </BaseDrawer>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import BaseDataTable from '@/components/BaseDataTable.vue'
import BaseDialog from '@/components/BaseDialog.vue'
import BaseDrawer from '@/components/BaseDrawer.vue'

const tableRef = ref(null)
const loading = ref(false)
const dialogVisible = ref(false)
const drawerVisible = ref(false)
const confirmLoading = ref(false)
const isEditing = ref(false)
const editingId = ref(null)

const searchForm = reactive({
  keyword: '',
  departmentId: null,
  majorId: null,
})

const form = reactive({
  name: '',
  code: '',
  departmentId: null,
  majorId: null,
  year: 2026,
  studentCount: 0,
  sort: 0,
  description: '',
})

const detailData = reactive({})

const departmentOptions = [
  { id: 1, name: '计算机科学与技术学院' },
  { id: 2, name: '数学与统计学院' },
  { id: 3, name: '外国语学院' },
]

const majorOptions = [
  { id: 1, name: '软件工程', departmentId: 1 },
  { id: 2, name: '计算机科学与技术', departmentId: 1 },
  { id: 3, name: '数据科学', departmentId: 1 },
  { id: 4, name: '数学与应用数学', departmentId: 2 },
  { id: 5, name: '信息与计算科学', departmentId: 2 },
  { id: 6, name: '英语', departmentId: 3 },
  { id: 7, name: '翻译', departmentId: 3 },
]

function getDeptName(id) {
  return departmentOptions.find(d => d.id === id)?.name || '-'
}
function getMajorName(id) {
  return majorOptions.find(m => m.id === id)?.name || '-'
}

const columns = [
  { label: '班级名称', prop: 'name', width: 200 },
  { label: '班级编码', prop: 'code', width: 130 },
  { label: '所属院系', prop: 'departmentId', width: 150 },
  { label: '所属专业', prop: 'majorId', width: 150 },
  { label: '入学年份', prop: 'year', width: 100 },
  { label: '学生人数', prop: 'studentCount', width: 100 },
  { label: '状态', prop: 'status', width: 90 },
  { label: '创建时间', prop: 'createTime', width: 170 },
  { label: '操作', prop: 'action', width: 180, fixed: 'right' },
]

const tableData = reactive({
  totalCount: 0,
  pageSize: 15,
  pageNo: 1,
  pageTotal: 0,
  list: [],
})

function fetchData() {
  loading.value = true
  setTimeout(() => {
    const mockList = []
    const classNames = ['软件工程1班', '软件工程2班', '计算机科学与技术1班', '数据科学1班', '数学1班', '英语1班', '翻译1班', '网络工程1班']
    for (let i = 0; i < 15; i++) {
      const idx = (tableData.pageNo - 1) * 15 + i
      mockList.push({
        id: idx + 1,
        name: classNames[idx % classNames.length] + (idx >= classNames.length ? `(${Math.floor(idx / classNames.length) + 2026})` : ''),
        code: 'CL' + String(idx + 1).padStart(3, '0'),
        departmentId: [1, 1, 1, 1, 2, 3, 3, 1][idx % 8],
        majorId: [1, 1, 2, 3, 4, 6, 7, 8][idx % 8],
        year: 2026 - (idx % 4),
        studentCount: 30 + (idx % 30),
        status: idx % 5 === 0 ? 0 : 1,
        sort: idx + 1,
        createTime: '2026-02-0' + ((idx % 9) + 1) + ' 00:00:00',
        description: '',
      })
    }
    tableData.list = mockList
    tableData.totalCount = 45
    tableData.pageTotal = 3
    loading.value = false
  }, 500)
}

function resetSearch() {
  searchForm.keyword = ''
  searchForm.departmentId = null
  searchForm.majorId = null
  fetchData()
}

function onPageChange({ pageNo, pageSize }) {
  tableData.pageNo = pageNo
  tableData.pageSize = pageSize
  fetchData()
}

function onSelectionChange(selection) {
  console.log('选中行:', selection)
}

function openAddDialog() {
  isEditing.value = false
  editingId.value = null
  form.name = ''
  form.code = ''
  form.departmentId = null
  form.majorId = null
  form.year = 2026
  form.studentCount = 0
  form.sort = 0
  form.description = ''
  dialogVisible.value = true
}

function handleEdit(row) {
  isEditing.value = true
  editingId.value = row.id
  Object.assign(form, row)
  dialogVisible.value = true
}

function handleView(row) {
  Object.assign(detailData, row)
  drawerVisible.value = true
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除「${row.name}」吗？`, '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    const idx = tableData.list.findIndex(item => item.id === row.id)
    if (idx !== -1) tableData.list.splice(idx, 1)
    tableData.totalCount -= 1
    ElMessage.success('删除成功')
  }).catch(() => {})
}

function handleConfirm() {
  confirmLoading.value = true
  setTimeout(() => {
    confirmLoading.value = false
    dialogVisible.value = false
    ElMessage.success(isEditing.value ? '编辑成功' : '新增成功')
    fetchData()
  }, 800)
}

onMounted(fetchData)
</script>

<style lang="scss" scoped>
.page {
  height: 100%;
  display: flex;
  flex-direction: column;
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-shrink: 0;
    margin-bottom: 16px;
    &__actions { display: flex; gap: 10px; }
  }
  .page-title {
    font-size: 20px;
    font-weight: 600;
    color: #303133;
    margin: 0;
  }
  .search-bar {
    display: flex;
    gap: 10px;
    margin-bottom: 16px;
    flex-shrink: 0;
    align-items: center;
  }
}
</style>

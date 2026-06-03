<template>
  <div class="page">
    <div class="page-header">
      <h2 class="page-title">专业管理</h2>
      <div class="page-header__actions">
        <el-button type="primary" @click="openAddDialog">新增专业</el-button>
      </div>
    </div>
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input v-model="searchForm.keyword" placeholder="专业名称/编码" style="width:200px" size="default" clearable @clear="fetchData" />
      <el-select v-model="searchForm.departmentId" placeholder="所属院系" style="width:160px" size="default" clearable @change="fetchData">
        <el-option v-for="d in departmentOptions" :key="d.id" :label="d.name" :value="d.id" />
      </el-select>
      <el-select v-model="searchForm.level" placeholder="学历层次" style="width:130px" size="default" clearable @change="fetchData">
        <el-option label="本科" value="本科" />
        <el-option label="专科" value="专科" />
        <el-option label="硕士" value="硕士" />
        <el-option label="博士" value="博士" />
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

    <!-- 新增/编辑弹窗 -->
    <BaseDialog
      v-model:visible="dialogVisible"
      :title="isEditing ? '编辑专业' : '新增专业'"
      width="600px"
      :confirm-loading="confirmLoading"
      confirm-text="保存"
      @confirm="handleConfirm"
      @cancel="dialogVisible = false"
    >
      <el-form :model="form" label-width="100px" label-position="right">
        <el-form-item label="专业名称">
          <el-input v-model="form.name" placeholder="请输入专业名称" />
        </el-form-item>
        <el-form-item label="专业编码">
          <el-input v-model="form.code" placeholder="请输入专业编码" />
        </el-form-item>
        <el-form-item label="所属院系">
          <el-select v-model="form.departmentId" placeholder="请选择院系" style="width:100%">
            <el-option v-for="d in departmentOptions" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="学历层次">
          <el-select v-model="form.level" placeholder="请选择" style="width:100%">
            <el-option label="本科" value="本科" />
            <el-option label="专科" value="专科" />
            <el-option label="硕士" value="硕士" />
            <el-option label="博士" value="博士" />
          </el-select>
        </el-form-item>
        <el-form-item label="学制年限">
          <el-input-number v-model="form.years" :min="1" :max="5" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入专业描述" />
        </el-form-item>
      </el-form>
    </BaseDialog>

    <!-- 查看详情抽屉 -->
    <BaseDrawer
      v-model:visible="drawerVisible"
      title="专业详情"
      size="500px"
      :show-confirm="false"
      cancel-text="关闭"
      @cancel="drawerVisible = false"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="专业名称">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="专业编码">{{ detailData.code }}</el-descriptions-item>
        <el-descriptions-item label="所属院系">{{ detailData.departmentName }}</el-descriptions-item>
        <el-descriptions-item label="学历层次">{{ detailData.level }}</el-descriptions-item>
        <el-descriptions-item label="学制年限">{{ detailData.years }} 年</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detailData.status === 1 ? '启用' : '禁用' }}</el-descriptions-item>
        <el-descriptions-item label="排序">{{ detailData.sort }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="描述">{{ detailData.description || '-' }}</el-descriptions-item>
      </el-descriptions>
    </BaseDrawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import BaseDataTable from '@/components/BaseDataTable.vue'
import BaseDialog from '@/components/BaseDialog.vue'
import BaseDrawer from '@/components/BaseDrawer.vue'
import { getMajors, createMajor, updateMajor, deleteMajor } from '@/api/major'
import { getDepartments } from '@/api/common'

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
  level: '',
})

const form = reactive({
  name: '',
  code: '',
  departmentId: null,
  level: '本科',
  years: 4,
  sort: 0,
  description: '',
})

const detailData = reactive({})

const departmentOptions = ref([])

function getDeptName(id) {
  return departmentOptions.value.find(d => d.id === id)?.name || '-'
}

const columns = [
  { label: '专业名称', prop: 'name', width: 180 },
  { label: '专业编码', prop: 'code', width: 130 },
  { label: '所属院系', prop: 'departmentId', width: 160 },
  { label: '学历层次', prop: 'level', width: 100 },
  { label: '学制(年)', prop: 'years', width: 90 },
  { label: '状态', prop: 'status', width: 90 },
  { label: '排序', prop: 'sort', width: 70 },
  { label: '创建时间', prop: 'createTime', width: 170 },
  { label: '描述', prop: 'description', minWidth: 180 },
  { label: '操作', prop: 'action', width: 180, fixed: 'right' },
]

const tableData = reactive({
  totalCount: 0,
  pageSize: 15,
  pageNo: 1,
  pageTotal: 0,
  list: [],
})

async function fetchData() {
  loading.value = true
  try {
    const params = { pageNo: tableData.pageNo, pageSize: tableData.pageSize }
    if (searchForm.keyword) params.keyword = searchForm.keyword
    if (searchForm.departmentId) params.departmentId = searchForm.departmentId
    if (searchForm.level) params.level = searchForm.level
    const res = await getMajors(params)
    Object.assign(tableData, res.data)
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchForm.keyword = ''
  searchForm.departmentId = null
  searchForm.level = ''
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
  form.level = '本科'
  form.years = 4
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
  detailData.departmentName = getDeptName(row.departmentId)
  drawerVisible.value = true
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除「${row.name}」吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteMajor(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) {
    // cancelled or error
  }
}

async function handleConfirm() {
  confirmLoading.value = true
  try {
    if (isEditing.value) {
      await updateMajor(editingId.value, { ...form })
      ElMessage.success('更新成功')
    } else {
      await createMajor({ ...form })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } finally {
    confirmLoading.value = false
  }
}

onMounted(async () => {
  await getDepartments().then(res => { departmentOptions.value = res.data || [] }).catch(() => {})
  fetchData()
})
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
    flex-shrink: 0;
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

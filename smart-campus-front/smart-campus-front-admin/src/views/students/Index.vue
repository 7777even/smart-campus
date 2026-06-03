<template>
  <div class="page">
    <div class="page-header">
      <h2 class="page-title">学生管理</h2>
      <div class="page-header__actions">
        <el-button type="primary" @click="openAddDialog">新增学生</el-button>
        <el-button @click="handleImport">导入</el-button>
        <el-button @click="handleExport">导出</el-button>
      </div>
    </div>
    <div class="search-bar">
      <el-input v-model="searchForm.keyword" placeholder="学号/姓名" style="width:180px" size="default" clearable @clear="fetchData" />
      <el-select v-model="searchForm.departmentId" placeholder="所属院系" style="width:150px" size="default" clearable @change="fetchData">
        <el-option v-for="d in departmentOptions" :key="d.id" :label="d.name" :value="d.id" />
      </el-select>
      <el-select v-model="searchForm.majorId" placeholder="所属专业" style="width:150px" size="default" clearable @change="fetchData">
        <el-option v-for="m in majorOptions" :key="m.id" :label="m.name" :value="m.id" />
      </el-select>
      <el-select v-model="searchForm.gender" placeholder="性别" style="width:100px" size="default" clearable @change="fetchData">
        <el-option label="男" value="男" />
        <el-option label="女" value="女" />
      </el-select>
      <el-select v-model="searchForm.status" placeholder="状态" style="width:100px" size="default" clearable @change="fetchData">
        <el-option label="在读" value="在读" />
        <el-option label="休学" value="休学" />
        <el-option label="毕业" value="毕业" />
        <el-option label="退学" value="退学" />
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
      <template #avatar="{ row }">
        <BaseCover :src="row.avatar" width="36px" shape="circle" :shadow="false" />
      </template>
      <template #departmentId="{ row }">{{ getDeptName(row.departmentId) }}</template>
      <template #majorId="{ row }">{{ getMajorName(row.majorId) }}</template>
      <template #gender="{ row }">
        <span :style="{ color: row.gender === '男' ? '#409EFF' : '#F56C6C' }">{{ row.gender }}</span>
      </template>
      <template #status="{ row }">
        <el-tag :type="statusType(row.status)" size="small">{{ row.status }}</el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link size="small" @click="handleView(row)">查看</el-button>
        <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
      </template>
    </BaseDataTable>

    <BaseDialog
      v-model:visible="dialogVisible"
      :title="isEditing ? '编辑学生' : '新增学生'"
      width="700px"
      :confirm-loading="confirmLoading"
      confirm-text="保存"
      @confirm="handleConfirm"
      @cancel="dialogVisible = false"
    >
      <el-form :model="form" label-width="100px" label-position="right">
        <el-form-item label="学号">
          <el-input v-model="form.studentNo" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="所属院系">
          <el-select v-model="form.departmentId" placeholder="请选择" style="width:100%">
            <el-option v-for="d in departmentOptions" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属专业">
          <el-select v-model="form.majorId" placeholder="请选择" style="width:100%">
            <el-option v-for="m in majorOptions" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="form.classId" placeholder="请选择" style="width:100%">
            <el-option v-for="c in classOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="在读" value="在读" />
            <el-option label="休学" value="休学" />
            <el-option label="毕业" value="毕业" />
            <el-option label="退学" value="退学" />
          </el-select>
        </el-form-item>
        <el-form-item label="住址">
          <el-input v-model="form.address" type="textarea" :rows="2" placeholder="请输入住址" />
        </el-form-item>
      </el-form>
    </BaseDialog>

    <BaseDrawer
      v-model:visible="drawerVisible"
      title="学生详情"
      size="500px"
      :show-confirm="false"
      cancel-text="关闭"
      @cancel="drawerVisible = false"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="学号">{{ detailData.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ detailData.gender }}</el-descriptions-item>
        <el-descriptions-item label="所属院系">{{ getDeptName(detailData.departmentId) }}</el-descriptions-item>
        <el-descriptions-item label="所属专业">{{ getMajorName(detailData.majorId) }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailData.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ detailData.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(detailData.status)" size="small">{{ detailData.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="住址">{{ detailData.address || '-' }}</el-descriptions-item>
      </el-descriptions>
    </BaseDrawer>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStudents, createStudent, updateStudent, deleteStudent } from '@/api/student.js'
import { getDepartments, getMajorsByDepartment, getClasses } from '@/api/common.js'
import BaseDataTable from '@/components/BaseDataTable.vue'
import BaseCover from '@/components/BaseCover.vue'
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
  gender: '',
  status: '',
})

const form = reactive({
  studentNo: '',
  name: '',
  gender: '男',
  departmentId: null,
  majorId: null,
  classId: null,
  phone: '',
  email: '',
  status: '在读',
  address: '',
})

const detailData = reactive({})

const departmentOptions = ref([])
const majorOptions = ref([])
const classOptions = ref([])

function getDeptName(id) { return departmentOptions.value.find(d => d.id === id)?.name || '-' }
function getMajorName(id) { return majorOptions.value.find(m => m.id === id)?.name || '-' }

function statusType(s) {
  const map = { '在读': 'success', '休学': 'warning', '毕业': 'info', '退学': 'danger' }
  return map[s] || 'info'
}

const columns = [
  { label: '学号', prop: 'studentNo', width: 130 },
  { label: '姓名', prop: 'name', width: 100 },
  { label: '性别', prop: 'gender', width: 70 },
  { label: '所属院系', prop: 'departmentId', width: 150 },
  { label: '所属专业', prop: 'majorId', width: 150 },
  { label: '手机号', prop: 'phone', width: 130 },
  { label: '邮箱', prop: 'email', minWidth: 160 },
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

async function fetchData() {
  loading.value = true
  try {
    const params = { pageNo: tableData.pageNo, pageSize: tableData.pageSize }
    if (searchForm.keyword) params.keyword = searchForm.keyword
    if (searchForm.departmentId) params.departmentId = searchForm.departmentId
    if (searchForm.majorId) params.majorId = searchForm.majorId
    if (searchForm.gender) params.gender = searchForm.gender
    if (searchForm.status) params.status = searchForm.status
    const res = await getStudents(params)
    tableData.list = res.data.list || []
    tableData.totalCount = res.data.totalCount || 0
    tableData.pageTotal = res.data.pageTotal || Math.ceil((res.data.totalCount || 0) / tableData.pageSize) || 0
  } catch (e) {
    // 错误由响应拦截器统一处理
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchForm.keyword = ''
  searchForm.departmentId = null
  searchForm.majorId = null
  searchForm.gender = ''
  searchForm.status = ''
  fetchData()
}

function onPageChange({ pageNo, pageSize }) {
  tableData.pageNo = pageNo
  tableData.pageSize = pageSize
  fetchData()
}

function onSelectionChange(selection) { console.log('选中:', selection) }

function openAddDialog() {
  isEditing.value = false
  editingId.value = null
  form.studentNo = ''
  form.name = ''
  form.gender = '男'
  form.departmentId = null
  form.majorId = null
  form.classId = null
  form.phone = ''
  form.email = ''
  form.status = '在读'
  form.address = ''
  majorOptions.value = []
  dialogVisible.value = true
}

function handleEdit(row) {
  isEditing.value = true
  editingId.value = row.id
  Object.assign(form, row)
  // 加载该院系下的专业
  if (row.departmentId) {
    getMajorsByDepartment(row.departmentId).then(res => {
      majorOptions.value = (res.data || []).map(m => ({ id: m.id, name: m.name }))
    }).catch(() => { majorOptions.value = [] })
  }
  dialogVisible.value = true
}

function handleView(row) {
  Object.assign(detailData, row)
  drawerVisible.value = true
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除学生「${row.name}」吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteStudent(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) {
    // 取消操作或删除失败，错误由响应拦截器处理
  }
}

async function handleConfirm() {
  confirmLoading.value = true
  try {
    if (isEditing.value) {
      await updateStudent(editingId.value, { ...form })
      ElMessage.success('编辑成功')
    } else {
      await createStudent({ ...form })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    // 错误由响应拦截器统一处理
  } finally {
    confirmLoading.value = false
  }
}

function handleImport() {
  ElMessage.info('导入功能（模拟）')
}
function handleExport() {
  ElMessage.success('导出成功（模拟）')
}

watch(() => form.departmentId, async (newVal) => {
  if (newVal) {
    try {
      const res = await getMajorsByDepartment(newVal)
      majorOptions.value = (res.data || []).map(m => ({ id: m.id, name: m.name }))
    } catch (e) {
      majorOptions.value = []
    }
  } else {
    majorOptions.value = []
    form.majorId = null
  }
})

watch(() => searchForm.departmentId, async (newVal) => {
  if (newVal) {
    try {
      const res = await getMajorsByDepartment(newVal)
      majorOptions.value = (res.data || []).map(m => ({ id: m.id, name: m.name }))
    } catch (e) {
      majorOptions.value = []
    }
  } else {
    majorOptions.value = []
    searchForm.majorId = null
  }
})

async function loadOptions() {
  try {
    const [deptRes, classRes] = await Promise.all([getDepartments(), getClasses()])
    departmentOptions.value = (deptRes.data || []).map(d => ({ id: d.id, name: d.name }))
    classOptions.value = (classRes.data || []).map(c => ({ id: c.id, name: c.name }))
  } catch (e) {
    // 错误由响应拦截器统一处理
  }
}

onMounted(() => {
  loadOptions()
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

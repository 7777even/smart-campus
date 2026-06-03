<template>
  <div class="page">
    <div class="page-header">
      <h2 class="page-title">课程管理</h2>
      <div class="page-header__actions">
        <el-button type="primary" @click="openAddDialog">新增课程</el-button>
      </div>
    </div>
    <div class="search-bar">
      <el-input v-model="searchForm.keyword" placeholder="课程名称/编码" style="width:200px" size="default" clearable @clear="fetchData" />
      <el-select v-model="searchForm.departmentId" placeholder="开课院系" style="width:150px" size="default" clearable @change="fetchData">
        <el-option v-for="d in departmentOptions" :key="d.id" :label="d.name" :value="d.id" />
      </el-select>
      <el-select v-model="searchForm.type" placeholder="课程类型" style="width:120px" size="default" clearable @change="fetchData">
        <el-option label="必修" value="必修" />
        <el-option label="选修" value="选修" />
        <el-option label="公共" value="公共" />
      </el-select>
      <el-select v-model="searchForm.credit" placeholder="学分" style="width:100px" size="default" clearable @change="fetchData">
        <el-option label="1" :value="1" />
        <el-option label="2" :value="2" />
        <el-option label="3" :value="3" />
        <el-option label="4" :value="4" />
        <el-option label="5" :value="5" />
        <el-option label="6" :value="6" />
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
      <template #cover="{ row }">
        <BaseCover :src="row.cover" width="80px" ratio="4:3" radius="4px" :shadow="false" />
      </template>
      <template #departmentId="{ row }">{{ getDeptName(row.departmentId) }}</template>
      <template #type="{ row }">
        <el-tag :type="row.type === '必修' ? 'danger' : row.type === '选修' ? 'primary' : 'success'" size="small">{{ row.type }}</el-tag>
      </template>
      <template #status="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '已开课' : '未开课' }}</el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link size="small" @click="handleView(row)">查看</el-button>
        <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
      </template>
    </BaseDataTable>

    <BaseDialog
      v-model:visible="dialogVisible"
      :title="isEditing ? '编辑课程' : '新增课程'"
      width="650px"
      :confirm-loading="confirmLoading"
      confirm-text="保存"
      @confirm="handleConfirm"
      @cancel="dialogVisible = false"
    >
      <el-form :model="form" label-width="110px" label-position="right">
        <el-form-item label="课程名称">
          <el-input v-model="form.name" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程编码">
          <el-input v-model="form.code" placeholder="请输入课程编码" />
        </el-form-item>
        <el-form-item label="开课院系">
          <el-select v-model="form.departmentId" placeholder="请选择" style="width:100%">
            <el-option v-for="d in departmentOptions" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="授课教师">
          <el-select v-model="form.teacherId" placeholder="请选择" style="width:100%">
            <el-option v-for="t in teacherOptions" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程类型">
          <el-select v-model="form.type" style="width:100%">
            <el-option label="必修" value="必修" />
            <el-option label="选修" value="选修" />
            <el-option label="公共" value="公共" />
          </el-select>
        </el-form-item>
        <el-form-item label="学分">
          <el-input-number v-model="form.credit" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="学时">
          <el-input-number v-model="form.hours" :min="8" :max="128" :step="8" />
        </el-form-item>
        <el-form-item label="上课地点">
          <el-input v-model="form.location" placeholder="如：教学楼301" />
        </el-form-item>
        <el-form-item label="课程描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入课程描述" />
        </el-form-item>
      </el-form>
    </BaseDialog>

    <BaseDrawer
      v-model:visible="drawerVisible"
      title="课程详情"
      size="550px"
      :show-confirm="false"
      cancel-text="关闭"
      @cancel="drawerVisible = false"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="课程编码">{{ detailData.code }}</el-descriptions-item>
        <el-descriptions-item label="课程名称">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="开课院系">{{ getDeptName(detailData.departmentId) }}</el-descriptions-item>
        <el-descriptions-item label="授课教师">{{ detailData.teacherName }}</el-descriptions-item>
        <el-descriptions-item label="课程类型">{{ detailData.type }}</el-descriptions-item>
        <el-descriptions-item label="学分">{{ detailData.credit }}</el-descriptions-item>
        <el-descriptions-item label="学时">{{ detailData.hours }}</el-descriptions-item>
        <el-descriptions-item label="上课地点">{{ detailData.location || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detailData.status === 1 ? '已开课' : '未开课' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="描述">{{ detailData.description || '-' }}</el-descriptions-item>
      </el-descriptions>
    </BaseDrawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCourses, createCourse, updateCourse, deleteCourse } from '@/api/course.js'
import { getDepartments, getTeachers } from '@/api/common.js'
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

const searchForm = reactive({ keyword: '', departmentId: null, type: '', credit: null })
const form = reactive({ name: '', code: '', departmentId: null, teacherId: null, type: '选修', credit: 3, hours: 48, location: '', description: '' })
const detailData = reactive({})

const departmentOptions = ref([])
const teacherOptions = ref([])

function getDeptName(id) { return departmentOptions.value.find(d => d.id === id)?.name || '-' }
function getTeacherName(id) { return teacherOptions.value.find(t => t.id === id)?.name || '-' }

const columns = [
  { label: '课程名称', prop: 'name', width: 200 },
  { label: '课程编码', prop: 'code', width: 120 },
  { label: '开课院系', prop: 'departmentId', width: 150 },
  { label: '授课教师', prop: 'teacherName', width: 100 },
  { label: '类型', prop: 'type', width: 80 },
  { label: '学分', prop: 'credit', width: 70 },
  { label: '学时', prop: 'hours', width: 70 },
  { label: '状态', prop: 'status', width: 90 },
  { label: '创建时间', prop: 'createTime', width: 170 },
  { label: '操作', prop: 'action', width: 180, fixed: 'right' },
]

const tableData = reactive({ totalCount: 0, pageSize: 15, pageNo: 1, pageTotal: 0, list: [] })

async function fetchData() {
  loading.value = true
  try {
    const params = { pageNo: tableData.pageNo, pageSize: tableData.pageSize }
    if (searchForm.keyword) params.keyword = searchForm.keyword
    if (searchForm.departmentId) params.departmentId = searchForm.departmentId
    if (searchForm.type) params.type = searchForm.type
    if (searchForm.credit) params.credit = searchForm.credit
    const res = await getCourses(params)
    tableData.list = res.data.list || []
    tableData.totalCount = res.data.totalCount || 0
    tableData.pageTotal = res.data.pageTotal || Math.ceil((res.data.totalCount || 0) / tableData.pageSize) || 0
  } catch (e) {
    // 错误由响应拦截器统一处理
  } finally {
    loading.value = false
  }
}

function resetSearch() { searchForm.keyword = ''; searchForm.departmentId = null; searchForm.type = ''; searchForm.credit = null; fetchData() }
function onPageChange({ pageNo, pageSize }) { tableData.pageNo = pageNo; tableData.pageSize = pageSize; fetchData() }
function onSelectionChange(selection) { console.log('选中:', selection) }

function openAddDialog() {
  isEditing.value = false; editingId.value = null
  form.name = ''; form.code = ''; form.departmentId = null; form.teacherId = null
  form.type = '选修'; form.credit = 3; form.hours = 48; form.location = ''; form.description = ''
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
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除课程「${row.name}」吗？`, '删除确认', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
    })
    await deleteCourse(row.id)
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
      await updateCourse(editingId.value, { ...form })
      ElMessage.success('编辑成功')
    } else {
      await createCourse({ ...form })
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

async function loadOptions() {
  try {
    const [deptRes, teacherRes] = await Promise.all([getDepartments(), getTeachers()])
    departmentOptions.value = (deptRes.data || []).map(d => ({ id: d.id, name: d.name }))
    teacherOptions.value = (teacherRes.data || []).map(t => ({ id: t.id, name: t.name }))
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
.page { height: 100%; display: flex; flex-direction: column;
  .page-header { display: flex; justify-content: space-between; align-items: center; flex-shrink: 0; margin-bottom: 16px;
    &__actions { display: flex; gap: 10px; } }
  .page-title { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
  .search-bar { display: flex; gap: 10px; margin-bottom: 16px; flex-shrink: 0; align-items: center; } }
</style>

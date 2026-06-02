<template>
  <div class="page">
    <div class="page-header">
      <h2 class="page-title">教师管理</h2>
      <div class="page-header__actions">
        <el-button type="primary" @click="openAddDialog">新增教师</el-button>
        <el-button @click="handleAssign">排课分配</el-button>
      </div>
    </div>
    <div class="search-bar">
      <el-input v-model="searchForm.keyword" placeholder="工号/姓名" style="width:180px" size="default" clearable @clear="fetchData" />
      <el-select v-model="searchForm.departmentId" placeholder="所属院系" style="width:150px" size="default" clearable @change="fetchData">
        <el-option v-for="d in departmentOptions" :key="d.id" :label="d.name" :value="d.id" />
      </el-select>
      <el-select v-model="searchForm.title" placeholder="职称" style="width:120px" size="default" clearable @change="fetchData">
        <el-option label="教授" value="教授" />
        <el-option label="副教授" value="副教授" />
        <el-option label="讲师" value="讲师" />
        <el-option label="助教" value="助教" />
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
      <template #departmentId="{ row }">{{ getDeptName(row.departmentId) }}</template>
      <template #title="{ row }">
        <el-tag :type="titleType(row.title)" size="small">{{ row.title }}</el-tag>
      </template>
      <template #status="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
          {{ row.status === 1 ? '在职' : '离职' }}
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
      :title="isEditing ? '编辑教师' : '新增教师'"
      width="650px"
      :confirm-loading="confirmLoading"
      confirm-text="保存"
      @confirm="handleConfirm"
      @cancel="dialogVisible = false"
    >
      <el-form :model="form" label-width="100px" label-position="right">
        <el-form-item label="工号">
          <el-input v-model="form.teacherNo" placeholder="请输入工号" />
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
        <el-form-item label="职称">
          <el-select v-model="form.title" placeholder="请选择" style="width:100%">
            <el-option label="教授" value="教授" />
            <el-option label="副教授" value="副教授" />
            <el-option label="讲师" value="讲师" />
            <el-option label="助教" value="助教" />
          </el-select>
        </el-form-item>
        <el-form-item label="学历">
          <el-select v-model="form.degree" placeholder="请选择" style="width:100%">
            <el-option label="博士" value="博士" />
            <el-option label="硕士" value="硕士" />
            <el-option label="本科" value="本科" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.intro" type="textarea" :rows="3" placeholder="请输入教师简介" />
        </el-form-item>
      </el-form>
    </BaseDialog>

    <BaseDrawer
      v-model:visible="drawerVisible"
      title="教师详情"
      size="500px"
      :show-confirm="false"
      cancel-text="关闭"
      @cancel="drawerVisible = false"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="工号">{{ detailData.teacherNo }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ detailData.gender }}</el-descriptions-item>
        <el-descriptions-item label="所属院系">{{ getDeptName(detailData.departmentId) }}</el-descriptions-item>
        <el-descriptions-item label="职称">{{ detailData.title }}</el-descriptions-item>
        <el-descriptions-item label="学历">{{ detailData.degree }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailData.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ detailData.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detailData.status === 1 ? '在职' : '离职' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="简介">{{ detailData.intro || '-' }}</el-descriptions-item>
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
  title: '',
})

const form = reactive({
  teacherNo: '',
  name: '',
  gender: '男',
  departmentId: null,
  title: '讲师',
  degree: '硕士',
  phone: '',
  email: '',
  intro: '',
})

const detailData = reactive({})

const departmentOptions = [
  { id: 1, name: '计算机科学与技术学院' },
  { id: 2, name: '数学与统计学院' },
  { id: 3, name: '外国语学院' },
]

function getDeptName(id) { return departmentOptions.find(d => d.id === id)?.name || '-' }

function titleType(t) {
  const map = { '教授': 'danger', '副教授': 'warning', '讲师': 'primary', '助教': 'info' }
  return map[t] || 'primary'
}

const columns = [
  { label: '工号', prop: 'teacherNo', width: 120 },
  { label: '姓名', prop: 'name', width: 100 },
  { label: '性别', prop: 'gender', width: 70 },
  { label: '所属院系', prop: 'departmentId', width: 160 },
  { label: '职称', prop: 'title', width: 90 },
  { label: '学历', prop: 'degree', width: 80 },
  { label: '手机号', prop: 'phone', width: 130 },
  { label: '邮箱', prop: 'email', minWidth: 170 },
  { label: '状态', prop: 'status', width: 80 },
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
    const lastNames = ['张', '李', '王', '赵', '刘', '陈', '杨', '黄', '周', '吴', '徐', '孙', '马', '朱', '胡']
    const titles = ['教授', '副教授', '讲师', '助教']
    const degrees = ['博士', '硕士', '本科']
    for (let i = 0; i < 15; i++) {
      const idx = (tableData.pageNo - 1) * 15 + i
      mockList.push({
        id: idx + 1,
        teacherNo: 'T' + String(idx + 1).padStart(5, '0'),
        name: lastNames[idx % 15] + '教授',
        gender: i % 3 === 0 ? '女' : '男',
        departmentId: [1, 1, 1, 2, 2, 3, 1, 1, 2, 3, 1, 2, 3, 1, 2][idx],
        title: titles[idx % 4],
        degree: degrees[idx % 3],
        phone: '139' + String(20000000 + idx).slice(0, 8),
        email: `teacher${idx + 1}@campus.edu`,
        status: idx % 7 === 0 ? 0 : 1,
        createTime: '2025-09-0' + ((idx % 9) + 1) + ' 00:00:00',
        intro: '主要研究方向为...',
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
  searchForm.title = ''
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
  form.teacherNo = ''
  form.name = ''
  form.gender = '男'
  form.departmentId = null
  form.title = '讲师'
  form.degree = '硕士'
  form.phone = ''
  form.email = ''
  form.intro = ''
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
  ElMessageBox.confirm(`确定要删除教师「${row.name}」吗？`, '删除确认', {
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

function handleAssign() {
  ElMessage.info('排课分配（模拟）')
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
  .page-title { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
  .search-bar { display: flex; gap: 10px; margin-bottom: 16px; flex-shrink: 0; align-items: center; }
}
</style>

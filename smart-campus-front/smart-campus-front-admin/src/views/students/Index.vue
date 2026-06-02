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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
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

const departmentOptions = [
  { id: 1, name: '计算机科学与技术学院' },
  { id: 2, name: '数学与统计学院' },
  { id: 3, name: '外国语学院' },
]
const majorOptions = [
  { id: 1, name: '软件工程', departmentId: 1 },
  { id: 2, name: '计算机科学与技术', departmentId: 1 },
  { id: 3, name: '数学与应用数学', departmentId: 2 },
  { id: 4, name: '英语', departmentId: 3 },
]
const classOptions = [
  { id: 1, name: '软件工程2026级1班' },
  { id: 2, name: '软件工程2026级2班' },
  { id: 3, name: '计算机科学与技术2025级1班' },
]

function getDeptName(id) { return departmentOptions.find(d => d.id === id)?.name || '-' }
function getMajorName(id) { return majorOptions.find(m => m.id === id)?.name || '-' }

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

function fetchData() {
  loading.value = true
  setTimeout(() => {
    const mockList = []
    for (let i = 0; i < 15; i++) {
      const idx = (tableData.pageNo - 1) * 15 + i
      const genders = ['男', '女']
      const statuses = ['在读', '在读', '在读', '休学', '毕业']
      mockList.push({
        id: idx + 1,
        studentNo: '2026' + String(idx + 1).padStart(6, '0'),
        name: ['张三', '李四', '王五', '赵六', '陈七', '周八', '吴九', '郑十', '林小雨', '黄大伟', '刘洋', '孙丽', '杨光', '徐明', '高远'][idx % 15],
        gender: genders[idx % 2],
        departmentId: [1, 1, 1, 2, 2, 3, 3, 1, 1, 2, 3, 1, 2, 1, 3][idx],
        majorId: [1, 1, 2, 3, 3, 4, 4, 1, 1, 3, 4, 2, 3, 1, 4][idx],
        phone: '138' + String(10000000 + idx).slice(0, 8),
        email: `student${idx + 1}@campus.edu`,
        status: statuses[idx % 5],
        createTime: '2026-03-0' + ((idx % 9) + 1) + ' 00:00:00',
        address: idx % 3 === 0 ? '北京市海淀区' : '',
      })
    }
    tableData.list = mockList
    tableData.totalCount = 60
    tableData.pageTotal = 4
    loading.value = false
  }, 500)
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
  ElMessageBox.confirm(`确定要删除学生「${row.name}」吗？`, '删除确认', {
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

function handleImport() {
  ElMessage.info('导入功能（模拟）')
}
function handleExport() {
  ElMessage.success('导出成功（模拟）')
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

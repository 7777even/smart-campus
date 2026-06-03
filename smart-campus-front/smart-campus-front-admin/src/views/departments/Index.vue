<template>
  <div class="page">
    <div class="page-header">
      <h2 class="page-title">院系管理</h2>
      <div class="page-header__actions">
        <el-button type="primary" @click="openAddDialog">新增院系</el-button>
      </div>
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
      <template #status="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
          {{ row.status === 1 ? '启用' : '禁用' }}
        </el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link size="small" @click="handleView(row)">查看详情</el-button>
        <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
      </template>
    </BaseDataTable>

    <!-- Dialog 测试 -->
    <BaseDialog
      v-model:visible="dialogVisible"
      title="新增院系"
      width="600px"
      :top="80"
      :show-cancel="true"
      :show-close="true"
      :confirm-loading="confirmLoading"
      cancel-text="取消"
      confirm-text="保存"
      body-padding="24px"
      @confirm="handleConfirm"
      @cancel="dialogVisible = false"
    >
      <el-form :model="form" label-width="100px" label-position="right">
        <el-form-item label="院系名称">
          <el-input v-model="form.name" placeholder="请输入院系名称" />
        </el-form-item>
        <el-form-item label="院系编码">
          <el-input v-model="form.code" placeholder="请输入院系编码" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="form.leader" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
    </BaseDialog>

    <!-- Drawer 测试 -->
    <BaseDrawer
      v-model:visible="drawerVisible"
      title="院系详情"
      size="500px"
      :show-cancel="true"
      :show-close="true"
      :confirm-loading="drawerConfirmLoading"
      cancel-text="关闭"
      confirm-text="保存"
      body-padding="24px"
      @confirm="handleDrawerConfirm"
      @cancel="drawerVisible = false"
    >
      <el-form :model="drawerForm" label-width="100px" label-position="right">
        <el-form-item label="院系名称">
          <el-input v-model="drawerForm.name" placeholder="请输入院系名称" />
        </el-form-item>
        <el-form-item label="院系编码">
          <el-input v-model="drawerForm.code" placeholder="请输入院系编码" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="drawerForm.leader" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="drawerForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="drawerForm.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="drawerForm.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="drawerForm.remark" type="textarea" :rows="4" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
    </BaseDrawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import BaseDataTable from '@/components/BaseDataTable.vue'
import BaseDialog from '@/components/BaseDialog.vue'
import BaseDrawer from '@/components/BaseDrawer.vue'
import { getDepartments, createDepartment, updateDepartment, deleteDepartment } from '@/api/department'

const tableRef = ref(null)
const loading = ref(false)
const dialogVisible = ref(false)
const confirmLoading = ref(false)
const drawerVisible = ref(false)
const drawerConfirmLoading = ref(false)
const isEditing = ref(false)
const editingId = ref(null)

const form = reactive({
  name: '',
  code: '',
  leader: '',
  phone: '',
  sort: 0,
  description: '',
})

const drawerForm = reactive({
  name: '',
  code: '',
  leader: '',
  phone: '',
  sort: 0,
  description: '',
  remark: '',
})

const columns = [
  { label: '院系名称', prop: 'name', width: 200 },
  { label: '院系编码', prop: 'code', width: 150 },
  { label: '负责人', prop: 'leader', width: 120 },
  { label: '联系电话', prop: 'phone', width: 150 },
  { label: '状态', prop: 'status', width: 100 },
  { label: '排序', prop: 'sort', width: 80 },
  { label: '创建时间', prop: 'createTime', width: 180 },
  { label: '描述', prop: 'description', minWidth: 200 },
  { label: '操作', prop: 'action', width: 230, fixed: 'right' },
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
    const res = await getDepartments({
      pageNo: tableData.pageNo,
      pageSize: tableData.pageSize,
    })
    Object.assign(tableData, res.data)
  } catch (e) {
    // error handled by interceptor
  } finally {
    loading.value = false
  }
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
  form.leader = ''
  form.phone = ''
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
  Object.assign(drawerForm, row)
  drawerVisible.value = true
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确定要删除「${row.name}」吗？`,
      '删除确认',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
    await deleteDepartment(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) {
    if (e !== 'cancel') {
      // error handled by interceptor or already shown
    }
  }
}

async function handleConfirm() {
  confirmLoading.value = true
  try {
    if (isEditing.value) {
      await updateDepartment(editingId.value, { ...form })
      ElMessage.success('更新成功')
    } else {
      await createDepartment({ ...form })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    // error handled by interceptor
  } finally {
    confirmLoading.value = false
  }
}

async function handleDrawerConfirm() {
  drawerConfirmLoading.value = true
  try {
    await updateDepartment(drawerForm.id, { ...drawerForm })
    ElMessage.success('更新成功')
    drawerVisible.value = false
    fetchData()
  } catch (e) {
    // error handled by interceptor
  } finally {
    drawerConfirmLoading.value = false
  }
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

    &__actions {
      display: flex;
      gap: 10px;
    }
  }

  .page-title {
    font-size: 20px;
    font-weight: 600;
    color: #303133;
    margin: 0;
    flex-shrink: 0;
  }
}
</style>

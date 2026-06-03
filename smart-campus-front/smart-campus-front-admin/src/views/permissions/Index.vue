<template>
  <div class="page">
    <div class="page-header">
      <h2 class="page-title">权限管理</h2>
    </div>
    <el-tabs v-model="activeTab" class="perm-tabs">
      <el-tab-pane label="用户管理" name="users">
        <div class="search-bar">
          <el-input v-model="userSearch.keyword" placeholder="用户名" style="width:200px" size="default" clearable @clear="fetchUsers" />
          <el-select v-model="userSearch.role" placeholder="角色" style="width:130px" size="default" clearable @change="fetchUsers">
            <el-option label="超级管理员" value="超级管理员" />
            <el-option label="管理员" value="管理员" />
            <el-option label="教师" value="教师" />
            <el-option label="学生" value="学生" />
          </el-select>
          <el-button type="primary" @click="fetchUsers">查询</el-button>
          <el-button @click="userSearch.keyword = ''; userSearch.role = ''; fetchUsers()">重置</el-button>
        </div>
        <BaseDataTable
          ref="tableRef"
          :columns="userColumns"
          :data="userData"
          :loading="userLoading"
          :selectable="true"
          @page-change="onUserPageChange"
          @selection-change="onSelectionChange"
        >
          <template #role="{ row }">
            <el-tag :type="roleTagType(row.role)" size="small">{{ row.role }}</el-tag>
          </template>
          <template #status="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
          <template #action="{ row }">
            <el-button type="primary" link size="small" @click="handleEditUser(row)">编辑</el-button>
            <el-button :type="row.status === 1 ? 'warning' : 'success'" link size="small" @click="handleToggleUser(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" link size="small" @click="handleDeleteUser(row)">删除</el-button>
          </template>
        </BaseDataTable>
      </el-tab-pane>

      <el-tab-pane label="角色管理" name="roles">
        <div class="tab-header-actions">
          <el-button type="primary" size="small" @click="openAddRole">新增角色</el-button>
        </div>
        <el-table :data="roleData" border stripe style="width:100%" v-loading="roleLoading">
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="name" label="角色名称" width="160" />
          <el-table-column prop="code" label="角色编码" width="150" />
          <el-table-column prop="description" label="描述" min-width="200" />
          <el-table-column prop="userCount" label="用户数" width="80" align="center" />
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleEditRole(row)">编辑</el-button>
              <el-button type="primary" link size="small" @click="handleConfigPerm(row)">配置权限</el-button>
              <el-button type="danger" link size="small" @click="handleDeleteRole(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="菜单权限" name="menus">
        <div class="tab-header-actions">
          <el-button type="primary" size="small" @click="openAddMenu">新增菜单</el-button>
        </div>
        <el-table :data="menuData" border stripe style="width:100%" row-key="id" default-expand-all v-loading="menuLoading" :tree-props="{ children: 'children' }">
          <el-table-column prop="name" label="菜单名称" width="220" />
          <el-table-column prop="icon" label="图标" width="80" align="center">
            <template #default="{ row }">
              <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
            </template>
          </el-table-column>
          <el-table-column prop="path" label="路由路径" width="180" />
          <el-table-column prop="perms" label="权限标识" width="200" />
          <el-table-column prop="sort" label="排序" width="70" align="center" />
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '显示' : '隐藏' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleEditMenu(row)">编辑</el-button>
              <el-button type="danger" link size="small" @click="handleDeleteMenu(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 编辑用户弹窗 -->
    <BaseDialog
      v-model:visible="userDialogVisible"
      title="编辑用户"
      width="500px"
      :confirm-loading="userConfirmLoading"
      confirm-text="保存"
      @confirm="handleUserConfirm"
      @cancel="userDialogVisible = false"
    >
      <el-form :model="userForm" label-width="100px" label-position="right">
        <el-form-item label="用户名">
          <el-input v-model="userForm.username" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="userForm.realName" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="userForm.role" style="width:100%">
            <el-option label="超级管理员" value="超级管理员" />
            <el-option label="管理员" value="管理员" />
            <el-option label="教师" value="教师" />
            <el-option label="学生" value="学生" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="userForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </BaseDialog>

    <!-- 配置权限弹窗 -->
    <BaseDialog
      v-model:visible="permDialogVisible"
      title="配置权限"
      width="450px"
      :confirm-loading="permConfirmLoading"
      confirm-text="保存"
      @confirm="handlePermConfirm"
      @cancel="permDialogVisible = false"
    >
      <div class="perm-tree">
        <el-tree
          ref="permTreeRef"
          :data="permTreeData"
          show-checkbox
          node-key="id"
          default-expand-all
          :props="{ label: 'name', children: 'children' }"
        />
      </div>
    </BaseDialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import BaseDataTable from '@/components/BaseDataTable.vue'
import BaseDialog from '@/components/BaseDialog.vue'
import { getUsers, updateUser, deleteUser, toggleUserStatus, getRoles, getPermissionTree, assignPermissions } from '@/api/permission'

const activeTab = ref('users')

// ==================== Users ====================
const tableRef = ref(null)
const userLoading = ref(false)
const userDialogVisible = ref(false)
const userConfirmLoading = ref(false)
const editingUserId = ref(null)

const userSearch = reactive({ keyword: '', role: '' })
const userForm = reactive({ username: '', realName: '', role: '教师', status: 1 })

function roleTagType(r) {
  return r === '超级管理员' ? 'danger' : r === '管理员' ? 'warning' : r === '教师' ? 'primary' : 'success'
}

const userColumns = [
  { label: '用户名', prop: 'username', width: 150 },
  { label: '真实姓名', prop: 'realName', width: 120 },
  { label: '角色', prop: 'role', width: 120 },
  { label: '邮箱', prop: 'email', width: 200 },
  { label: '状态', prop: 'status', width: 80 },
  { label: '创建时间', prop: 'createTime', width: 170 },
  { label: '操作', prop: 'action', width: 240, fixed: 'right' },
]

const userData = reactive({ totalCount: 0, pageSize: 15, pageNo: 1, pageTotal: 0, list: [] })

async function fetchUsers() {
  userLoading.value = true
  try {
    const params = { pageNo: userData.pageNo, pageSize: userData.pageSize }
    if (userSearch.keyword) params.keyword = userSearch.keyword
    if (userSearch.role) params.role = userSearch.role
    const res = await getUsers(params)
    Object.assign(userData, res.data)
  } catch (e) {
    // handled by interceptor
  } finally {
    userLoading.value = false
  }
}

function onUserPageChange({ pageNo, pageSize }) { userData.pageNo = pageNo; userData.pageSize = pageSize; fetchUsers() }
function onSelectionChange(selection) { console.log('选中:', selection) }

function handleEditUser(row) {
  editingUserId.value = row.id
  Object.assign(userForm, row)
  userDialogVisible.value = true
}
async function handleToggleUser(row) {
  const action = row.status === 1 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定${action}用户「${row.username}」？`, '确认', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
    })
    await toggleUserStatus(row.id)
    ElMessage.success(`${action}成功`)
    fetchUsers()
  } catch (e) {
    if (e !== 'cancel') {
      // handled by interceptor
    }
  }
}
async function handleDeleteUser(row) {
  try {
    await ElMessageBox.confirm(`确定删除用户「${row.username}」？`, '删除确认', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
    })
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    fetchUsers()
  } catch (e) {
    if (e !== 'cancel') {
      // handled by interceptor
    }
  }
}
async function handleUserConfirm() {
  userConfirmLoading.value = true
  try {
    await updateUser(editingUserId.value, { ...userForm })
    ElMessage.success('用户信息已更新')
    userDialogVisible.value = false
    fetchUsers()
  } catch (e) {
    // handled by interceptor
  } finally {
    userConfirmLoading.value = false
  }
}

// ==================== Roles ====================
const roleLoading = ref(false)
const roleData = reactive([])

async function fetchRoles() {
  roleLoading.value = true
  try {
    const res = await getRoles()
    roleData.length = 0
    const list = res.data || []
    list.forEach(item => roleData.push(item))
  } catch (e) {
    // handled by interceptor
  } finally {
    roleLoading.value = false
  }
}

function openAddRole() { ElMessage.info('新增角色（模拟）') }
function handleEditRole(row) { ElMessage.info(`编辑角色：${row.name}（模拟）`) }
function handleDeleteRole(row) {
  ElMessageBox.confirm(`确定删除角色「${row.name}」？`, '确认', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
  }).then(() => {
    ElMessage.success('删除成功（模拟）')
  }).catch(() => {})
}

// ==================== Permission Config ====================
const permDialogVisible = ref(false)
const permConfirmLoading = ref(false)
const permTreeRef = ref(null)
const permTreeData = ref([])
const configRoleId = ref(null)

async function handleConfigPerm(row) {
  configRoleId.value = row.id
  permDialogVisible.value = true
  try {
    const res = await getPermissionTree()
    permTreeData.value = res.data || []
  } catch (e) {
    permTreeData.value = []
  }
}
async function handlePermConfirm() {
  permConfirmLoading.value = true
  try {
    const checkedIds = permTreeRef.value.getCheckedKeys()
    await assignPermissions(configRoleId.value, checkedIds)
    ElMessage.success('权限配置已保存')
    permDialogVisible.value = false
  } catch (e) {
    // handled by interceptor
  } finally {
    permConfirmLoading.value = false
  }
}

// ==================== Menus ====================
const menuLoading = ref(false)
const menuData = reactive([])

async function fetchMenus() {
  menuLoading.value = true
  try {
    const res = await getPermissionTree()
    menuData.length = 0
    const list = res.data || []
    list.forEach(item => menuData.push(item))
  } catch (e) {
    // handled by interceptor
  } finally {
    menuLoading.value = false
  }
}

function openAddMenu() { ElMessage.info('新增菜单（模拟）') }
function handleEditMenu(row) { ElMessage.info(`编辑菜单：${row.name}（模拟）`) }
function handleDeleteMenu(row) {
  ElMessageBox.confirm(`确定删除菜单「${row.name}」？`, '确认', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
  }).then(() => {
    ElMessage.success('删除成功（模拟）')
  }).catch(() => {})
}

onMounted(() => {
  fetchUsers()
  fetchRoles()
  fetchMenus()
})
</script>

<style lang="scss" scoped>
.page { height: 100%; display: flex; flex-direction: column;
  .page-header { display: flex; justify-content: space-between; align-items: center; flex-shrink: 0; margin-bottom: 16px;
    &__actions { display: flex; gap: 10px; } }
  .page-title { font-size: 20px; font-weight: 600; color: #303133; margin: 0; } }
.perm-tabs { flex: 1; display: flex; flex-direction: column; :deep(.el-tabs__content) { flex: 1; overflow: auto; } }
.search-bar { display: flex; gap: 10px; margin-bottom: 16px; flex-shrink: 0; align-items: center; }
.tab-header-actions { margin-bottom: 16px; }
.perm-tree { max-height: 400px; overflow-y: auto; }
</style>

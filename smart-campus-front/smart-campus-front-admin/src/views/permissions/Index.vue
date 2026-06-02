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

function fetchUsers() {
  userLoading.value = true
  setTimeout(() => {
    const mockList = []
    for (let i = 0; i < 15; i++) {
      const idx = (userData.pageNo - 1) * 15 + i
      const roles = ['超级管理员', '管理员', '教师', '学生']
      mockList.push({
        id: idx + 1,
        username: ['admin', 'sysop', 'zhangsan', 'lisi', 'wangwu', 'zhaoliu', 'chenqi'][idx % 7],
        realName: ['系统管理员', '系统运维', '张三', '李四', '王五', '赵六', '陈七'][idx % 7],
        role: roles[idx % 4],
        email: `user${idx + 1}@campus.edu`,
        status: idx % 6 === 0 ? 0 : 1,
        createTime: '2026-01-0' + ((idx % 9) + 1) + ' 00:00:00',
      })
    }
    userData.list = mockList
    userData.totalCount = 45
    userData.pageTotal = 3
    userLoading.value = false
  }, 500)
}

function onUserPageChange({ pageNo, pageSize }) { userData.pageNo = pageNo; userData.pageSize = pageSize; fetchUsers() }
function onSelectionChange(selection) { console.log('选中:', selection) }

function handleEditUser(row) {
  editingUserId.value = row.id
  Object.assign(userForm, row)
  userDialogVisible.value = true
}
function handleToggleUser(row) {
  const action = row.status === 1 ? '禁用' : '启用'
  ElMessageBox.confirm(`确定${action}用户「${row.username}」？`, '确认', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
  }).then(() => {
    row.status = row.status === 1 ? 0 : 1
    ElMessage.success(`${action}成功`)
  }).catch(() => {})
}
function handleDeleteUser(row) {
  ElMessageBox.confirm(`确定删除用户「${row.username}」？`, '删除确认', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
  }).then(() => {
    const idx = userData.list.findIndex(item => item.id === row.id)
    if (idx !== -1) userData.list.splice(idx, 1)
    userData.totalCount -= 1
    ElMessage.success('删除成功')
  }).catch(() => {})
}
function handleUserConfirm() {
  userConfirmLoading.value = true
  setTimeout(() => {
    userConfirmLoading.value = false; userDialogVisible.value = false
    ElMessage.success('用户信息已更新')
    fetchUsers()
  }, 800)
}

// ==================== Roles ====================
const roleLoading = ref(false)
const roleData = reactive([
  { id: 1, name: '超级管理员', code: 'ROLE_SUPER_ADMIN', description: '拥有系统所有权限', userCount: 2, status: 1 },
  { id: 2, name: '管理员', code: 'ROLE_ADMIN', description: '拥有大部分管理权限', userCount: 5, status: 1 },
  { id: 3, name: '教师', code: 'ROLE_TEACHER', description: '教学相关功能权限', userCount: 45, status: 1 },
  { id: 4, name: '学生', code: 'ROLE_STUDENT', description: '基础学习功能权限', userCount: 1200, status: 1 },
])

function openAddRole() { ElMessage.info('新增角色（模拟）') }
function handleEditRole(row) { ElMessage.info(`编辑角色：${row.name}（模拟）`) }
function handleDeleteRole(row) {
  ElMessageBox.confirm(`确定删除角色「${row.name}」？`, '确认', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
  }).then(() => {
    const idx = roleData.findIndex(r => r.id === row.id)
    if (idx !== -1) roleData.splice(idx, 1)
    ElMessage.success('删除成功')
  }).catch(() => {})
}

// ==================== Permission Config ====================
const permDialogVisible = ref(false)
const permConfirmLoading = ref(false)
const permTreeRef = ref(null)
const permTreeData = reactive([
  { id: 1, name: '系统管理', children: [
    { id: 11, name: '用户管理', children: [
      { id: 111, name: '查看用户' }, { id: 112, name: '新增用户' }, { id: 113, name: '编辑用户' }, { id: 114, name: '删除用户' },
    ]},
    { id: 12, name: '角色管理', children: [
      { id: 121, name: '查看角色' }, { id: 122, name: '新增角色' }, { id: 123, name: '编辑角色' },
    ]},
    { id: 13, name: '菜单管理', children: [{ id: 131, name: '查看菜单' }, { id: 132, name: '编辑菜单' }] },
  ]},
  { id: 2, name: '教学管理', children: [
    { id: 21, name: '课程管理', children: [{ id: 211, name: '管理课程' }, { id: 212, name: '查看课程' }] },
    { id: 22, name: '考试管理', children: [{ id: 221, name: '管理考试' }, { id: 222, name: '查看成绩' }] },
  ]},
  { id: 3, name: '基础数据', children: [
    { id: 31, name: '院系管理' }, { id: 32, name: '专业管理' }, { id: 33, name: '班级管理' }, { id: 34, name: '学生管理' }, { id: 35, name: '教师管理' },
  ]},
])

function handleConfigPerm(row) {
  ElMessage.info(`为角色「${row.name}」配置权限（模拟）`)
  permDialogVisible.value = true
}
function handlePermConfirm() {
  permConfirmLoading.value = true
  setTimeout(() => {
    permConfirmLoading.value = false; permDialogVisible.value = false
    ElMessage.success('权限配置已保存')
  }, 800)
}

// ==================== Menus ====================
const menuLoading = ref(false)
const menuData = reactive([
  { id: 1, name: '首页', icon: 'HomeFilled', path: '/dashboard', perms: '', sort: 1, status: 1, children: [] },
  { id: 2, name: '基础数据', icon: 'Notebook', path: '', perms: '', sort: 2, status: 1, children: [
    { id: 21, name: '院系管理', icon: '', path: '/departments', perms: 'sys:dept:list', sort: 1, status: 1 },
    { id: 22, name: '专业管理', icon: '', path: '/majors', perms: 'sys:major:list', sort: 2, status: 1 },
    { id: 23, name: '班级管理', icon: '', path: '/classes', perms: 'sys:class:list', sort: 3, status: 1 },
    { id: 24, name: '学生管理', icon: '', path: '/students', perms: 'sys:student:list', sort: 4, status: 1 },
    { id: 25, name: '教师管理', icon: '', path: '/teachers', perms: 'sys:teacher:list', sort: 5, status: 1 },
  ]},
  { id: 3, name: '资源中心', icon: 'FolderOpened', path: '', perms: '', sort: 3, status: 1, children: [
    { id: 31, name: '资源管理', icon: '', path: '/resources', perms: 'res:list', sort: 1, status: 1 },
  ]},
  { id: 4, name: '教学业务', icon: 'Reading', path: '', perms: '', sort: 4, status: 1, children: [
    { id: 41, name: '课程管理', icon: '', path: '/courses', perms: 'edu:course:list', sort: 1, status: 1 },
    { id: 42, name: '习题管理', icon: '', path: '/exercises', perms: 'edu:exercise:list', sort: 2, status: 1 },
    { id: 43, name: '试卷管理', icon: '', path: '/papers', perms: 'edu:paper:list', sort: 3, status: 1 },
    { id: 44, name: '考试管理', icon: '', path: '/exams', perms: 'edu:exam:list', sort: 4, status: 1 },
  ]},
  { id: 5, name: '系统管理', icon: 'Setting', path: '', perms: '', sort: 5, status: 1, children: [
    { id: 51, name: '公告管理', icon: '', path: '/announcements', perms: 'sys:notice:list', sort: 1, status: 1 },
    { id: 52, name: '权限管理', icon: '', path: '/permissions', perms: 'sys:perm:list', sort: 2, status: 1 },
  ]},
])

function openAddMenu() { ElMessage.info('新增菜单（模拟）') }
function handleEditMenu(row) { ElMessage.info(`编辑菜单：${row.name}（模拟）`) }
function handleDeleteMenu(row) {
  ElMessageBox.confirm(`确定删除菜单「${row.name}」？`, '确认', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
  }).then(() => {
    ElMessage.success('删除成功（模拟）')
  }).catch(() => {})
}

onMounted(fetchUsers)
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

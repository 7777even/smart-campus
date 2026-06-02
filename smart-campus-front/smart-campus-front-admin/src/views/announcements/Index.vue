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

function fetchData() {
  loading.value = true
  setTimeout(() => {
    const mockList = []
    const titles = [
      '关于2026年端午节放假安排的通知',
      '关于开展期中教学检查工作的通知',
      '智慧校园系统升级维护公告',
      '关于2026届毕业生毕业典礼的通知',
      '图书馆暑假开放时间调整通知',
      '关于申报2026年度教学改革项目的通知',
      '校园网络优化升级通知',
    ]
    const levels = ['普通', '重要', '紧急', '重要', '普通', '普通', '紧急']
    const contents = [
      '根据学校安排，现将2026年端午节放假有关事项通知如下：\n\n一、放假时间：6月25日至6月27日，共3天。\n二、请各部门做好节前安全检查工作。\n三、假期期间如遇紧急情况，请及时与学校值班人员联系。',
      '各学院（部）：\n\n为全面了解本学期教学运行情况，学校决定在第12-13周开展期中教学检查工作。\n\n一、检查内容：课堂教学质量、教学进度执行情况、学生学习效果等。\n二、检查方式：学院自查与学校抽查相结合。\n三、请各学院于5月30日前提交自查报告。',
      '尊敬的师生用户：\n\n智慧校园系统将于2026年6月5日22:00至6月6日06:00进行系统升级维护，届时部分功能将暂停使用。\n\n影响范围：\n1. 教务管理系统\n2. 学生选课系统\n3. 校园卡充值\n\n给您带来的不便，敬请谅解。',
    ]
    for (let i = 0; i < 15; i++) {
      const idx = (tableData.pageNo - 1) * 15 + i
      mockList.push({
        id: idx + 1,
        title: titles[idx % titles.length],
        level: levels[idx % levels.length],
        publisher: ['管理员', '教务处', '信息中心', '学生处', '图书馆'][idx % 5],
        status: idx % 4 === 0 ? '草稿' : '已发布',
        content: contents[idx % contents.length] || '公告内容',
        createTime: '2026-05-' + String(10 + (idx % 20)).padStart(2, '0') + ' 10:00:00',
      })
    }
    tableData.list = mockList
    tableData.totalCount = 30
    tableData.pageTotal = 2
    loading.value = false
  }, 500)
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
function handleTogglePublish(row) {
  const action = row.status === '已发布' ? '下架' : '发布'
  ElMessageBox.confirm(`确定${action}公告「${row.title}」？`, '确认', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'info',
  }).then(() => {
    row.status = row.status === '已发布' ? '草稿' : '已发布'
    ElMessage.success(`${action}成功`)
  }).catch(() => {})
}
function handleDelete(row) {
  ElMessageBox.confirm(`确定删除公告「${row.title}」？`, '删除确认', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
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
    confirmLoading.value = false; dialogVisible.value = false
    ElMessage.success(isEditing.value ? '编辑成功' : '发布成功')
    fetchData()
  }, 800)
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

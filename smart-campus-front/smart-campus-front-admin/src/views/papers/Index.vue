<template>
  <div class="page">
    <div class="page-header">
      <h2 class="page-title">试卷管理</h2>
      <div class="page-header__actions">
        <el-button type="primary" @click="openAddDialog">新建试卷</el-button>
      </div>
    </div>
    <div class="search-bar">
      <el-input v-model="searchForm.keyword" placeholder="试卷名称" style="width:200px" size="default" clearable @clear="fetchData" />
      <el-select v-model="searchForm.courseId" placeholder="所属课程" style="width:150px" size="default" clearable @change="fetchData">
        <el-option v-for="c in courseOptions" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-select v-model="searchForm.status" placeholder="状态" style="width:100px" size="default" clearable @change="fetchData">
        <el-option label="草稿" value="草稿" />
        <el-option label="已发布" value="已发布" />
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
      <template #courseId="{ row }">{{ getCourseName(row.courseId) }}</template>
      <template #status="{ row }">
        <el-tag :type="row.status === '已发布' ? 'success' : 'info'" size="small">{{ row.status }}</el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link size="small" @click="handleView(row)">预览</el-button>
        <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
        <el-button type="primary" link size="small" @click="handlePublish(row)" v-if="row.status === '草稿'">发布</el-button>
        <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
      </template>
    </BaseDataTable>

    <BaseDialog
      v-model:visible="dialogVisible"
      :title="isEditing ? '编辑试卷' : '新建试卷'"
      width="650px"
      :confirm-loading="confirmLoading"
      confirm-text="保存"
      @confirm="handleConfirm"
      @cancel="dialogVisible = false"
    >
      <el-form :model="form" label-width="110px" label-position="right">
        <el-form-item label="试卷名称">
          <el-input v-model="form.name" placeholder="请输入试卷名称" />
        </el-form-item>
        <el-form-item label="所属课程">
          <el-select v-model="form.courseId" placeholder="请选择" style="width:100%">
            <el-option v-for="c in courseOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="总分">
          <el-input-number v-model="form.totalScore" :min="10" :max="200" :step="10" />
        </el-form-item>
        <el-form-item label="及格分">
          <el-input-number v-model="form.passScore" :min="0" :max="200" />
        </el-form-item>
        <el-form-item label="考试时长(分钟)">
          <el-input-number v-model="form.duration" :min="10" :max="180" :step="10" />
        </el-form-item>
        <el-form-item label="单选题数/分值">
          <span class="inline-group">
            <el-input-number v-model="form.singleCount" :min="0" :max="50" size="small" style="width:120px" /> 题 ×
            <el-input-number v-model="form.singleScore" :min="1" :max="10" size="small" style="width:120px" /> 分
          </span>
        </el-form-item>
        <el-form-item label="多选题数/分值">
          <span class="inline-group">
            <el-input-number v-model="form.multiCount" :min="0" :max="50" size="small" style="width:120px" /> 题 ×
            <el-input-number v-model="form.multiScore" :min="1" :max="10" size="small" style="width:120px" /> 分
          </span>
        </el-form-item>
        <el-form-item label="判断题数/分值">
          <span class="inline-group">
            <el-input-number v-model="form.judgeCount" :min="0" :max="50" size="small" style="width:120px" /> 题 ×
            <el-input-number v-model="form.judgeScore" :min="1" :max="10" size="small" style="width:120px" /> 分
          </span>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="考试说明" />
        </el-form-item>
      </el-form>
    </BaseDialog>

    <BaseDrawer
      v-model:visible="drawerVisible"
      title="试卷预览"
      size="600px"
      :show-confirm="false"
      cancel-text="关闭"
      @cancel="drawerVisible = false"
    >
      <div class="paper-preview">
        <div class="pp-header">
          <h3>{{ detailData.name }}</h3>
          <div class="pp-meta">
            <span>课程：{{ getCourseName(detailData.courseId) }}</span>
            <span>总分：{{ detailData.totalScore }}分</span>
            <span>时长：{{ detailData.duration }}分钟</span>
          </div>
        </div>
        <div class="pp-section">
          <h4>一、单选题（共{{ detailData.singleCount }}题，每题{{ detailData.singleScore }}分）</h4>
          <p class="pp-placeholder">（题目内容略）</p>
        </div>
        <div class="pp-section">
          <h4>二、多选题（共{{ detailData.multiCount }}题，每题{{ detailData.multiScore }}分）</h4>
          <p class="pp-placeholder">（题目内容略）</p>
        </div>
        <div class="pp-section">
          <h4>三、判断题（共{{ detailData.judgeCount }}题，每题{{ detailData.judgeScore }}分）</h4>
          <p class="pp-placeholder">（题目内容略）</p>
        </div>
        <div class="pp-footer">
          <p>及格线：{{ detailData.passScore }}分</p>
          <p v-if="detailData.description">说明：{{ detailData.description }}</p>
        </div>
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
import { getPapers, createPaper, updatePaper, deletePaper, publishPaper } from '@/api/paper'
import { getCourses } from '@/api/common'

const tableRef = ref(null)
const loading = ref(false)
const dialogVisible = ref(false)
const drawerVisible = ref(false)
const confirmLoading = ref(false)
const isEditing = ref(false)
const editingId = ref(null)

const searchForm = reactive({ keyword: '', courseId: null, status: '' })
const form = reactive({
  name: '', courseId: null, totalScore: 100, passScore: 60, duration: 90,
  singleCount: 10, singleScore: 4, multiCount: 5, multiScore: 4,
  judgeCount: 10, judgeScore: 2, description: '',
})
const detailData = reactive({})

const courseOptions = ref([])
function getCourseName(id) { return courseOptions.value.find(c => c.id === id)?.name || '-' }

const columns = [
  { label: '试卷名称', prop: 'name', minWidth: 220 },
  { label: '所属课程', prop: 'courseId', width: 130 },
  { label: '总分', prop: 'totalScore', width: 70 },
  { label: '及格分', prop: 'passScore', width: 70 },
  { label: '时长(分)', prop: 'duration', width: 90 },
  { label: '总题数', prop: 'questionCount', width: 80 },
  { label: '状态', prop: 'status', width: 90 },
  { label: '创建时间', prop: 'createTime', width: 170 },
  { label: '操作', prop: 'action', width: 250, fixed: 'right' },
]

const tableData = reactive({ totalCount: 0, pageSize: 15, pageNo: 1, pageTotal: 0, list: [] })

async function fetchData() {
  loading.value = true
  try {
    const params = { pageNo: tableData.pageNo, pageSize: tableData.pageSize }
    if (searchForm.keyword) params.keyword = searchForm.keyword
    if (searchForm.courseId) params.courseId = searchForm.courseId
    if (searchForm.status) params.status = searchForm.status
    const res = await getPapers(params)
    Object.assign(tableData, res.data)
  } catch (e) {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

function resetSearch() { searchForm.keyword = ''; searchForm.courseId = null; searchForm.status = ''; fetchData() }
function onPageChange({ pageNo, pageSize }) { tableData.pageNo = pageNo; tableData.pageSize = pageSize; fetchData() }
function onSelectionChange(selection) { console.log('选中:', selection) }

function openAddDialog() {
  isEditing.value = false; editingId.value = null
  form.name = ''; form.courseId = null; form.totalScore = 100; form.passScore = 60; form.duration = 90
  form.singleCount = 10; form.singleScore = 4; form.multiCount = 5; form.multiScore = 4
  form.judgeCount = 10; form.judgeScore = 2; form.description = ''
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
async function handlePublish(row) {
  try {
    await ElMessageBox.confirm(`确定发布试卷「${row.name}」？`, '发布确认', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'info',
    })
    await publishPaper(row.id)
    ElMessage.success('发布成功')
    fetchData()
  } catch (e) {
    if (e !== 'cancel') {
      // handled by interceptor
    }
  }
}
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除试卷「${row.name}」？`, '删除确认', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
    })
    await deletePaper(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) {
    if (e !== 'cancel') {
      // handled by interceptor
    }
  }
}
async function handleConfirm() {
  confirmLoading.value = true
  try {
    if (isEditing.value) {
      await updatePaper(editingId.value, { ...form })
      ElMessage.success('编辑成功')
    } else {
      await createPaper({ ...form })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    // handled by interceptor
  } finally {
    confirmLoading.value = false
  }
}

async function loadCourseOptions() {
  try {
    const res = await getCourses()
    courseOptions.value = res.data || []
  } catch (e) {
    courseOptions.value = []
  }
}
onMounted(() => { loadCourseOptions(); fetchData() })
</script>

<style lang="scss" scoped>
.page { height: 100%; display: flex; flex-direction: column;
  .page-header { display: flex; justify-content: space-between; align-items: center; flex-shrink: 0; margin-bottom: 16px;
    &__actions { display: flex; gap: 10px; } }
  .page-title { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
  .search-bar { display: flex; gap: 10px; margin-bottom: 16px; flex-shrink: 0; align-items: center; }
}
.inline-group { display: flex; align-items: center; gap: 6px; flex-wrap: wrap; }
.paper-preview { padding: 8px 0;
  .pp-header { margin-bottom: 20px;
    h3 { font-size: 18px; color: #303133; margin: 0 0 12px; text-align: center; }
    .pp-meta { display: flex; justify-content: center; gap: 24px; font-size: 13px; color: #909399; } }
  .pp-section { margin-bottom: 16px;
    h4 { font-size: 15px; color: #409EFF; margin: 0 0 8px; }
    .pp-placeholder { color: #c0c4cc; font-size: 13px; padding: 12px; background: #f5f7fa; border-radius: 6px; } }
  .pp-footer { margin-top: 20px; padding-top: 16px; border-top: 1px solid #f0f0f0; font-size: 13px; color: #909399; } }
</style>

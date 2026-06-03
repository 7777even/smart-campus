<template>
  <div class="page">
    <div class="page-header">
      <h2 class="page-title">习题管理</h2>
      <div class="page-header__actions">
        <el-button type="primary" @click="openAddDialog">新增习题</el-button>
        <el-button @click="handleBatchImport">批量导入</el-button>
      </div>
    </div>
    <div class="search-bar">
      <el-input v-model="searchForm.keyword" placeholder="题目内容" style="width:200px" size="default" clearable @clear="fetchData" />
      <el-select v-model="searchForm.type" placeholder="题型" style="width:120px" size="default" clearable @change="fetchData">
        <el-option label="单选题" value="单选题" />
        <el-option label="多选题" value="多选题" />
        <el-option label="判断题" value="判断题" />
        <el-option label="填空题" value="填空题" />
        <el-option label="简答题" value="简答题" />
      </el-select>
      <el-select v-model="searchForm.difficulty" placeholder="难度" style="width:100px" size="default" clearable @change="fetchData">
        <el-option label="简单" value="简单" />
        <el-option label="中等" value="中等" />
        <el-option label="困难" value="困难" />
      </el-select>
      <el-select v-model="searchForm.courseId" placeholder="所属课程" style="width:150px" size="default" clearable @change="fetchData">
        <el-option v-for="c in courseOptions" :key="c.id" :label="c.name" :value="c.id" />
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
      <template #question="{ row }">
        <span class="question-text">{{ row.question }}</span>
      </template>
      <template #type="{ row }">
        <el-tag :type="typeTag(row.type)" size="small">{{ row.type }}</el-tag>
      </template>
      <template #difficulty="{ row }">
        <el-tag :type="diffTag(row.difficulty)" size="small">{{ row.difficulty }}</el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link size="small" @click="handleView(row)">预览</el-button>
        <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
      </template>
    </BaseDataTable>

    <BaseDialog
      v-model:visible="dialogVisible"
      :title="isEditing ? '编辑习题' : '新增习题'"
      width="700px"
      :confirm-loading="confirmLoading"
      confirm-text="保存"
      @confirm="handleConfirm"
      @cancel="dialogVisible = false"
    >
      <el-form :model="form" label-width="100px" label-position="right">
        <el-form-item label="所属课程">
          <el-select v-model="form.courseId" placeholder="请选择" style="width:100%">
            <el-option v-for="c in courseOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="题型">
          <el-select v-model="form.type" style="width:100%">
            <el-option label="单选题" value="单选题" />
            <el-option label="多选题" value="多选题" />
            <el-option label="判断题" value="判断题" />
            <el-option label="填空题" value="填空题" />
            <el-option label="简答题" value="简答题" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-radio-group v-model="form.difficulty">
            <el-radio label="简单">简单</el-radio>
            <el-radio label="中等">中等</el-radio>
            <el-radio label="困难">困难</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="题目">
          <el-input v-model="form.question" type="textarea" :rows="3" placeholder="请输入题目内容" />
        </el-form-item>
        <el-form-item label="选项A">
          <el-input v-model="form.optionA" placeholder="A选项内容" />
        </el-form-item>
        <el-form-item label="选项B">
          <el-input v-model="form.optionB" placeholder="B选项内容" />
        </el-form-item>
        <el-form-item label="选项C">
          <el-input v-model="form.optionC" placeholder="C选项内容" />
        </el-form-item>
        <el-form-item label="选项D">
          <el-input v-model="form.optionD" placeholder="D选项内容" />
        </el-form-item>
        <el-form-item label="正确答案">
          <el-input v-model="form.answer" placeholder="如：A / ABC / 对" style="width:200px" />
        </el-form-item>
        <el-form-item label="解析">
          <el-input v-model="form.analysis" type="textarea" :rows="2" placeholder="答案解析（可选）" />
        </el-form-item>
      </el-form>
    </BaseDialog>

    <BaseDrawer
      v-model:visible="drawerVisible"
      title="习题预览"
      size="600px"
      :show-confirm="false"
      cancel-text="关闭"
      @cancel="drawerVisible = false"
    >
      <div class="question-preview">
        <div class="qp-meta">
          <el-tag size="small">{{ detailData.type }}</el-tag>
          <el-tag :type="diffTag(detailData.difficulty)" size="small">{{ detailData.difficulty }}</el-tag>
          <span class="qp-course">{{ getCourseName(detailData.courseId) }}</span>
        </div>
        <div class="qp-question">{{ detailData.question }}</div>
        <div v-if="detailData.optionA" class="qp-option">A. {{ detailData.optionA }}</div>
        <div v-if="detailData.optionB" class="qp-option">B. {{ detailData.optionB }}</div>
        <div v-if="detailData.optionC" class="qp-option">C. {{ detailData.optionC }}</div>
        <div v-if="detailData.optionD" class="qp-option">D. {{ detailData.optionD }}</div>
        <div class="qp-answer">
          <span class="qp-answer-label">正确答案：</span>
          <span class="qp-answer-value">{{ detailData.answer }}</span>
        </div>
        <div v-if="detailData.analysis" class="qp-analysis">
          <span class="qp-analysis-label">解析：</span>
          <span class="qp-analysis-value">{{ detailData.analysis }}</span>
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
import { getExercises, createExercise, updateExercise, deleteExercise } from '@/api/exercise'
import { getCourses } from '@/api/common'

const tableRef = ref(null)
const loading = ref(false)
const dialogVisible = ref(false)
const drawerVisible = ref(false)
const confirmLoading = ref(false)
const isEditing = ref(false)
const editingId = ref(null)

const searchForm = reactive({ keyword: '', type: '', difficulty: '', courseId: null })
const form = reactive({
  courseId: null, type: '单选题', difficulty: '中等',
  question: '', optionA: '', optionB: '', optionC: '', optionD: '', answer: '', analysis: '',
})
const detailData = reactive({})

const courseOptions = ref([])
function getCourseName(id) { return courseOptions.value.find(c => c.id === id)?.name || '-' }

function typeTag(t) {
  const map = { '单选题': 'primary', '多选题': 'success', '判断题': 'warning', '填空题': 'info', '简答题': 'danger' }
  return map[t] || 'info'
}
function diffTag(d) { return d === '简单' ? 'success' : d === '中等' ? 'warning' : 'danger' }

const columns = [
  { label: '题目', prop: 'question', minWidth: 300 },
  { label: '题型', prop: 'type', width: 90 },
  { label: '难度', prop: 'difficulty', width: 80 },
  { label: '正确答案', prop: 'answer', width: 100 },
  { label: '创建时间', prop: 'createTime', width: 170 },
  { label: '操作', prop: 'action', width: 180, fixed: 'right' },
]

const tableData = reactive({ totalCount: 0, pageSize: 15, pageNo: 1, pageTotal: 0, list: [] })

async function fetchData() {
  loading.value = true
  try {
    const params = { pageNo: tableData.pageNo, pageSize: tableData.pageSize }
    if (searchForm.keyword) params.keyword = searchForm.keyword
    if (searchForm.type) params.type = searchForm.type
    if (searchForm.difficulty) params.difficulty = searchForm.difficulty
    if (searchForm.courseId) params.courseId = searchForm.courseId
    const res = await getExercises(params)
    Object.assign(tableData, res.data)
  } catch (e) {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

function resetSearch() { searchForm.keyword = ''; searchForm.type = ''; searchForm.difficulty = ''; searchForm.courseId = null; fetchData() }
function onPageChange({ pageNo, pageSize }) { tableData.pageNo = pageNo; tableData.pageSize = pageSize; fetchData() }
function onSelectionChange(selection) { console.log('选中:', selection) }

function openAddDialog() {
  isEditing.value = false; editingId.value = null
  form.courseId = null; form.type = '单选题'; form.difficulty = '中等'
  form.question = ''; form.optionA = ''; form.optionB = ''; form.optionC = ''; form.optionD = ''
  form.answer = ''; form.analysis = ''
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
    await ElMessageBox.confirm(`确定删除该习题？`, '删除确认', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
    })
    await deleteExercise(row.id)
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
      await updateExercise(editingId.value, { ...form })
      ElMessage.success('编辑成功')
    } else {
      await createExercise({ ...form })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    // handled by interceptor
  } finally {
    confirmLoading.value = false
  }
}
function handleBatchImport() { ElMessage.info('批量导入（模拟）') }

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
.question-text { overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.question-preview { padding: 8px 0;
  .qp-meta { display: flex; gap: 8px; align-items: center; margin-bottom: 16px; }
  .qp-course { font-size: 13px; color: #909399; }
  .qp-question { font-size: 16px; font-weight: 600; color: #303133; margin-bottom: 16px; line-height: 1.6; }
  .qp-option { padding: 8px 12px; margin-bottom: 8px; background: #f5f7fa; border-radius: 6px; font-size: 14px; color: #606266; }
  .qp-answer { margin-top: 16px; padding: 12px; background: #f0f9eb; border-radius: 6px;
    &-label { font-weight: 600; color: #67C23A; }
    &-value { color: #303133; font-weight: 500; } }
  .qp-analysis { margin-top: 12px; padding: 12px; background: #ecf5ff; border-radius: 6px;
    &-label { font-weight: 600; color: #409EFF; }
    &-value { color: #606266; } } }
</style>

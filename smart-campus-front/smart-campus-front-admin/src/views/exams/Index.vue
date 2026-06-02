<template>
  <div class="page">
    <div class="page-header">
      <h2 class="page-title">考试管理</h2>
      <div class="page-header__actions">
        <el-button type="primary" @click="openAddDialog">安排考试</el-button>
      </div>
    </div>
    <div class="search-bar">
      <el-input v-model="searchForm.keyword" placeholder="考试名称" style="width:200px" size="default" clearable @clear="fetchData" />
      <el-select v-model="searchForm.status" placeholder="状态" style="width:120px" size="default" clearable @change="fetchData">
        <el-option label="待开始" value="待开始" />
        <el-option label="进行中" value="进行中" />
        <el-option label="已结束" value="已结束" />
      </el-select>
      <el-date-picker v-model="searchForm.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" style="width:250px" size="default" @change="fetchData" />
      <el-button type="primary" @click="fetchData">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>
    <!-- 状态统计 -->
    <div class="stat-cards">
      <div class="stat-card"><span class="stat-label">全部考试</span><span class="stat-value">{{ tableData.totalCount }}</span></div>
      <div class="stat-card"><span class="stat-label">待开始</span><span class="stat-value" style="color:#409EFF">{{ pendingCount }}</span></div>
      <div class="stat-card"><span class="stat-label">进行中</span><span class="stat-value" style="color:#E6A23C">{{ ongoingCount }}</span></div>
      <div class="stat-card"><span class="stat-label">已结束</span><span class="stat-value" style="color:#909399">{{ endedCount }}</span></div>
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
      <template #paperId="{ row }">{{ getPaperName(row.paperId) }}</template>
      <template #status="{ row }">
        <el-tag :type="statusType(row.status)" size="small">{{ row.status }}</el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link size="small" @click="handleView(row)">详情</el-button>
        <el-button type="primary" link size="small" @click="handleEdit(row)" v-if="row.status === '待开始'">编辑</el-button>
        <el-button type="primary" link size="small" @click="handleGrade(row)" v-if="row.status !== '待开始'">成绩</el-button>
        <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
      </template>
    </BaseDataTable>

    <BaseDialog
      v-model:visible="dialogVisible"
      :title="isEditing ? '编辑考试' : '安排考试'"
      width="650px"
      :confirm-loading="confirmLoading"
      confirm-text="保存"
      @confirm="handleConfirm"
      @cancel="dialogVisible = false"
    >
      <el-form :model="form" label-width="120px" label-position="right">
        <el-form-item label="考试名称">
          <el-input v-model="form.name" placeholder="请输入考试名称" />
        </el-form-item>
        <el-form-item label="所属课程">
          <el-select v-model="form.courseId" placeholder="请选择" style="width:100%">
            <el-option v-for="c in courseOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联试卷">
          <el-select v-model="form.paperId" placeholder="请选择" style="width:100%">
            <el-option v-for="p in paperOptions" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="考试日期">
          <el-date-picker v-model="form.examDate" type="date" placeholder="选择日期" style="width:100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-time-picker v-model="form.startTime" placeholder="开始时间" style="width:100%" value-format="HH:mm" />
        </el-form-item>
        <el-form-item label="考试时长">
          <el-input-number v-model="form.duration" :min="10" :max="180" :step="10" /> 分钟
        </el-form-item>
        <el-form-item label="考试地点">
          <el-input v-model="form.location" placeholder="如：教学楼301" />
        </el-form-item>
        <el-form-item label="监考教师">
          <el-select v-model="form.invigilator" placeholder="请选择" style="width:100%">
            <el-option label="张教授" value="张教授" />
            <el-option label="李教授" value="李教授" />
            <el-option label="王老师" value="王老师" />
            <el-option label="赵老师" value="赵老师" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="备注信息" />
        </el-form-item>
      </el-form>
    </BaseDialog>

    <BaseDrawer
      v-model:visible="drawerVisible"
      title="考试详情"
      size="550px"
      :show-confirm="false"
      cancel-text="关闭"
      @cancel="drawerVisible = false"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="考试名称">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="所属课程">{{ getCourseName(detailData.courseId) }}</el-descriptions-item>
        <el-descriptions-item label="关联试卷">{{ getPaperName(detailData.paperId) }}</el-descriptions-item>
        <el-descriptions-item label="考试日期">{{ detailData.examDate }}</el-descriptions-item>
        <el-descriptions-item label="考试时间">{{ detailData.startTime }}</el-descriptions-item>
        <el-descriptions-item label="考试时长">{{ detailData.duration }} 分钟</el-descriptions-item>
        <el-descriptions-item label="考试地点">{{ detailData.location }}</el-descriptions-item>
        <el-descriptions-item label="监考教师">{{ detailData.invigilator }}</el-descriptions-item>
        <el-descriptions-item label="应考人数">{{ detailData.totalStudents }}</el-descriptions-item>
        <el-descriptions-item label="实考人数">{{ detailData.attendedStudents }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(detailData.status)" size="small">{{ detailData.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </BaseDrawer>

    <!-- 成绩抽屉 -->
    <BaseDrawer
      v-model:visible="gradeDrawerVisible"
      title="考试成绩"
      size="600px"
      :show-confirm="false"
      cancel-text="关闭"
      @cancel="gradeDrawerVisible = false"
    >
      <el-table :data="gradeData" border stripe style="width:100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="studentNo" label="学号" width="130" />
        <el-table-column prop="score" label="成绩" width="80" />
        <el-table-column label="结果" width="80">
          <template #default="{ row }">
            <el-tag :type="row.score >= 60 ? 'success' : 'danger'" size="small">
              {{ row.score >= 60 ? '及格' : '不及格' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div class="grade-summary">
        <span>参考人数：{{ gradeData.length }}</span>
        <span>平均分：{{ avgScore }}</span>
        <span>最高分：{{ maxScore }}</span>
        <span>及格率：{{ passRate }}%</span>
      </div>
    </BaseDrawer>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import BaseDataTable from '@/components/BaseDataTable.vue'
import BaseDialog from '@/components/BaseDialog.vue'
import BaseDrawer from '@/components/BaseDrawer.vue'

const tableRef = ref(null)
const loading = ref(false)
const dialogVisible = ref(false)
const drawerVisible = ref(false)
const gradeDrawerVisible = ref(false)
const confirmLoading = ref(false)
const isEditing = ref(false)
const editingId = ref(null)

const searchForm = reactive({ keyword: '', status: '', dateRange: null })
const form = reactive({
  name: '', courseId: null, paperId: null, examDate: '', startTime: '',
  duration: 90, location: '', invigilator: '', remark: '',
})
const detailData = reactive({})
const gradeData = reactive([])

const courseOptions = [
  { id: 1, name: '数据结构' }, { id: 2, name: '操作系统' },
  { id: 3, name: '计算机网络' }, { id: 4, name: '高等数学' },
]
const paperOptions = [
  { id: 1, name: '数据结构期中考试' }, { id: 2, name: '操作系统期末考试' },
  { id: 3, name: '计算机网络单元测试' }, { id: 4, name: '高等数学模拟考试' },
]
function getCourseName(id) { return courseOptions.find(c => c.id === id)?.name || '-' }
function getPaperName(id) { return paperOptions.find(p => p.id === id)?.name || '-' }

function statusType(s) {
  return s === '待开始' ? 'primary' : s === '进行中' ? 'warning' : 'info'
}

const pendingCount = computed(() => tableData.list.filter(r => r.status === '待开始').length)
const ongoingCount = computed(() => tableData.list.filter(r => r.status === '进行中').length)
const endedCount = computed(() => tableData.list.filter(r => r.status === '已结束').length)

const avgScore = computed(() => {
  if (!gradeData.length) return 0
  return (gradeData.reduce((s, r) => s + r.score, 0) / gradeData.length).toFixed(1)
})
const maxScore = computed(() => {
  if (!gradeData.length) return 0
  return Math.max(...gradeData.map(r => r.score))
})
const passRate = computed(() => {
  if (!gradeData.length) return 0
  return (gradeData.filter(r => r.score >= 60).length / gradeData.length * 100).toFixed(1)
})

const columns = [
  { label: '考试名称', prop: 'name', minWidth: 200 },
  { label: '所属课程', prop: 'courseId', width: 140 },
  { label: '考试日期', prop: 'examDate', width: 110 },
  { label: '考试时间', prop: 'startTime', width: 80 },
  { label: '时长', prop: 'duration', width: 70 },
  { label: '地点', prop: 'location', width: 120 },
  { label: '应考', prop: 'totalStudents', width: 70 },
  { label: '实考', prop: 'attendedStudents', width: 70 },
  { label: '状态', prop: 'status', width: 90 },
  { label: '操作', prop: 'action', width: 230, fixed: 'right' },
]

const tableData = reactive({ totalCount: 0, pageSize: 15, pageNo: 1, pageTotal: 0, list: [] })

function fetchData() {
  loading.value = true
  setTimeout(() => {
    const mockList = []
    for (let i = 0; i < 15; i++) {
      const idx = (tableData.pageNo - 1) * 15 + i
      mockList.push({
        id: idx + 1,
        name: ['数据结构期中考试', '操作系统期末考试', '计算机网络单元测试', '高等数学期中考试', '数据结构期末考试', '操作系统期中考试', '大学英语测试'][idx % 7],
        courseId: [1, 2, 3, 4, 1, 2, 4][idx % 7],
        paperId: [1, 2, 3, 4, 1, 2, 3][idx % 7],
        examDate: ['2026-06-10', '2026-06-20', '2026-05-15', '2026-06-25', '2026-07-01', '2026-05-28', '2026-06-18'][idx % 7],
        startTime: ['08:30', '14:00', '10:00', '14:30', '09:00', '13:30', '15:00'][idx % 7],
        duration: [90, 120, 60, 90, 120, 90, 60][idx % 7],
        location: ['教学楼301', '机房201', '教学楼105', '教学楼203', '教学楼302', '机房205', '语音室'][idx % 7],
        invigilator: ['张教授', '李教授', '王老师', '赵老师', '张教授', '赵老师', '王老师'][idx % 7],
        totalStudents: 45,
        attendedStudents: [0, 0, 43, 44, 0, 42, 45][idx % 7],
        status: ['待开始', '待开始', '已结束', '进行中', '待开始', '已结束', '已结束'][idx % 7],
        remark: '',
      })
    }
    tableData.list = mockList
    tableData.totalCount = 35
    tableData.pageTotal = 3
    loading.value = false
  }, 500)
}

function resetSearch() { searchForm.keyword = ''; searchForm.status = ''; searchForm.dateRange = null; fetchData() }
function onPageChange({ pageNo, pageSize }) { tableData.pageNo = pageNo; tableData.pageSize = pageSize; fetchData() }
function onSelectionChange(selection) { console.log('选中:', selection) }

function openAddDialog() {
  isEditing.value = false; editingId.value = null
  form.name = ''; form.courseId = null; form.paperId = null; form.examDate = ''
  form.startTime = ''; form.duration = 90; form.location = ''; form.invigilator = ''; form.remark = ''
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
function handleGrade(row) {
  gradeData.length = 0
  const names = ['张三', '李四', '王五', '赵六', '陈七', '周八']
  for (let i = 0; i < 6; i++) {
    gradeData.push({
      studentName: names[i],
      studentNo: '202600' + String(i + 1).padStart(4, '0'),
      score: 30 + Math.floor(Math.random() * 70),
    })
  }
  gradeDrawerVisible.value = true
}
function handleDelete(row) {
  ElMessageBox.confirm(`确定删除考试「${row.name}」？`, '删除确认', {
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
    ElMessage.success(isEditing.value ? '编辑成功' : '创建成功')
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
  .search-bar { display: flex; gap: 10px; margin-bottom: 16px; flex-shrink: 0; align-items: center; flex-wrap: wrap; } }
.stat-cards { display: flex; gap: 16px; margin-bottom: 16px; flex-shrink: 0;
  .stat-card { flex: 1; background: #f9fafc; border-radius: 8px; padding: 16px 20px; display: flex; flex-direction: column; gap: 6px;
    .stat-label { font-size: 13px; color: #909399; }
    .stat-value { font-size: 24px; font-weight: 700; color: #303133; } } }
.grade-summary { display: flex; gap: 20px; margin-top: 16px; padding: 12px 16px; background: #f5f7fa; border-radius: 8px; font-size: 13px; color: #606266; }
</style>

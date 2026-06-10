<template>
  <div class="profile-page">
    <div class="page-header">
      <h2>AI 学业画像</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleCalculateAll" :loading="calculating">
          全量画像生成
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">画像总数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color:#E6A23C">{{ stats.pending }}</div>
          <div class="stat-label">待计算</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color:#67C23A">{{ stats.green }}</div>
          <div class="stat-label">绿色（正常）</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color:#F56C6C">{{ stats.flagged }}</div>
          <div class="stat-label">黄色/红色预警</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-input v-model="filter.keyword" placeholder="学生姓名/学号" style="width: 200px" clearable @clear="fetchData" />
      <el-select v-model="filter.departmentId" placeholder="所属院系" clearable style="width: 150px" @change="fetchData">
        <el-option v-for="d in departmentOptions" :key="d.id" :label="d.name" :value="d.id" />
      </el-select>
      <el-select v-model="filter.riskLevel" placeholder="风险等级" clearable style="width: 130px" @change="fetchData">
        <el-option label="绿色" value="green" />
        <el-option label="黄色" value="yellow" />
        <el-option label="红色" value="red" />
      </el-select>
      <el-button type="primary" @click="fetchData">查询</el-button>
      <el-button @click="resetFilter">重置</el-button>
    </div>

    <!-- 表格 -->
    <BaseDataTable
      ref="tableRef"
      :columns="columns"
      :data="tableData"
      :loading="loading"
      @page-change="onPageChange"
    >
      <template #riskLevel="{ row }">
        <el-tag :type="riskTag(row.riskLevel)" size="small">{{ riskLabel(row.riskLevel) }}</el-tag>
      </template>
      <template #action="{ row }">
        <el-button type="primary" link size="small" @click="handleView(row)">查看详情</el-button>
        <el-button type="warning" link size="small" @click="handleRecalculate(row)">重新画像</el-button>
      </template>
    </BaseDataTable>

    <!-- 详情弹窗 -->
    <BaseDialog
      v-model:visible="dialogVisible"
      title="学业画像详情"
      width="700px"
      :show-cancel="true"
      :show-confirm="false"
      cancel-text="关闭"
      @cancel="dialogVisible = false"
    >
      <div v-if="detail" class="profile-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="学生姓名">{{ detail.studentName }}</el-descriptions-item>
          <el-descriptions-item label="学号">{{ detail.studentNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="GPA">{{ detail.gpa || '-' }}</el-descriptions-item>
          <el-descriptions-item label="风险等级">
            <el-tag :type="riskTag(detail.riskLevel)" size="small">{{ riskLabel(detail.riskLevel) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="综合得分">{{ detail.comprehensiveScore || '-' }}</el-descriptions-item>
          <el-descriptions-item label="趋势">{{ detail.trend || '-' }}</el-descriptions-item>
          <el-descriptions-item label="最后计算时间">{{ detail.lastCalcTime || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="profile-section">
          <h4>各项指标</h4>
          <div class="metrics-grid">
            <div class="metric-item">
              <span class="metric-label">出勤率</span>
              <span class="metric-value">{{ detail.attendanceRate ? (detail.attendanceRate * 100).toFixed(1) + '%' : '-' }}</span>
            </div>
            <div class="metric-item">
              <span class="metric-label">作业平均分</span>
              <span class="metric-value">{{ detail.homeworkAvg ? Number(detail.homeworkAvg).toFixed(1) : '-' }}</span>
            </div>
            <div class="metric-item">
              <span class="metric-label">考试平均分</span>
              <span class="metric-value">{{ detail.examAvg ? Number(detail.examAvg).toFixed(1) : '-' }}</span>
            </div>
            <div class="metric-item">
              <span class="metric-label">综合得分</span>
              <span class="metric-value">{{ detail.comprehensiveScore ? Number(detail.comprehensiveScore).toFixed(1) : '-' }}</span>
            </div>
          </div>
        </div>
      </div>
    </BaseDialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import BaseDataTable from '@/components/BaseDataTable.vue'
import BaseDialog from '@/components/BaseDialog.vue'
import { getProfilePage, calculateProfile, calculateAllProfiles, getProfileStatistics } from '@/api/ai'

const tableRef = ref(null)
const loading = ref(false)
const calculating = ref(false)
const dialogVisible = ref(false)
const detail = ref(null)

const filter = reactive({ keyword: '', departmentId: null, riskLevel: '' })
const departmentOptions = ref([])

const stats = reactive({
  total: 0,
  pending: 0,
  green: 0,
  flagged: 0,
})

const columns = [
  { label: '学生姓名', prop: 'studentName', width: 100 },
  { label: '学号', prop: 'studentNo', width: 130 },
  { label: 'GPA', prop: 'gpa', width: 80 },
  { label: '出勤率', prop: 'attendanceRate', width: 90 },
  { label: '作业均分', prop: 'homeworkAvg', width: 90 },
  { label: '考试均分', prop: 'examAvg', width: 90 },
  { label: '风险等级', prop: 'riskLevel', width: 90 },
  { label: '最后计算', prop: 'lastCalcTime', width: 170 },
  { label: '操作', prop: 'action', width: 160, fixed: 'right' },
]

const tableData = reactive({ totalCount: 0, pageSize: 15, pageNo: 1, pageTotal: 0, list: [] })

function riskTag(level) {
  const map = { green: '', yellow: 'warning', red: 'danger' }
  return map[level] || ''
}

function riskLabel(level) {
  const map = { green: '绿色', yellow: '黄色', red: '红色' }
  return map[level] || level || '-'
}

async function fetchData() {
  loading.value = true
  try {
    const params = { pageNo: tableData.pageNo, pageSize: tableData.pageSize }
    if (filter.keyword) params.keyword = filter.keyword
    if (filter.departmentId) params.departmentId = filter.departmentId
    if (filter.riskLevel) params.riskLevel = filter.riskLevel
    const res = await getProfilePage(params)
    const d = res.data || {}
    tableData.list = d.list || []
    tableData.totalCount = d.totalCount || 0
    tableData.pageNo = d.pageNo || 1
    tableData.pageSize = d.pageSize || 15
    tableData.pageTotal = d.pageTotal || Math.ceil((d.totalCount || 0) / tableData.pageSize) || 0
  } catch (e) { /* ignored */ }
  finally { loading.value = false }
}

function resetFilter() {
  filter.keyword = ''
  filter.departmentId = null
  filter.riskLevel = ''
  fetchData()
}

function onPageChange({ pageNo, pageSize }) {
  tableData.pageNo = pageNo
  tableData.pageSize = pageSize
  fetchData()
}

async function handleCalculateAll() {
  calculating.value = true
  try {
    await ElMessageBox.confirm('确定要为所有学生生成学业画像吗？', '全量画像生成', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'info',
    })
    const res = await calculateAllProfiles()
    ElMessage.success(`画像生成任务已启动，共处理 ${res.data?.count || 0} 名学生`)
    await fetchStats()
  } catch (e) {
    if (e !== 'cancel') { /* ignored */ }
  } finally { calculating.value = false }
}

function handleView(row) {
  detail.value = { ...row }
  dialogVisible.value = true
}

async function handleRecalculate(row) {
  try {
    await ElMessageBox.confirm(`确定重新为「${row.studentName}」生成学业画像？`, '重新画像', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'info',
    })
    await calculateProfile(row.studentId)
    ElMessage.success('画像重新生成中')
    await fetchData()
    await fetchStats()
  } catch (e) {
    if (e !== 'cancel') { /* ignored */ }
  }
}

async function fetchStats() {
  try {
    const res = await getProfileStatistics()
    const d = res.data || {}
    stats.total = d.total || 0
    stats.pending = d.pending || 0
    stats.green = d.green || 0
    stats.flagged = (d.yellow || 0) + (d.red || 0)
  } catch (e) { /* ignored */ }
}

// 获取院系列表用于筛选
async function loadDepartments() {
  try {
    const res = await fetch('/api/departments/page?noPage=true')
    const data = await res.json()
    if (data.code === 0) {
      departmentOptions.value = data.data?.list || []
    }
  } catch {
    departmentOptions.value = []
  }
}

onMounted(() => { fetchData(); fetchStats(); loadDepartments() })
</script>

<style lang="scss" scoped>
.profile-page {
  height: 100%;
  display: flex;
  flex-direction: column;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-shrink: 0;
    margin-bottom: 16px;
    h2 {
      font-size: 20px;
      font-weight: 600;
      color: #303133;
      margin: 0;
    }
    .header-actions { display: flex; gap: 10px; }
  }

  .stats-row {
    margin-bottom: 16px;
    flex-shrink: 0;
  }

  .filter-bar {
    display: flex;
    gap: 10px;
    margin-bottom: 16px;
    flex-shrink: 0;
    align-items: center;
  }
}

.stat-card {
  text-align: center;
  padding: 8px 0;
  .stat-value {
    font-size: 28px;
    font-weight: 700;
    color: #303133;
  }
  .stat-label {
    font-size: 13px;
    color: #909399;
    margin-top: 6px;
  }
}

.profile-detail {
  padding: 8px 0;
  .profile-section {
    margin-top: 20px;
    h4 {
      font-size: 15px;
      color: #409EFF;
      margin: 0 0 12px;
    }
  }
  .metrics-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
    .metric-item {
      background: #f5f7fa;
      border-radius: 8px;
      padding: 12px 16px;
      display: flex;
      flex-direction: column;
      gap: 4px;
      .metric-label {
        font-size: 12px;
        color: #909399;
      }
      .metric-value {
        font-size: 18px;
        font-weight: 600;
        color: #303133;
      }
    }
  }
}
</style>

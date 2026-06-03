<template>
  <div class="warning-page">
    <div class="page-header">
      <h2>🔮 学业预警管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleEvaluateAll" :loading="evaluating">
          全量评估
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-red">
          <div class="stat-value">{{ stats.redCount }}</div>
          <div class="stat-label">🔴 红色预警</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-yellow">
          <div class="stat-value">{{ stats.yellowCount }}</div>
          <div class="stat-label">🟡 黄色预警</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-pending">
          <div class="stat-value">{{ stats.pendingCount }}</div>
          <div class="stat-label">⏳ 待处理</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-resolved">
          <div class="stat-value">{{ stats.resolvedCount }}</div>
          <div class="stat-label">✅ 已处理</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-select v-model="filter.level" placeholder="预警等级" clearable style="width: 130px" @change="fetchData">
        <el-option label="红色" value="red" />
        <el-option label="黄色" value="yellow" />
      </el-select>
      <el-select v-model="filter.status" placeholder="处理状态" clearable style="width: 130px" @change="fetchData">
        <el-option label="待处理" value="pending" />
        <el-option label="已处理" value="resolved" />
        <el-option label="已忽略" value="ignored" />
      </el-select>
      <el-select v-model="filter.warningType" placeholder="预警类型" clearable style="width: 140px" @change="fetchData">
        <el-option label="出勤" value="attendance" />
        <el-option label="作业" value="homework" />
        <el-option label="考试" value="exam" />
        <el-option label="综合" value="comprehensive" />
      </el-select>
      <el-button type="primary" @click="fetchData">查询</el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData.list" v-loading="loading" stripe border style="width: 100%">
      <el-table-column prop="studentId" label="学生ID" width="80" />
      <el-table-column label="预警类型" width="120">
        <template #default="{ row }">
          <el-tag :type="typeTag(row.warningType)" size="small">{{ typeLabel(row.warningType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="level" label="等级" width="80">
        <template #default="{ row }">
          <el-tag :type="row.level === 'red' ? 'danger' : 'warning'" size="small">
            {{ row.level === 'red' ? '🔴 红色' : '🟡 黄色' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="score" label="分值" width="80" />
      <el-table-column prop="threshold" label="阈值" width="80" />
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="suggestion" label="干预建议" min-width="200" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'resolved' ? 'success' : row.status === 'ignored' ? 'info' : 'warning'" size="small">
            {{ row.status === 'pending' ? '待处理' : row.status === 'resolved' ? '已处理' : '已忽略' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160">
        <template #default="{ row }">{{ row.createTime?.substring(0, 16) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 'pending'" size="small" type="success" @click="handleResolve(row)">
            处理
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrap" v-if="tableData.totalCount > 0">
      <el-pagination
        v-model:current-page="tableData.pageNo"
        v-model:page-size="tableData.pageSize"
        :total="tableData.totalCount"
        layout="total, sizes, prev, pager, next"
        background
        @change="fetchData" />
    </div>

    <!-- 处理对话框 -->
    <el-dialog v-model="resolveVisible" title="处理预警" width="400px">
      <el-form :model="resolveForm" label-width="80px">
        <el-form-item label="处理人">
          <el-input v-model="resolveForm.resolver" placeholder="处理人姓名" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resolveVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmResolve" :loading="resolving">确认处理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getWarningPage, evaluateAllWarnings, resolveWarning, getWarningStatistics } from '@/api/ai'

const loading = ref(false)
const evaluating = ref(false)
const resolving = ref(false)
const resolveVisible = ref(false)
const currentResolveId = ref(null)

const stats = reactive({ redCount: 0, yellowCount: 0, pendingCount: 0, resolvedCount: 0 })
const filter = reactive({ level: '', status: '', warningType: '' })
const resolveForm = reactive({ resolver: '' })
const tableData = reactive({ list: [], totalCount: 0, pageNo: 1, pageSize: 10 })

function typeLabel(type) {
  const map = { attendance: '出勤', homework: '作业', exam: '考试', comprehensive: '综合' }
  return map[type] || type
}

function typeTag(type) {
  const map = { attendance: '', homework: 'primary', exam: 'danger', comprehensive: 'warning' }
  return map[type] || ''
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getWarningPage({
      pageNo: tableData.pageNo,
      pageSize: tableData.pageSize,
      ...filter,
    })
    const d = res.data || {}
    tableData.list = d.list || []
    tableData.totalCount = d.totalCount || 0
    tableData.pageNo = d.pageNo || 1
    tableData.pageSize = d.pageSize || 10
  } catch (e) { /* ignored */ }
  finally { loading.value = false }
}

async function fetchStats() {
  try {
    const res = await getWarningStatistics()
    const d = res.data || {}
    stats.redCount = d.redCount || 0
    stats.yellowCount = d.yellowCount || 0
    stats.pendingCount = d.pendingCount || 0
    stats.resolvedCount = d.resolvedCount || 0
  } catch (e) { /* ignored */ }
}

async function handleEvaluateAll() {
  evaluating.value = true
  try {
    const res = await evaluateAllWarnings()
    ElMessage.success(`评估完成，共产生 ${res.data?.totalWarnings || 0} 条预警`)
    await fetchData()
    await fetchStats()
  } catch (e) { /* ignored */ }
  finally { evaluating.value = false }
}

function handleResolve(row) {
  currentResolveId.value = row.id
  resolveForm.resolver = ''
  resolveVisible.value = true
}

async function handleConfirmResolve() {
  if (!resolveForm.resolver) {
    ElMessage.warning('请输入处理人')
    return
  }
  resolving.value = true
  try {
    await resolveWarning(currentResolveId.value, { resolver: resolveForm.resolver })
    ElMessage.success('处理成功')
    resolveVisible.value = false
    await fetchData()
    await fetchStats()
  } catch (e) { /* ignored */ }
  finally { resolving.value = false }
}

onMounted(() => {
  fetchData()
  fetchStats()
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.page-header h2 { margin: 0; font-size: 18px; color: #303133; }

.stats-row { margin-bottom: 16px; }

.stat-card { text-align: center; cursor: default; }

.stat-value {
  font-size: 36px;
  font-weight: 700;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.stat-red .stat-value { color: #F56C6C; }
.stat-yellow .stat-value { color: #E6A23C; }
.stat-pending .stat-value { color: #409EFF; }
.stat-resolved .stat-value { color: #67C23A; }

.filter-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>

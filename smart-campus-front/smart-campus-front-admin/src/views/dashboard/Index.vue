<template>
  <div class="dashboard">
    <!-- ==================== 1. 校园概览 ==================== -->
    <section class="db-section">
      <div class="section-header">
        <span class="section-line" />
        <h3>校园概览</h3>
      </div>
      <div class="stat-grid">
        <StatCard
          v-for="s in overviewStats"
          :key="s.label"
          :label="s.label"
          :value="s.value"
          :unit="s.unit"
          :icon="s.icon"
          :bg-start="s.bgStart"
          :bg-end="s.bgEnd"
        />
      </div>
    </section>

    <!-- ==================== 2. 教学运行分析 ==================== -->
    <section class="db-section">
      <div class="section-header">
        <span class="section-line" />
        <h3>教学运行分析</h3>
      </div>
      <div class="teach-stats">
        <div class="teach-stat-item" v-for="s in teachingStats" :key="s.label">
          <span class="teach-stat-label">{{ s.label }}</span>
          <span class="teach-stat-value">{{ s.value.toLocaleString() }}</span>
          <span class="teach-stat-unit">{{ s.unit }}</span>
        </div>
      </div>
      <div class="chart-card">
        <h4 class="chart-title">教师授课工作量趋势</h4>
        <div ref="teacherChartRef" class="chart-box" />
      </div>
    </section>

    <!-- ==================== 3. 学生分析 ==================== -->
    <section class="db-section">
      <div class="section-header">
        <span class="section-line" />
        <h3>学生分析</h3>
      </div>
      <div class="grid-2col">
        <div class="chart-card">
          <h4 class="chart-title">院系学生分布</h4>
          <div ref="deptChartRef" class="chart-box" />
        </div>
        <div class="chart-card">
          <h4 class="chart-title">年级学生分布</h4>
          <div ref="gradeChartRef" class="chart-box" />
        </div>
        <div class="chart-card">
          <h4 class="chart-title">男女比例</h4>
          <div ref="genderChartRef" class="chart-box" />
        </div>
        <div class="chart-card">
          <h4 class="chart-title">学生增长趋势</h4>
          <div ref="growthChartRef" class="chart-box" />
        </div>
      </div>
    </section>

    <!-- ==================== 4. 资源分析 ==================== -->
    <section class="db-section">
      <div class="section-header">
        <span class="section-line" />
        <h3>资源分析</h3>
      </div>
      <div class="resource-stats">
        <div class="resource-stat-item">
          <span class="resource-stat-label">资源总量</span>
          <span class="resource-stat-value">{{ resourceData.total.toLocaleString() }}</span>
          <span class="resource-stat-unit">个</span>
        </div>
        <div class="resource-stat-item">
          <span class="resource-stat-label">总下载次数</span>
          <span class="resource-stat-value">{{ resourceData.totalDownloads.toLocaleString() }}</span>
          <span class="resource-stat-unit">次</span>
        </div>
      </div>
      <div class="grid-2col">
        <div class="chart-card">
          <h4 class="chart-title">资源上传趋势</h4>
          <div ref="uploadTrendRef" class="chart-box" />
        </div>
        <div class="chart-card">
          <h4 class="chart-title">热门资源 TOP10</h4>
          <div class="hot-table-wrapper">
            <div class="hot-item" v-for="(r, i) in resourceData.hotResources" :key="r.name">
              <span class="hot-rank" :class="{ 'hot-rank--top': i < 3 }">{{ i + 1 }}</span>
              <div class="hot-info">
                <span class="hot-name" :title="r.name">{{ r.name }}</span>
                <span class="hot-dept">{{ r.dept }}</span>
              </div>
              <span class="hot-downloads">{{ r.downloads.toLocaleString() }}<em>次下载</em></span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ==================== 5. 考试分析 ==================== -->
    <section class="db-section">
      <div class="section-header">
        <span class="section-line" />
        <h3>考试分析</h3>
      </div>
      <div class="exam-rate-row">
        <div class="exam-rate-card exam-rate-card--pass">
          <span class="exam-rate-label">及格率</span>
          <span class="exam-rate-value">{{ examData.passRate }}<em>%</em></span>
        </div>
        <div class="exam-rate-card exam-rate-card--excellent">
          <span class="exam-rate-label">优秀率</span>
          <span class="exam-rate-value">{{ examData.excellenceRate }}<em>%</em></span>
        </div>
      </div>
      <div class="grid-2col">
        <div class="chart-card">
          <h4 class="chart-title">考试数量趋势</h4>
          <div ref="examTrendRef" class="chart-box" />
        </div>
        <div class="chart-card">
          <h4 class="chart-title">各学院平均成绩</h4>
          <div ref="avgScoreRef" class="chart-box" />
        </div>
      </div>
    </section>

    <!-- ==================== 6. 系统运行监控 ==================== -->
    <section class="db-section">
      <div class="section-header">
        <span class="section-line" />
        <h3>系统运行监控</h3>
      </div>
      <div class="stat-grid">
        <StatCard
          v-for="s in systemStats"
          :key="s.label"
          :label="s.label"
          :value="s.value"
          :unit="s.unit"
          :icon="s.icon"
          :bg-start="s.bgStart"
          :bg-end="s.bgEnd"
        />
      </div>
      <div class="grid-2col" style="margin-top: 16px">
        <div class="chart-card">
          <h4 class="chart-title">系统资源使用</h4>
          <div class="sys-metrics">
            <div class="sys-metric-item">
              <span class="sys-metric-label">CPU 使用率</span>
              <el-progress
                :percentage="systemData.cpuUsage"
                :stroke-width="14"
                :format="() => `${systemData.cpuUsage}%`"
                color="#409EFF"
              />
            </div>
            <div class="sys-metric-item">
              <span class="sys-metric-label">内存使用率</span>
              <el-progress
                :percentage="systemData.memoryUsage"
                :stroke-width="14"
                :format="() => `${systemData.memoryUsage}%`"
                :status="systemData.memoryUsage > 80 ? 'exception' : 'success'"
                color="#67C23A"
              />
            </div>
            <div class="sys-metric-item">
              <span class="sys-metric-label">磁盘使用率</span>
              <el-progress
                :percentage="systemData.diskUsage"
                :stroke-width="14"
                :format="() => `${systemData.diskUsage}%`"
                :status="systemData.diskUsage > 80 ? 'exception' : 'warning'"
                color="#E6A23C"
              />
            </div>
          </div>
        </div>
        <div class="chart-card">
          <h4 class="chart-title">服务运行状态</h4>
          <div class="service-list">
            <div
              class="service-item"
              v-for="srv in systemData.services"
              :key="srv.name"
            >
              <span class="service-dot" :class="`service-dot--${srv.status}`" />
              <span class="service-name">{{ srv.name }}</span>
              <el-tag
                :type="srv.status === 'normal' ? 'success' : 'warning'"
                size="small"
                effect="dark"
              >
                {{ srv.status === 'normal' ? '正常' : '异常' }}
              </el-tag>
            </div>
          </div>
          <div class="system-status-badge">
            <span class="status-dot status-dot--ok" />
            <span>系统运行正常</span>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import {
  UserFilled,
  Reading,
  OfficeBuilding,
  Collection,
  School,
  Notebook,
  Clock,
  CircleCheckFilled,
  TrendCharts,
  DataLine,
  Monitor
} from '@element-plus/icons-vue'
import StatCard from '@/components/StatCard.vue'
import { getDashboardData } from './mockData.js'

const data = getDashboardData()

// -------------------- helpers --------------------
let chartInstances = []
const observers = []

function initChart(el, option) {
  if (!el) return null
  const chart = echarts.init(el)
  chart.setOption(option)
  chartInstances.push(chart)
  const ro = new ResizeObserver(() => chart.resize())
  ro.observe(el)
  observers.push(ro)
  return chart
}

function disposeAll() {
  observers.forEach(o => o.disconnect())
  chartInstances.forEach(c => c.dispose())
  chartInstances = []
  observers.splice(0)
}

function makeGradient(chart, color) {
  return chart && chart.getDom()
    ? new echarts.graphic.LinearGradient(0, 0, 1, 0, [
        { offset: 0, color },
        { offset: 1, color: '#36D1DC' }
      ])
    : color
}

const axisColor = '#9ca3af'
const splitColor = '#f0f0f0'

// -------------------- 1. overview --------------------
const overviewStats = [
  { label: '学生总人数', value: data.overview.totalStudents, unit: '人', icon: UserFilled, bgStart: '#409EFF', bgEnd: '#36D1DC' },
  { label: '教师总人数', value: data.overview.totalTeachers, unit: '人', icon: Reading, bgStart: '#667EEA', bgEnd: '#764BA2' },
  { label: '院系数量', value: data.overview.totalDepartments, unit: '个', icon: OfficeBuilding, bgStart: '#F093FB', bgEnd: '#F5576C' },
  { label: '专业数量', value: data.overview.totalMajors, unit: '个', icon: Collection, bgStart: '#4FACFE', bgEnd: '#00F2FE' },
  { label: '班级数量', value: data.overview.totalClasses, unit: '个', icon: School, bgStart: '#43E97B', bgEnd: '#38F9D7' },
  { label: '课程数量', value: data.overview.totalCourses, unit: '门', icon: Notebook, bgStart: '#FA709A', bgEnd: '#FEE140' }
]

// -------------------- 2. teaching --------------------
const teachingStats = [
  { label: '本学期开课', value: data.teaching.semesterCourses, unit: '门' },
  { label: '已完成考试', value: data.teaching.completedExams, unit: '场' },
  { label: '待进行考试', value: data.teaching.pendingExams, unit: '场' }
]

const teacherChartRef = ref(null)

// -------------------- 3. student --------------------
const deptChartRef = ref(null)
const gradeChartRef = ref(null)
const genderChartRef = ref(null)
const growthChartRef = ref(null)

// -------------------- 4. resource --------------------
const resourceData = data.resource
const uploadTrendRef = ref(null)

// -------------------- 5. exam --------------------
const examData = data.exam
const examTrendRef = ref(null)
const avgScoreRef = ref(null)

// -------------------- 6. system --------------------
const systemData = data.system
const systemStats = [
  { label: '在线用户', value: systemData.onlineUsers, unit: '人', icon: UserFilled, bgStart: '#409EFF', bgEnd: '#36D1DC' },
  { label: '今日登录', value: systemData.todayLogins, unit: '人', icon: TrendCharts, bgStart: '#667EEA', bgEnd: '#764BA2' },
  { label: '本周活跃', value: systemData.weeklyActive, unit: '人', icon: DataLine, bgStart: '#43E97B', bgEnd: '#38F9D7' },
  { label: '系统可用率', value: 99.9, unit: '%', icon: Monitor, bgStart: '#F093FB', bgEnd: '#F5576C' }
]

// -------------------- chart options --------------------
function getTeacherOption() {
  const d = data.teaching.teacherWorkload
  return {
    tooltip: { trigger: 'axis', backgroundColor: 'rgba(255,255,255,0.95)', borderColor: '#409EFF' },
    grid: { top: 20, right: 20, bottom: 24, left: 40 },
    xAxis: { type: 'category', data: d.map(i => i.month), axisLine: { lineStyle: { color: axisColor } }, axisLabel: { color: '#666' } },
    yAxis: { type: 'value', splitLine: { lineStyle: { color: splitColor } }, axisLabel: { color: '#666' } },
    series: [{
      type: 'line', smooth: true, data: d.map(i => i.count),
      symbol: 'circle', symbolSize: 7, lineStyle: { width: 3, color: '#409EFF' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64,158,255,0.35)' },
          { offset: 1, color: 'rgba(64,158,255,0.04)' }
        ])
      },
      itemStyle: { color: '#409EFF' }
    }]
  }
}

function getDeptOption() {
  const items = data.studentDistribution.departments.slice(0, 10).reverse()
  return {
    tooltip: { trigger: 'axis', backgroundColor: 'rgba(255,255,255,0.95)', borderColor: '#409EFF' },
    grid: { top: 10, right: 20, bottom: 10, left: 130 },
    xAxis: { type: 'value', splitLine: { lineStyle: { color: splitColor } }, axisLabel: { color: '#666' } },
    yAxis: { type: 'category', data: items.map(i => i.name), axisLine: false, axisLabel: { color: '#333', fontSize: 11 } },
    series: [{
      type: 'bar', data: items.map(i => i.value), barWidth: 14,
      itemStyle: {
        borderRadius: [0, 6, 6, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#409EFF' },
          { offset: 1, color: '#36D1DC' }
        ])
      }
    }]
  }
}

function getGradeOption() {
  const d = data.studentDistribution.grades
  return {
    tooltip: { trigger: 'axis', backgroundColor: 'rgba(255,255,255,0.95)', borderColor: '#409EFF' },
    grid: { top: 20, right: 20, bottom: 24, left: 40 },
    xAxis: { type: 'category', data: d.map(i => i.grade), axisLine: { lineStyle: { color: axisColor } }, axisLabel: { color: '#666' } },
    yAxis: { type: 'value', splitLine: { lineStyle: { color: splitColor } }, axisLabel: { color: '#666' } },
    series: [{
      type: 'bar', data: d.map(i => i.count), barWidth: 28,
      itemStyle: {
        borderRadius: [6, 6, 0, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#667EEA' },
          { offset: 1, color: '#764BA2' }
        ])
      }
    }]
  }
}

function getGenderOption() {
  const g = data.studentDistribution.gender
  return {
    tooltip: { trigger: 'item', backgroundColor: 'rgba(255,255,255,0.95)', borderColor: '#409EFF' },
    legend: { bottom: 0, textStyle: { color: '#666' } },
    series: [{
      type: 'pie', radius: ['45%', '70%'], center: ['50%', '45%'],
      avoidLabelOverlap: false,
      label: { show: true, formatter: '{d}%', color: '#333', fontWeight: 600 },
      emphasis: { scale: true },
      data: [
        { name: '男生', value: g.male, itemStyle: { color: '#409EFF' } },
        { name: '女生', value: g.female, itemStyle: { color: '#F56C6C' } }
      ]
    }]
  }
}

function getGrowthOption() {
  const d = data.studentDistribution.growth
  return {
    tooltip: { trigger: 'axis', backgroundColor: 'rgba(255,255,255,0.95)', borderColor: '#409EFF' },
    grid: { top: 20, right: 20, bottom: 24, left: 40 },
    xAxis: { type: 'category', data: d.map(i => i.year), axisLine: { lineStyle: { color: axisColor } }, axisLabel: { color: '#666' } },
    yAxis: { type: 'value', splitLine: { lineStyle: { color: splitColor } }, axisLabel: { color: '#666' } },
    series: [{
      type: 'line', smooth: true, data: d.map(i => i.count),
      symbol: 'circle', symbolSize: 8, lineStyle: { width: 3, color: '#67C23A' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(67,233,123,0.35)' },
          { offset: 1, color: 'rgba(67,233,123,0.04)' }
        ])
      },
      itemStyle: { color: '#67C23A' }
    }]
  }
}

function getUploadTrendOption() {
  const d = data.resource.uploadTrend
  return {
    tooltip: { trigger: 'axis', backgroundColor: 'rgba(255,255,255,0.95)', borderColor: '#409EFF' },
    grid: { top: 20, right: 20, bottom: 24, left: 40 },
    xAxis: { type: 'category', data: d.map(i => i.month), axisLine: { lineStyle: { color: axisColor } }, axisLabel: { color: '#666' } },
    yAxis: { type: 'value', splitLine: { lineStyle: { color: splitColor } }, axisLabel: { color: '#666' } },
    series: [{
      type: 'bar', data: d.map(i => i.count), barWidth: 24,
      itemStyle: {
        borderRadius: [4, 4, 0, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#4FACFE' },
          { offset: 1, color: '#00F2FE' }
        ])
      }
    }]
  }
}

function getExamTrendOption() {
  const d = data.exam.countTrend
  return {
    tooltip: { trigger: 'axis', backgroundColor: 'rgba(255,255,255,0.95)', borderColor: '#409EFF' },
    grid: { top: 20, right: 20, bottom: 24, left: 40 },
    xAxis: { type: 'category', data: d.map(i => i.month), axisLine: { lineStyle: { color: axisColor } }, axisLabel: { color: '#666' } },
    yAxis: { type: 'value', splitLine: { lineStyle: { color: splitColor } }, axisLabel: { color: '#666' } },
    series: [{
      type: 'line', smooth: true, data: d.map(i => i.count),
      symbol: 'diamond', symbolSize: 10, lineStyle: { width: 3, color: '#F56C6C' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(245,108,108,0.3)' },
          { offset: 1, color: 'rgba(245,108,108,0.04)' }
        ])
      },
      itemStyle: { color: '#F56C6C' }
    }]
  }
}

function getAvgScoreOption() {
  const d = data.exam.avgScores
  return {
    tooltip: { trigger: 'axis', backgroundColor: 'rgba(255,255,255,0.95)', borderColor: '#409EFF' },
    grid: { top: 20, right: 20, bottom: 28, left: 40 },
    xAxis: { type: 'category', data: d.map(i => i.name), axisLabel: { rotate: 20, color: '#666', fontSize: 11 }, axisLine: { lineStyle: { color: axisColor } } },
    yAxis: { type: 'value', min: 60, splitLine: { lineStyle: { color: splitColor } }, axisLabel: { color: '#666' } },
    series: [{
      type: 'bar', data: d.map(i => ({
        value: i.value,
        itemStyle: {
          color: i.value >= 80 ? '#67C23A' : (i.value >= 75 ? '#E6A23C' : '#F56C6C'),
          borderRadius: [4, 4, 0, 0]
        }
      })), barWidth: 28,
      label: { show: true, position: 'top', color: '#666', fontSize: 11, formatter: '{c}分' }
    }]
  }
}

// -------------------- lifecycle --------------------
onMounted(async () => {
  await nextTick()
  initChart(teacherChartRef.value, getTeacherOption())
  initChart(deptChartRef.value, getDeptOption())
  initChart(gradeChartRef.value, getGradeOption())
  initChart(genderChartRef.value, getGenderOption())
  initChart(growthChartRef.value, getGrowthOption())
  initChart(uploadTrendRef.value, getUploadTrendOption())
  initChart(examTrendRef.value, getExamTrendOption())
  initChart(avgScoreRef.value, getAvgScoreOption())
})

onUnmounted(() => {
  disposeAll()
})
</script>

<style lang="scss" scoped>
.dashboard {
  padding: 20px;
  min-height: 100vh;
  background: #f0f5ff;
}

// ---------- section ----------
.db-section {
  background: #fff;
  border-radius: 12px;
  padding: 20px 24px;
  margin-bottom: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.3s;

  &:hover {
    box-shadow: 0 2px 12px rgba(64, 158, 255, 0.1);
  }
}

.section-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;

  .section-line {
    display: inline-block;
    width: 4px;
    height: 18px;
    border-radius: 2px;
    background: linear-gradient(180deg, #409EFF, #36D1DC);
  }

  h3 {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin: 0;
  }
}

// ---------- stat grid ----------
.stat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

// ---------- teaching stats ----------
.teach-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.teach-stat-item {
  background: linear-gradient(135deg, #f0f9ff 0%, #e8f4fd 100%);
  border: 1px solid rgba(64, 158, 255, 0.15);
  border-radius: 10px;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  transition: transform 0.25s;

  &:hover { transform: translateY(-2px); }

  .teach-stat-label { font-size: 13px; color: #909399; }
  .teach-stat-value { font-size: 24px; font-weight: 700; color: #409EFF; }
  .teach-stat-unit { font-size: 12px; color: #909399; margin-left: 2px; }
}

// ---------- chart card ----------
.chart-card {
  border: 1px solid #ebeef5;
  border-radius: 10px;
  padding: 16px;
  transition: box-shadow 0.3s;

  &:hover {
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  }
}

.chart-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px;
  padding-left: 10px;
  border-left: 3px solid #409EFF;
}

.chart-box {
  width: 100%;
  height: 280px;
}

// ---------- grid ----------
.grid-2col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

// ---------- resource ----------
.resource-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.resource-stat-item {
  background: linear-gradient(135deg, #fefce8 0%, #fef3c7 100%);
  border: 1px solid rgba(230, 162, 60, 0.2);
  border-radius: 10px;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 4px;

  .resource-stat-label { font-size: 13px; color: #909399; }
  .resource-stat-value { font-size: 24px; font-weight: 700; color: #E6A23C; }
  .resource-stat-unit { font-size: 12px; color: #909399; margin-left: 2px; }
}

// ---------- hot resources ----------
.hot-table-wrapper {
  max-height: 260px;
  overflow-y: auto;

  &::-webkit-scrollbar { width: 4px; }
  &::-webkit-scrollbar-thumb { background: #dcdfe6; border-radius: 2px; }
}

.hot-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 4px;
  border-bottom: 1px solid #f2f2f2;

  &:last-child { border-bottom: none; }
}

.hot-rank {
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 700;
  color: #909399;
  background: #f5f5f5;
  flex-shrink: 0;

  &--top {
    color: #fff;
    background: linear-gradient(135deg, #409EFF, #36D1DC);
  }
}

.hot-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;

  .hot-name {
    font-size: 13px;
    color: #303133;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .hot-dept {
    font-size: 11px;
    color: #c0c4cc;
  }
}

.hot-downloads {
  font-size: 13px;
  font-weight: 600;
  color: #E6A23C;
  white-space: nowrap;

  em {
    font-style: normal;
    font-size: 11px;
    font-weight: 400;
    color: #c0c4cc;
    margin-left: 2px;
  }
}

// ---------- exam rate ----------
.exam-rate-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.exam-rate-card {
  border-radius: 10px;
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  transition: transform 0.25s;

  &:hover { transform: translateY(-2px); }

  &--pass {
    background: linear-gradient(135deg, #f0f9eb 0%, #e5f7d9 100%);
    border: 1px solid rgba(103, 194, 58, 0.2);
    .exam-rate-value { color: #67C23A; }
  }

  &--excellent {
    background: linear-gradient(135deg, #fdf6ec 0%, #faecd8 100%);
    border: 1px solid rgba(230, 162, 60, 0.2);
    .exam-rate-value { color: #E6A23C; }
  }

  .exam-rate-label { font-size: 14px; color: #909399; }

  .exam-rate-value {
    font-size: 36px;
    font-weight: 700;
    em { font-style: normal; font-size: 18px; font-weight: 400; margin-left: 2px; }
  }
}

// ---------- system ----------
.sys-metrics {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 8px 0;
}

.sys-metric-item {
  display: flex;
  flex-direction: column;
  gap: 6px;

  .sys-metric-label {
    font-size: 13px;
    color: #606266;
  }
}

.service-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 16px;
}

.service-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  border-radius: 8px;
  background: #fafafa;
  transition: background 0.2s;

  &:hover { background: #f0f5ff; }

  .service-name { flex: 1; font-size: 13px; color: #303133; }
}

.service-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;

  &--normal { background: #67C23A; box-shadow: 0 0 6px rgba(103, 194, 58, 0.5); }
  &--warning { background: #E6A23C; box-shadow: 0 0 6px rgba(230, 162, 60, 0.5); }
}

.system-status-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  border-radius: 8px;
  background: linear-gradient(135deg, #f0f9eb, #e5f7d9);
  font-size: 13px;
  font-weight: 600;
  color: #67C23A;
}

.status-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;

  &--ok {
    background: #67C23A;
    animation: pulse 2s infinite;
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; box-shadow: 0 0 0 0 rgba(103, 194, 58, 0.5); }
  50% { opacity: 0.8; box-shadow: 0 0 0 6px rgba(103, 194, 58, 0); }
}

// ---------- responsive ----------
@media (max-width: 1024px) {
  .grid-2col { grid-template-columns: 1fr; }
  .stat-grid { grid-template-columns: repeat(3, 1fr); }
  .exam-rate-row { grid-template-columns: 1fr 1fr; }
  .resource-stats { grid-template-columns: 1fr 1fr; }
}

@media (max-width: 768px) {
  .stat-grid { grid-template-columns: repeat(2, 1fr); }
  .teach-stats { grid-template-columns: 1fr; }
  .resource-stats { grid-template-columns: 1fr; }
  .exam-rate-row { grid-template-columns: 1fr; }
  .chart-box { height: 220px; }
}
</style>

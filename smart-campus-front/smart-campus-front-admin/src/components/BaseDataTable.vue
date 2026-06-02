<script setup>
/**
 * BaseDataTable.vue — 通用数据表格组件
 *
 * 功能：
 *  - 集成 Element Plus el-table + el-pagination
 *  - 支持复选框多选
 *  - 单元格支持通过命名插槽自定义内容
 *  - 自动计算高度实现行内滚动，外层无滚动条
 *  - 宽度 100%
 *
 * 数据格式：
 *  { totalCount, pageSize, pageNo, pageTotal, list }
 *
 * 调用示例：
 *  <BaseDataTable
 *    :columns="columns"
 *    :data="tableData"
 *    :loading="loading"
 *    @page-change="onPageChange"
 *    @selection-change="onSelectionChange"
 *  >
 *    <template #status="{ row }">
 *      <el-tag :type="row.status ? 'success' : 'danger'">
 *        {{ row.status ? '启用' : '禁用' }}
 *      </el-tag>
 *    </template>
 *  </BaseDataTable>
 */
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'

// ============================================
// Props
// ============================================
const props = defineProps({
  /** 列定义 [{ label, prop, width, minWidth, fixed, align, sortable, showOverflowTooltip }] */
  columns: {
    type: Array,
    required: true,
    default: () => [],
  },
  /** 分页数据 { totalCount, pageSize, pageNo, pageTotal, list } */
  data: {
    type: Object,
    default: null,
  },
  /** 加载状态 */
  loading: {
    type: Boolean,
    default: false,
  },
  /** 是否显示复选框列 */
  selectable: {
    type: Boolean,
    default: true,
  },
  /** 每页条数选项 */
  pageSizes: {
    type: Array,
    default: () => [10, 15, 20, 30, 50],
  },
  /** 行数据的唯一键 */
  rowKey: {
    type: String,
    default: 'id',
  },
  /** 表格边框 */
  border: {
    type: Boolean,
    default: false,
  },
  /** 固定高度（如果传此值则不自动计算） */
  height: {
    type: Number,
    default: null,
  },
})

// ============================================
// Emits
// ============================================
const emit = defineEmits([
  'selection-change',
  'update:pageNo',
  'update:pageSize',
  'page-change',
])

// ============================================
// Refs
// ============================================
const containerRef = ref(null)
const tableRef = ref(null)
const containerHeight = ref(0)

// ============================================
// 分页双向绑定
// ============================================
const pageNoModel = computed({
  get: () => props.data?.pageNo || 1,
  set: (val) => {
    emit('update:pageNo', val)
    emit('page-change', { pageNo: val, pageSize: pageSizeModel.value })
  },
})

const pageSizeModel = computed({
  get: () => props.data?.pageSize || 15,
  set: (val) => {
    emit('update:pageSize', val)
    emit('page-change', { pageNo: pageNoModel.value, pageSize: val })
  },
})

// ============================================
// 表格高度计算
//  el-table 的 height 控制内部滚动，外层无滚动条
// ============================================
const PAGINATION_HEIGHT = 44
const TABLE_GUTTER = 4

const tableHeight = computed(() => {
  if (props.height) return props.height
  if (!containerHeight.value) return null
  return Math.max(containerHeight.value - PAGINATION_HEIGHT - TABLE_GUTTER, 100)
})

// ============================================
// ResizeObserver — 容器尺寸变化时重新计算高度
// ============================================
let observer = null

function initObserver() {
  if (!containerRef.value) return
  // 读取初始高度
  containerHeight.value = containerRef.value.clientHeight

  observer = new ResizeObserver(([entry]) => {
    containerHeight.value = entry.contentRect.height
  })
  observer.observe(containerRef.value)
}

onMounted(() => {
  nextTick(initObserver)
})

onBeforeUnmount(() => {
  observer?.disconnect()
})

// ============================================
// 事件处理
// ============================================
function handleSelectionChange(selection) {
  emit('selection-change', selection)
}

// ============================================
// 暴露内部方法给父组件
// ============================================
function clearSelection() {
  tableRef.value?.clearSelection()
}
function toggleRowSelection(row, selected) {
  tableRef.value?.toggleRowSelection(row, selected)
}
function toggleAllSelection() {
  tableRef.value?.toggleAllSelection()
}

defineExpose({
  tableRef,
  clearSelection,
  toggleRowSelection,
  toggleAllSelection,
})
</script>

<template>
  <div ref="containerRef" class="base-table">
    <!-- 表格 -->
    <el-table
      ref="tableRef"
      :data="data?.list ?? []"
      :height="tableHeight"
      :border="border"
      :row-key="rowKey"
      :highlight-current-row="true"
      stripe
      style="width: 100%"
      @selection-change="handleSelectionChange"
      v-loading="loading"
      element-loading-text="加载中…"
    >
      <!-- 多选列 -->
      <el-table-column
        v-if="selectable"
        type="selection"
        width="50"
        align="center"
        fixed="left"
      />

      <!-- 序号列 -->
      <el-table-column
        type="index"
        label="序号"
        width="60"
        align="center"
        fixed="left"
      />

      <!-- 数据列 -->
      <el-table-column
        v-for="col in columns"
        :key="col.prop"
        :prop="col.prop"
        :label="col.label"
        :width="col.width"
        :min-width="col.minWidth"
        :fixed="col.fixed"
        :align="col.align || 'center'"
        :sortable="col.sortable || false"
        :show-overflow-tooltip="col.showOverflowTooltip ?? true"
        :header-align="col.headerAlign || 'center'"
      >
        <template #default="scope">
          <slot
            :name="col.prop"
            :row="scope.row"
            :column="scope.column"
            :index="scope.$index"
          >
            <span>{{ scope.row[col.prop] }}</span>
          </slot>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div v-if="data" class="base-table__pagination">
      <el-pagination
        v-model:current-page="pageNoModel"
        v-model:page-size="pageSizeModel"
        :page-sizes="pageSizes"
        :total="data.totalCount ?? 0"
        :layout="'total, sizes, prev, pager, next, jumper'"
        background
        small
      />
    </div>
  </div>
</template>

<style lang="scss" scoped>
.base-table {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  .el-table {
    flex-shrink: 0;
  }

  &__pagination {
    flex-shrink: 0;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    padding: 8px 0 0;
    // 随父容器缩放同步保持底部对齐
  }
}
</style>

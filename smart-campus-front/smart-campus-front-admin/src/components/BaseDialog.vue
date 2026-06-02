<script setup>
/**
 * BaseDialog.vue — 通用弹出框组件
 *
 * 功能：
 *  - 自定义标题、宽度（百分比或具体值）
 *  - 内容插槽，自动计算最大高度，超出内部滚动
 *  - 底部按钮区域，支持自定义事件和文案
 *  - 取消按钮为 link 样式，可通过参数控制显示
 *  - 右侧关闭按钮可通过参数控制显示
 *  - 内容区域 padding 可配置
 *  - 距离顶部距离可配置（默认 30px）
 */
import { computed } from 'vue'

// ============================================
// Props
// ============================================
const props = defineProps({
  /** 是否显示弹出框 */
  visible: {
    type: Boolean,
    default: false,
  },
  /** 弹出框标题 */
  title: {
    type: String,
    default: '提示',
  },
  /** 弹出框宽度 — 支持百分比（'50%'）或具体像素值（'600px'） */
  width: {
    type: String,
    default: '50%',
  },
  /** 对话框顶部间距（px） */
  top: {
    type: [Number, String],
    default: 30,
  },
  /** 是否显示取消按钮 */
  showCancel: {
    type: Boolean,
    default: true,
  },
  /** 取消按钮文案 */
  cancelText: {
    type: String,
    default: '取消',
  },
  /** 是否显示确定按钮 */
  showConfirm: {
    type: Boolean,
    default: true,
  },
  /** 确定按钮文案 */
  confirmText: {
    type: String,
    default: '确定',
  },
  /** 是否显示右侧关闭 (×) 按钮 */
  showClose: {
    type: Boolean,
    default: true,
  },
  /** 内容区域 padding */
  bodyPadding: {
    type: String,
    default: '28px',
  },
  /** 确定按钮加载状态 */
  confirmLoading: {
    type: Boolean,
    default: false,
  },
  /** 点击遮罩层是否关闭 */
  closeOnClickModal: {
    type: Boolean,
    default: false,
  },
  /** 弹出框自定义类名 */
  customClass: {
    type: String,
    default: '',
  },
})

// ============================================
// Emits
// ============================================
const emit = defineEmits([
  'update:visible',
  'confirm',
  'cancel',
  'open',
  'close',
])

// ============================================
// Model: visible 双向绑定
// ============================================
const visibleModel = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val),
})

// ============================================
// 容器高度引用 — 标题栏和底部按钮区域近似高度
// ============================================
const HEADER_HEIGHT = 56   // 标题栏近似高度
const FOOTER_HEIGHT = 68   // 底部按钮区域近似高度

// 内容区域最大高度 = 视口高度 - top - header - footer - 间距缓冲
const bodyMaxHeight = computed(() => {
  const topVal = typeof props.top === 'number' ? props.top : parseInt(props.top, 10) || 30
  const vh = window.innerHeight
  const maxH = vh - topVal - HEADER_HEIGHT - FOOTER_HEIGHT - 32 // 32px 为上下额外内边距缓冲
  return maxH > 100 ? `${maxH}px` : '100px'
})

// ============================================
// 对话框宽度计算
// ============================================
const dialogWidth = computed(() => {
  const w = props.width.trim()
  // 如果是纯数字或数字开头不带 %/px，默认视为百分比
  if (/^\d+(\.\d+)?$/.test(w)) return `${w}%`
  return w
})

// ============================================
// 事件处理
// ============================================
function handleOpen() {
  emit('open')
}

function handleClose() {
  emit('close')
}

function handleConfirm() {
  emit('confirm')
}

function handleCancel() {
  visibleModel.value = false
  emit('cancel')
}
</script>

<template>
  <el-dialog
    v-model="visibleModel"
    :title="title"
    :width="dialogWidth"
    :top="`${top}px`"
    :show-close="showClose"
    :close-on-click-modal="closeOnClickModal"
    :before-close="handleCancel"
    :class="['base-dialog', customClass]"
    @opened="handleOpen"
    @closed="handleClose"
  >
    <!-- ====== 内容区 ====== -->
    <div
      class="base-dialog__body"
      :style="{ padding: bodyPadding, maxHeight: bodyMaxHeight }"
    >
      <slot />
    </div>

    <!-- ====== 底部按钮区 ====== -->
    <template #footer>
      <div class="base-dialog__footer">
        <slot name="footer">
          <el-button
            v-if="showCancel"
            link
            type="info"
            :disabled="confirmLoading"
            @click="handleCancel"
          >
            {{ cancelText }}
          </el-button>
          <el-button
            v-if="showConfirm"
            type="primary"
            :loading="confirmLoading"
            @click="handleConfirm"
          >
            {{ confirmText }}
          </el-button>
        </slot>
      </div>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
.base-dialog {
  // 标题区域
  :deep(.el-dialog__header) {
    padding: 16px 28px;
    margin: 0;
    border-bottom: 1px solid #f0f0f0;

    .el-dialog__title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
  }

  // 内容区域 — 滚动由内部 div 控制
  :deep(.el-dialog__body) {
    padding: 0;
    overflow: hidden;
  }

  // 底部区域
  :deep(.el-dialog__footer) {
    padding: 16px 28px;
    margin: 0;
    border-top: 1px solid #f0f0f0;
  }

  &__body {
    overflow-y: auto;
    box-sizing: border-box;
  }

  &__footer {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    gap: 12px;
  }
}
</style>

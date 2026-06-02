<script setup>
/**
 * BaseDrawer.vue — 通用抽屉组件
 *
 * 功能：
 *  - 自定义标题、宽度（百分比或具体像素值）
 *  - 内容插槽，超出高度内部滚动
 *  - 底部按钮区域，支持自定义事件和文案
 *  - 取消按钮为 link 样式，可通过参数控制显示
 *  - 右侧关闭按钮可通过参数控制显示
 *  - 内容区域 padding 可配置
 *
 * 使用方式与 BaseDialog 一致，区别为抽屉从右侧滑入。
 *
 * 调用示例：
 *  <BaseDrawer
 *    v-model:visible="drawerVisible"
 *    title="编辑信息"
 *    size="500px"
 *    @confirm="handleConfirm"
 *    @cancel="drawerVisible = false"
 *  >
 *    <el-form>...</el-form>
 *  </BaseDrawer>
 */
import { computed } from 'vue'

// ============================================
// Props
// ============================================
const props = defineProps({
  /** 是否显示抽屉 */
  visible: {
    type: Boolean,
    default: false,
  },
  /** 抽屉标题 */
  title: {
    type: String,
    default: '提示',
  },
  /** 抽屉宽度 — 支持百分比（'50%'）或具体像素值（'500px'） */
  size: {
    type: String,
    default: '450px',
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
  /** 是否显示右上角关闭 (×) 按钮 */
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
  /** 抽屉弹出方向: rtl / ltr / ttb / btt */
  direction: {
    type: String,
    default: 'rtl',
  },
  /** 抽屉自定义类名 */
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
// 抽屉 size 归一化
//   el-drawer 的 size 支持 px / % / vh
//   纯数字默认补 %
// ============================================
const drawerSize = computed(() => {
  const s = props.size.trim()
  if (/^\d+(\.\d+)?$/.test(s)) return `${s}%`
  return s
})

// ============================================
// 内容区域最大高度
//   drawer 占满视口高度，扣除 header + footer + 上下内边距
// ============================================
const HEADER_HEIGHT = 56
const FOOTER_HEIGHT = 68

const bodyMaxHeight = computed(() => {
  const vh = window.innerHeight
  const maxH = vh - HEADER_HEIGHT - FOOTER_HEIGHT - 32
  return `${Math.max(maxH, 100)}px`
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
  <el-drawer
    v-model="visibleModel"
    :title="title"
    :size="drawerSize"
    :direction="direction"
    :show-close="showClose"
    :close-on-click-modal="closeOnClickModal"
    :before-close="handleCancel"
    :class="['base-drawer', customClass]"
    @opened="handleOpen"
    @closed="handleClose"
  >
    <!-- ====== 内容区 ====== -->
    <div
      class="base-drawer__body"
      :style="{ padding: bodyPadding, maxHeight: bodyMaxHeight }"
    >
      <slot />
    </div>

    <!-- ====== 底部按钮区 ====== -->
    <template #footer>
      <div class="base-drawer__footer">
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
  </el-drawer>
</template>

<style lang="scss" scoped>
.base-drawer {
  // 标题区域
  :deep(.el-drawer__header) {
    padding: 16px 24px;
    margin: 0;
    border-bottom: 1px solid #f0f0f0;

    .el-drawer__title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      flex: 1;
    }
  }

  // 内容区域 — 滚动由内部 div 控制，el-drawer 默认 overflow: hidden
  :deep(.el-drawer__body) {
    padding: 0;
    overflow: hidden;
    display: flex;
    flex-direction: column;
  }

  // 底部区域（放在 el-drawer__body 外部通过 #footer 插槽渲染）
  :deep(.el-drawer__footer) {
    padding: 16px 24px;
    margin: 0;
    border-top: 1px solid #f0f0f0;
    flex-shrink: 0;
  }

  &__body {
    flex: 1;
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

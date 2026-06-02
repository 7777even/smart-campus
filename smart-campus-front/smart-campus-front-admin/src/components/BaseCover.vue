<script setup>
/**
 * BaseCover.vue — 通用封面/头像图片组件
 *
 * 适用场景：课程封面、个人头像、商品图片、轮播图等。
 *
 * 功能：
 *  - 自定义宽度
 *  - 自定义圆角大小
 *  - 自定义宽高比（aspect-ratio）
 *  - 图片加载失败时显示占位图标
 *  - 圆形 / 圆角矩形 / 直角 一键切换
 *  - 可选外阴影
 */
import { ref, computed } from 'vue'

// ============================================
// Props
// ============================================
const props = defineProps({
  /** 图片 URL（必填） */
  src: {
    type: String,
    default: '',
  },
  /** 图片 alt 描述 */
  alt: {
    type: String,
    default: '',
  },
  /** 封面宽度 — 支持 '80px' / '50%' / '10rem' 等任意 CSS 单位 */
  width: {
    type: String,
    default: '120px',
  },
  /** 宽高比格式 'w:h'，如 '4:3'、'1:1'、'16:9'。设为 'auto' 则由图片原始尺寸决定 */
  ratio: {
    type: String,
    default: '1:1',
  },
  /** 圆角大小 */
  radius: {
    type: String,
    default: '8px',
  },
  /** 预设形状 — 覆盖 radius 设置：'circle' 圆形 / 'round' 圆角矩形 / 'square' 直角 */
  shape: {
    type: String,
    default: '',
    validator: (v) => ['', 'circle', 'round', 'square'].includes(v),
  },
  /** 是否添加浅阴影 */
  shadow: {
    type: Boolean,
    default: false,
  },
  /** 图片填充模式 — object-fit */
  fit: {
    type: String,
    default: 'cover',
    validator: (v) => ['fill', 'contain', 'cover', 'none', 'scale-down'].includes(v),
  },
  /** 图片加载失败时显示的占位图标颜色 */
  placeholderColor: {
    type: String,
    default: '#dcdfe6',
  },
  /** 占位背景色 */
  bgColor: {
    type: String,
    default: '#f5f7fa',
  },
})

// ============================================
// Emits
// ============================================
const emit = defineEmits(['load', 'error'])

// ============================================
// 图片加载状态
// ============================================
const loaded = ref(false)
const hasError = ref(false)

function handleLoad() {
  loaded.value = true
  hasError.value = false
  emit('load')
}

function handleError() {
  hasError.value = true
  loaded.value = false
  emit('error')
}

// ============================================
// 计算样式
// ============================================
const borderRadius = computed(() => {
  if (props.shape === 'circle') return '50%'
  if (props.shape === 'square') return '0'
  if (props.shape === 'round') return '8px'
  return props.radius
})

const aspectRatio = computed(() => {
  if (props.ratio === 'auto') return 'auto'
  const parts = props.ratio.split(':').map(Number)
  if (parts.length === 2 && parts[0] > 0 && parts[1] > 0) {
    return `${parts[0]} / ${parts[1]}`
  }
  return '1 / 1'
})

const wrapperStyle = computed(() => ({
  width: props.width,
  borderRadius: borderRadius.value,
  aspectRatio: aspectRatio.value,
  boxShadow: props.shadow ? '0 2px 8px rgba(0, 0, 0, 0.1)' : 'none',
  backgroundColor: props.bgColor,
}))

// ============================================
// 占位图标 — 根据形状渲染不同 SVG
// ============================================
const isCircle = computed(() => borderRadius.value === '50%')
</script>

<template>
  <div
    class="base-cover"
    :style="wrapperStyle"
    :title="alt"
  >
    <!-- 图片加载成功 -->
    <img
      v-show="src && !hasError"
      class="base-cover__img"
      :src="src"
      :alt="alt"
      :style="{ objectFit: fit }"
      @load="handleLoad"
      @error="handleError"
    />

    <!-- 加载失败/无图片时的占位 -->
    <div
      v-if="!src || hasError"
      class="base-cover__placeholder"
      :style="{ borderRadius: borderRadius.value }"
    >
      <!-- 圆形用 person 图标 -->
      <svg
        v-if="isCircle"
        class="base-cover__icon"
        viewBox="0 0 24 24"
        :fill="placeholderColor"
      >
        <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z" />
      </svg>
      <!-- 非圆形用 image 图标 -->
      <svg
        v-else
        class="base-cover__icon"
        viewBox="0 0 24 24"
        :fill="placeholderColor"
      >
        <path d="M21 19V5c0-1.1-.9-2-2-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2zM8.5 13.5l2.5 3.01L14.5 12l4.5 6H5l3.5-4.5z" />
      </svg>
    </div>

    <!-- 过渡中的加载指示器（浅色骨架） -->
    <div
      v-if="src && !loaded && !hasError"
      class="base-cover__skeleton"
    />
  </div>
</template>

<style lang="scss" scoped>
.base-cover {
  position: relative;
  display: inline-block;
  overflow: hidden;
  flex-shrink: 0;
  line-height: 0;

  // 真实图片
  &__img {
    width: 100%;
    height: 100%;
    display: block;
  }

  // 占位区域
  &__placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: inherit;
  }

  // 占位图标
  &__icon {
    width: 40%;
    height: 40%;
    opacity: 0.5;
  }

  // 骨架屏闪烁
  &__skeleton {
    position: absolute;
    inset: 0;
    background: linear-gradient(
      90deg,
      transparent 0%,
      rgba(255, 255, 255, 0.35) 50%,
      transparent 100%
    );
    background-size: 200% 100%;
    animation: shine 1.4s infinite;
  }
}

@keyframes shine {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}
</style>

<template>
  <div
    class="stat-card"
    :style="{
      background: `linear-gradient(135deg, ${bgStart} 0%, ${bgEnd} 100%)`
    }"
  >
    <div class="stat-card__icon" v-if="icon">
      <el-icon :size="32">
        <component :is="icon" />
      </el-icon>
    </div>
    <div class="stat-card__body">
      <span class="stat-card__label">{{ label }}</span>
      <span class="stat-card__value">
        <CountTo :end="value" :duration="duration" />
        <em v-if="unit" class="stat-card__unit">{{ unit }}</em>
      </span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

defineProps({
  label: { type: String, default: '' },
  value: { type: Number, default: 0 },
  unit: { type: String, default: '' },
  icon: { type: [String, Object], default: '' },
  bgStart: { type: String, default: '#409EFF' },
  bgEnd: { type: String, default: '#36D1DC' },
  duration: { type: Number, default: 2000 }
})

// Number scroll animation component
const CountTo = {
  props: {
    end: { type: Number, default: 0 },
    duration: { type: Number, default: 2000 }
  },
  setup(props) {
    const displayValue = ref(0)
    let startTime = null
    let rafId = null

    function animate(timestamp) {
      if (!startTime) startTime = timestamp
      const progress = Math.min((timestamp - startTime) / props.duration, 1)
      const eased = 1 - Math.pow(1 - progress, 3)
      displayValue.value = Math.floor(eased * props.end)
      if (progress < 1) {
        rafId = requestAnimationFrame(animate)
      }
    }

    onMounted(() => {
      rafId = requestAnimationFrame(animate)
    })

    onUnmounted(() => {
      if (rafId) cancelAnimationFrame(rafId)
    })

    return () => displayValue.value.toLocaleString()
  }
}
</script>

<style lang="scss" scoped>
.stat-card {
  position: relative;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;

  &::after {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, rgba(255,255,255,0.15) 0%, transparent 50%);
    pointer-events: none;
  }

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  }

  &__icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 56px;
    height: 56px;
    border-radius: 14px;
    background: rgba(255, 255, 255, 0.2);
    flex-shrink: 0;
    color: #fff;
  }

  &__body {
    display: flex;
    flex-direction: column;
    gap: 4px;
    z-index: 1;
  }

  &__label {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.85);
    white-space: nowrap;
  }

  &__value {
    font-size: 26px;
    font-weight: 700;
    color: #fff;
    font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', Arial, sans-serif;
  }

  &__unit {
    font-size: 13px;
    font-weight: 400;
    color: rgba(255, 255, 255, 0.75);
    margin-left: 4px;
    font-style: normal;
  }
}
</style>

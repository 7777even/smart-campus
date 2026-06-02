<template>
  <div class="page">
    <h2 class="page-title">数据看板</h2>
    <p class="page-desc">欢迎使用智慧校园后台管理系统</p>

    <div class="cover-showcase">
      <!-- 1️⃣ 圆形头像 -->
      <div class="cover-demo">
        <span class="cover-label">圆形头像 60px</span>
        <BaseCover
          src=""
          width="60px"
          shape="circle"
          :shadow="true"
        />
      </div>

      <!-- 2️⃣ 课程封面 4:3 -->
      <div class="cover-demo">
        <span class="cover-label">课程封面 4:3</span>
        <BaseCover
          src=""
          width="140px"
          ratio="4:3"
          radius="10px"
          :shadow="true"
        />
      </div>

      <!-- 3️⃣ 课程封面 16:9 -->
      <div class="cover-demo">
        <span class="cover-label">课程封面 16:9</span>
        <BaseCover
          src=""
          width="160px"
          ratio="16:9"
          radius="6px"
          :shadow="true"
        />
      </div>

      <!-- 4️⃣ 方形直角 -->
      <div class="cover-demo">
        <span class="cover-label">直角 1:1</span>
        <BaseCover
          src=""
          width="80px"
          shape="square"
          :shadow="true"
        />
      </div>

      <!-- 5️⃣ 超大圆角 -->
      <div class="cover-demo">
        <span class="cover-label">超大圆角 20px</span>
        <BaseCover
          src=""
          width="100px"
          ratio="1:1"
          radius="20px"
          :shadow="true"
          bg-color="#ecf5ff"
          placeholder-color="#a0cfff"
        />
      </div>

      <!-- 6️⃣ 实际图片示例（外部 URL） -->
      <div class="cover-demo">
        <span class="cover-label">图片加载示例</span>
        <BaseCover
          src="https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg"
          width="140px"
          ratio="4:3"
          radius="10px"
          :shadow="true"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import BaseCover from '@/components/BaseCover.vue'
</script>

<style lang="scss" scoped>
.page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 8px;
  }
  .page-desc {
    font-size: 14px;
    color: #909399;
    margin: 0 0 24px;
  }
}

.cover-showcase {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
}

.cover-demo {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;

  .cover-label {
    font-size: 13px;
    color: #909399;
  }
}
</style>

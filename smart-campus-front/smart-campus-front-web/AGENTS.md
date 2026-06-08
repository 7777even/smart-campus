# smart-campus-front-web 约束

## 工程定位
学生端前端，面向学生用户。Vue 3 + JavaScript + Vite + Element Plus。卡片式 UI，非后台表格风格。

## 目录结构
```
src/
  api/          业务接口
  components/   公共组件
  router/       路由配置
  stores/       状态管理
  views/        页面
```

## 现有页面
```
views/
  Home.vue         首页（课程列表等）
  Courses.vue      我的课程
  AiChat.vue       AI 助教对话
  Announcements.vue 公告列表
  Login.vue        登录页
  Profile.vue      个人中心
```

## 现有公共组件
- `PortalLayout.vue`：门户布局

## 设计约束
- **禁止使用后台表格 UI**（如 `el-table` 的分页表格风格）
- 使用**卡片列表**、图表等学习场景布局
- 视频播放走项目封装的视频播放组件（HLS + 进度续播），禁止引入第三方播放器
- 面向 PC 优先，移动端支持按需加媒体查询

## 登录态
- header 使用 `studentToken`
- token 来自学生端认证接口
- 路由守卫统一处理未登录跳转

## 新增页面步骤
1. `src/views/` 下新建 `.vue` 页面
2. `src/api/` 下新建对应 API 文件
3. 路由配置同步
4. 优先使用卡片/列表布局，不套用后台表格

## 当前待实现（参考后端 web 模块）
- 课程学习（章节/课时展示 + 视频播放）
- 在线考试
- 学习进度/分析
- 消息中心
- 学习计划

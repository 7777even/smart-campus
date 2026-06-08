# smart-campus-front-admin 约束

## 工程定位
管理后台前端，面向系统管理员和教师。Vue 3 + JavaScript + Vite + Element Plus。

## 目录结构
```
src/
  api/          业务模块接口文件（按模块分）
  components/   公共组件
  router/       路由配置 + 菜单配置
  stores/       Pinia 存储（登录态等）
  views/        页面，按业务子目录组织
  assets/       样式、图标
```

## 现有 API 模块
`auth.js`、`course.js`、`student.js`、`teacher.js`、`class.js`、`department.js`、`major.js`、`exam.js`、`exercise.js`、`paper.js`、`resource.js`、`announcement.js`、`permission.js`、`ai.js`、`dashboard.js`、`common.js`

## 现有页面
```
views/
  login/index.vue     登录页
  dashboard/           仪表盘
  departments/         院系管理
  majors/              专业管理
  classes/             班级管理
  students/            学生管理
  teachers/            教师管理
  courses/             课程管理
  resources/           资源管理
  exercises/           习题管理
  papers/              试卷管理
  exams/               考试管理
  announcements/       公告管理
  permissions/         权限管理
  ai/                  AI 管理
```

## 现有公共组件
- `AdminLayout.vue`：后台布局
- `BaseDataTable.vue`：标准数据表格（含分页）
- `BaseDialog.vue`：通用表单弹窗
- `BaseDrawer.vue`：通用抽屉
- `BaseCover.vue`：封面组件
- `StatCard.vue`：统计卡片

## UI 强制约束（三段式）
每**个管理页面必须遵守：
1. **搜索区**：表单筛选，下拉选择立即触发搜索
2. **表格区**：使用 `BaseDataTable` 组件，不要直接写 `el-table`
3. **分页**：表格自带分页，列表必须分页
4. **新增/编辑**：使用 `BaseDialog` 弹窗，抽独立组件文件
5. **操作列**：查看 / 编辑 / 删除，按钮显隐由权限控制
6. **删除**：必须二次确认
7. **状态**：用 `el-tag` 展示

## 新增页面步骤
1. `src/views/<模块名>/` 下新建目录
2. 创建列表页（三段式，引入 `BaseDataTable`）
3. 创建表单弹窗组件（`<模块名>Form.vue`，使用 `BaseDialog`）
4. `src/api/` 下新建 API 文件
5. 路由配置 + 菜单配置同步

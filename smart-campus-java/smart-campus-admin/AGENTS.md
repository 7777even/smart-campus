# smart-campus-admin 模块约束

## 包路径
- 基础包：`com.smart.campus.admin.*`
- 子包：`controller/`、`entity/`、`mappers/`、`service/`、`biz/`、`config/`、`task/`

## 已存在的业务模块

| 业务 | Controller | Service | Mapper | Entity |
|------|-----------|---------|--------|--------|
| 认证 | AuthController | - | SysUserMapper | SysUser |
| 仪表盘 | DashboardController | - | - | - |
| 院系 | DepartmentController | - | DepartmentMapper | Department |
| 专业 | MajorController | - | MajorMapper | Major |
| 班级 | ClazzController | - | ClazzMapper | Clazz |
| 学生 | StudentController | - | StudentMapper | Student |
| 教师 | TeacherController | - | TeacherMapper | Teacher |
| 课程 | CourseController, StudentCourseController | - | CourseMapper, StudentCourseMapper | Course |
| 资源 | ResourceController | - | ResourceMapper | Resource |
| 习题 | ExerciseController | - | ExerciseMapper | Exercise |
| 试卷 | PaperController | - | PaperMapper | Paper |
| 考试 | ExamController | - | ExamMapper | Exam |
| 公告 | AnnouncementController | AnnouncementService | AnnouncementMapper | Announcement |
| 权限 | PermissionController | SysPermissionService, SysRoleService, SysUserService | SysPermissionMapper, SysRoleMapper, SysUserMapper | SysPermission, SysRole |
| AI对话 | AiChatController | AiChatService | AiConversationMapper, AiMessageMapper | AiConversation, AiMessage |
| AI知识库 | AiKnowledgeController | AiKnowledgeService | AiKnowledgeDocMapper | AiKnowledgeDoc |
| 学业画像 | AiProfileController | AiProfileService | AiStudentProfileMapper | AiStudentProfile |
| 学业预警 | AiWarningController | AiWarningService | AiEarlyWarningMapper | AiEarlyWarning |
| 推荐 | RecommendController | RecommendService | - | - |
| 通用 | CommonController | - | - | - |

## 分层规范
- **Controller**：接收参数 → 校验 → 调用 Service/Biz → 返回 `R<T>`。不写业务逻辑。
- **Service**（本模块 `service/`）：单领域业务逻辑，事务边界在这层。
- **Biz**（`biz/`）：跨 Service 编排。**当前 biz 目录为空**——新增复杂编排逻辑优先放 biz，保持 Service 职责单一。
- **Mapper**（本模块 `mappers/`，XML 在 common 的 `resources/mappers/`）。

## 鉴权约束
- 所有管理端接口必须加权限注解。权限编码与菜单表 `sys_permission` 中的 `perms` 字段对应。
- 当前 `AuthController` 使用 `@RequestAttribute Long userId` 获取登录用户，这是变通做法。后续应统一走拦截器 + 登录上下文 ThreadLocal 方案。

## 当前不足纪要
1. 暂未实现全局权限拦截器（`AuthInterceptor` 在 common 但实际未被 admin 使用）
2. `biz/` 目录已创建但无实现文件
3. `task/` 目录已创建但无定时任务
4. 部分 Controller 缺少 `@Validated` 注解和参数校验
5. 权限注解体系待实现

## 新增模块步骤
1. `entity/` 下建 PO
2. `mappers/` 下建 Mapper 接口 + common 模块 resources/mappers/ 下建 XML
3. `service/` 下建 Service 接口 + `impl/` 下建实现
4. 需要编排时 `biz/` 下建 Biz
5. `controller/` 下建 Controller

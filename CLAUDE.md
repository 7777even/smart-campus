# Smart Campus 项目规则

高校在线学习平台，前后端分离，后端拆成两个独立服务（管理端 + 用户端），各自带独立拦截器与登录态。

# 项目结构

后端 `smart-campus-java/`（Maven 多模块）：

- `smart-campus-common`：公共模块，包路径 `com.smart.campus`。承载 PO / DTO / VO / Query、Service / Mapper、Redis 组件、异常、枚举、工具类。
- `smart-campus-admin`：管理端服务，包路径 `com.smart.admin`。承载 Controller、Biz（业务编排）、管理端专用 DTO/VO。独立启动，运行在管理端端口。
- `smart-campus-web`：用户端服务，包路径 `com.smart.web`。承载 Controller、Biz、用户端专用 DTO/VO。独立启动，运行在用户端端口。

依赖方向：`admin → common`、`web → common`。common 不允许反向依赖任何端。

前端 `smart-campus-front/`：

- `smart-campus-front-admin`：管理后台前端工程。
- `smart-campus-front-web`：学生端前端工程。

> 包名提醒：common 用 `com.smart.campus`，admin 用 `com.smart.admin`，web 用 `com.smart.web`。新增类必须落到正确的包路径，不要混。

# 系统角色

- 系统管理员：管理基础数据 + 资源 + 教学业务。
- 教师：维护资源 + 自己名下课程的教学业务。
- 学生：学习课程 + 在线考试 + 学习分析。

角色字段约定在 `UserInfo.roleType`。

# 接口与路径

- 全局上下文：`/api`，由 `server.servlet.context-path` 提供，路径中**不再重复**写 `admin` / `web`。
- 路径形式：`/api/<模块名>/<动作>`，模块名小驼峰；常用动作：`loadDataList`、`add`、`update`、`delete`、`detail`、`getXxxOptions`（下拉用）。
- 管理接口归 admin 模块、用户接口归 web 模块；两端互不复用 Controller。

# 鉴权与权限

- 管理端 token 走 header `adminToken`，由管理端登录拦截器校验，并配合 Redis 登录组件做有效期管理。
- 用户端 token 走 header `studentToken`，由用户端登录拦截器校验。
- 管理端接口必须打权限注解，权限编码与系统菜单表中的编码一一对应；权限不通过则抛业务异常。
- 当前登录用户统一从登录上下文持有器（ThreadLocal）取，禁止在 Controller / Service 里再次手动解析 token。

# 核心业务域

- 基础数据：院系、专业、班级、用户（含教师与学生）。
- 教学业务：课程 → 章节 → 课时 → 课时资源；选课通过班级与课程的关联表。
- 习题与试卷：题库、题目选项、试卷、试卷题目关联。
- 考试：考试信息、考试与班级关联。
- 作业评估：作业提交、提交题目。
- 学习数据：课程进度（课程级）、课时进度（含视频时间点）、学习日志。
- 资源：资源信息（视频/文档），上传走分片 + Redis 队列异步处理。
- 系统：菜单、角色菜单、系统通知、站内消息。

# 业务规则

- 课程：归属一名教师；教师只能维护自己的课程；学生通过班级选课。
- 章节：两级结构（章节 → 课时），课时下挂资源。
- 考试：客观题自动评分，主观题人工评分。
- 学习记录：服务端记录学习时长、视频时间点；前端按课时恢复播放进度。

# 字段与返回约定

- 后端统一返回 `ResponseVO<T>`，不直接返回 Entity / PO。
- 分页参数继承统一基类，分页结果使用统一的分页结果 VO；前后端分页字段保持一致。
- 数据库字段下划线、Java 字段驼峰，由 MyBatis 配置自动映射；**前端字段保持与后端驼峰一致**，禁止前端起别名。
- 时间字段统一格式化为 `yyyy-MM-dd HH:mm:ss`（GMT+8），禁止返回时间戳。
- 主键：业务实体多为 String UUID（如课程 ID / 章节 ID / 课时 ID / 资源 ID），基础数据多为自增整数。

# 禁止行为

- 禁止前后端字段不一致或私自重命名 PO 字段。
- 禁止生成 mock 数据，联调以真实接口为准。
- 禁止管理端接口不带权限注解。
- 禁止用户端接口暴露管理端字段（创建人、排序权重、审核状态等）。
- 禁止把仅一端使用的类放进 common 模块。
- 禁止 Controller / Biz / Service / Mapper 越层（Controller 不能直接调 Mapper，Mapper 不能反查 Service 等）。

# 子规则索引

- 后端通用：`smart-campus-java/CLAUDE.md`
- 前端通用：`smart-campus-front/CLAUDE.md`
- 各子模块 / 前端工程根目录下的 `AGENTS.md` 为补充约束。

# 测试规范

## 测试框架
- JUnit 5 + Mockito 5，使用 `@ExtendWith(MockitoExtension.class)`
- 测试类名 = `被测试类名 + Test`，放 `src/test/java/` 对应包路径
- 每个方法使用 `@DisplayName("中文描述")` 标注测试场景

## 覆盖要求
- **P0**：工具类 + 核心业务 Service 必须覆盖（AI对话、推荐引擎、学业画像、学业预警）
- **P1**：通用 CRUD Service 需有基类测试
- **P2**：Controller 集成测试（视情况补充）

## 每方法至少覆盖
1. 正常路径（happy path）
2. 异常路径（空数据、非法输入）
3. 边界值（极值、临界值）

## Mock 原则
- 只 Mock 外部依赖（Mapper、JdbcTemplate），不 Mock 被测试对象
- 使用 `@Captor` 验证传递给 Mapper 的关键参数
- 禁止 `MockitoJUnitRunner`，统一用 `@ExtendWith`

## 运行测试
```bash
mvn test                    # 全部
mvn test -pl smart-campus-admin -am    # 单模块
mvn verify                  # 含 JaCoCo 覆盖率报告
```

# 代码审查规范

- 提交前通过 `pre-commit-review` workflow 自动扫描
- 审查维度：正确性（N+1/事务/NPE）→ 安全（SQL注入/权限/越权）→ 架构规范（分层/包路径）→ 字段一致性 → 代码质量
- P0（必须修）、P1（建议修）、P2（值得关注）三级问题分级

# 自动化流水线

项目配置了 Claude Code 自动化流水线，定义在 `.claude/` 目录下：

## Hooks（自动触发）

| Hook | 触发时机 | 脚本 | 说明 |
|---|---|---|---|
| `PreCommit` | `git commit` 前 | `.claude/hooks/pre-commit.sh` | 快速检查暂存区变更：权限注解缺失、SQL 高危操作、Vue 直接 import axios |
| `PostStart` | 每次启动 Claude Code | `.claude/hooks/post-start.sh` | 环境检查：JDK / MySQL / Redis / Node 可达性、模块结构完整性 |

## Workflows（手动调用）

| Workflow | 命令 | 说明 |
|---|---|---|
| `pre-commit-review` | `/pre-commit-review` | 提交前全方位代码审查：Java/Vue/XML/SQL 多维扫描，检查 N+1、SQL 注入、空 catch、TODO 残留等 |
| `pr-checklist` | 调用 PR 检查流程 | PR 就绪验证：Maven 编译检查 + 单元测试 + 合规扫描 |

## 使用建议

- 提交前先运行 `/pre-commit-review` workflow 做全面审查（hook 只做快速基础检查）
- 提 PR 前运行 PR 检查流程，确保编译 + 测试 + 合规全部通过
- 首次启动后查看环境检查报告，确认开发环境正常
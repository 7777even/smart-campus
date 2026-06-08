# smart-campus-web 模块约束

## 包路径
- 基础包：`com.smart.campus.web.*`
- 子包：`controller/`、`entity/`、`biz/`、`config/`

## 当前状态
> **注意：web 模块处于早期阶段。** Controller、biz 目录为空，entity 目录仅有占位。学生端接口尚未实现。

## 已知的控制器预期（参考 admin 模块推算）
| 业务 | 说明 |
|------|------|
| 认证 | 学生端登录，header 使用 `studentToken` |
| 课程 | 学生选课列表、课程详情、学习进度 |
| 考试 | 在线考试、答题提交 |
| 学习记录 | 课时进度、视频时间点续播 |
| AI对话 | 学生与 AI 助教对话 |
| 个人中心 | 个人信息、学习分析 |

## 分层规范
- **Controller**：接收参数 → 校验 → 调用 Biz/Service → 返回 `R<T>`
- **Biz**（`biz/`）：业务编排层，当前为空
- **Entity**（`entity/`）：学生端专用 DTO/VO。不要放 PO（PO 应该在各模块自己的 entity 下，或共用 admin 的 entity）

## 鉴权约束
- 登录拦截器读 header `studentToken`
- 用户端接口禁止暴露管理端字段（如创建人、排序权重、审核状态等）

## 注意事项
- 禁止复用 admin 模块的 Controller。两端的接口各自独立维护。
- 学生端不需要权限注解（无角色细分），只需登录态校验。
- 学习进度、视频续播等需对接 Redis 组件。

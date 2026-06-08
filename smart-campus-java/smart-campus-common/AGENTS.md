# smart-campus-common 模块约束

## 包路径
- 基础包：`com.campus.*`（注意：实际代码为 `com.campus`，非 CLAUDE.md 所述的 `com.smart.campus`）
- 子包：`entity/`、`mappers/`、`service/`、`utils/`、`result/`、`exception/`、`config/`、`components/`

## 分层定位
- **PO/Entity**：`entity/` 下仅放基础公共实体（`BaseEntity`、`PageResult`），业务实体在各端模块自行管理
- **Mapper**：基类 `BaseMapper<T>`，提供泛型 CRUD 方法。各端模块的 Mapper 接口放在各自模块的 `mappers/` 下
- **Service**：`BaseService<T>`，提供通用 CRUD 实现。业务 Service 在各端模块
- **Result**：统一响应 `R<T>`，所有 Controller 返回必须走 R
- **Utils**：`JwtUtil`、`PasswordUtil` 等跨模块工具

## 约束
- 禁止在 common 引入端模块特有的依赖（admin/web 的包、类）
- 实体类不要在本模块放业务属性，仅放公共字段
- Mapper XML 放 common 的 `resources/mappers/` 下
- 日期/时间序列化统一由 `JacksonConfig` 管理

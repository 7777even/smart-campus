---
name: java-test
description: Smart Campus 后端测试规范 — JUnit 5 + Mockito 写单元测试的规则与模式。TRIGGER when 写新测试、补测试覆盖率、跑测试。SKIP when 任务不涉及 Java 测试。
---

# 测试框架

- **JUnit 5** (Jupiter)：`@Test`、`@DisplayName`、`@ParameterizedTest`
- **Mockito 5**：`@Mock`、`@InjectMocks`、`@Captor`、`@ExtendWith(MockitoExtension.class)`
- **断言**：JUnit 5 `assert*` / AssertJ（优先 JUnit 内置）

# 测试目录结构

```
smart-campus-common/src/test/java/com/campus/
    utils/         工具类测试
    result/        响应体测试
    exception/     异常处理测试
    service/       公共 Service 测试

smart-campus-admin/src/test/java/com/smart/campus/admin/
    service/       业务 Service 测试
    controller/    Controller 测试（可选）

smart-campus-web/src/test/java/com/smart/web/
    service/       用户端服务测试
    controller/    Controller 测试
```

# 测试类命名

- 测试类名 = `被测试类名 + Test`
- 示例：`JwtUtilTest`、`AiChatServiceTest`、`RecommendServiceTest`

# 方法命名与规范

- 使用 `@DisplayName("中文描述")` 方法级注解，标注测试场景
- 方法名用驼峰，传达测试意图如 `sendMessageUpdatesTitle`
- 遵循 Arrange-Act-Assert 三段式，空行分隔

# Mock 原则

1. **只 Mock 外部依赖**（Mapper、JdbcTemplate），不 Mock 被测试对象自己
2. **优先构造器注入**（`@InjectMocks`），不手工 set
3. **`@Mock` 类级声明**，`@InjectMocks` 自动注入
4. **使用 `ArgumentCaptor`** 验证传递给 Mapper 的参数，而非简单 verify
5. **禁止 `MockitoJUnitRunner`**，统一使用 `@ExtendWith(MockitoExtension.class)`

# 测试覆盖要求

| 优先级 | 覆盖目标 | 最低行覆盖率 |
|---|---|---|
| P0 | 工具类（JwtUtil、R、异常） | ≥ 90% |
| P0 | 核心业务 Service（AI、推荐） | ≥ 80% |
| P1 | 通用 CRUD Service（BaseService） | ≥ 70% |
| P2 | Controller（集成测试） | ≥ 60% |

# 测试场景模板（每个方法至少覆盖）

```
正常路径（happy path）
异常路径（异常输入、空数据）
边界值（最大值、最小值、空字符串）
```

# 典型 Mock 测试结构

```java
@ExtendWith(MockitoExtension.class)
class XxxServiceTest {

    @Mock
    private XxxMapper mapper;

    @InjectMocks
    private XxxService service;

    @Captor
    private ArgumentCaptor<XxxEntity> captor;

    @Test
    @DisplayName("方法名描述 — 正常场景")
    void methodNameHappyPath() {
        // Arrange
        when(mapper.selectById(1L)).thenReturn(someEntity);

        // Act
        var result = service.getById(1L);

        // Assert
        assertNotNull(result);
        verify(mapper).selectById(1L);
    }
}
```

# 运行测试

```bash
# 运行全部测试
mvn test

# 运行单个模块
mvn test -pl smart-campus-admin -am

# 运行单个测试类
mvn test -pl smart-campus-admin -Dtest=AiChatServiceTest

# 生成覆盖率报告
mvn verify
# 报告位置：target/site/jacoco/index.html
```

# 检查清单

- 测试类是否用 `@ExtendWith(MockitoExtension.class)` 而非 JUnit 4 Runner
- 是否覆盖了正常路径 + 异常路径 + 边界值
- Mock 是否仅作用于外部依赖，不 Mock 被测试对象
- 是否验证了 Mapper/Service 的关键参数（使用 ArgumentCaptor）
- 是否在 Service 方法抛异常时的行为被测试

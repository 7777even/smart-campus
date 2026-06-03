# 智慧校园 · 接口文档

> **基础 URL**: `http://localhost:6061/api` (管理后台) / `http://localhost:6060/api` (门户)
> **认证方式**: Bearer JWT Token (在 Header 中传递 `Authorization: Bearer <token>`)
> **响应格式**: 统一 `R<T>` 结构 `{ code: 200, msg: "操作成功", data: T }`
> **在线 Swagger UI**: `http://localhost:6061/api/swagger-ui/index.html`

---

## 目录

1. [认证模块](#1-认证模块)
2. [院系管理](#2-院系管理)
3. [专业管理](#3-专业管理)
4. [班级管理](#4-班级管理)
5. [学生管理](#5-学生管理)
6. [教师管理](#6-教师管理)
7. [课程管理](#7-课程管理)
8. [资源管理](#8-资源管理)
9. [习题管理](#9-习题管理)
10. [试卷管理](#10-试卷管理)
11. [考试管理](#11-考试管理)
12. [公告管理](#12-公告管理)
13. [权限管理](#13-权限管理)
14. [数据看板](#14-数据看板)
15. [通用字典接口](#15-通用字典接口)

---

## 1. 认证模块

### POST /auth/login
用户登录

**Request Body:**
```json
{
  "username": "admin",
  "password": "123456"
}
```

**Response (200):**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "realName": "系统管理员",
      "role": "super_admin"
    }
  }
}
```

### GET /auth/info
获取当前登录用户信息（需在 Header 中携带 Token）

**Response:**
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "系统管理员",
    "role": "super_admin",
    "email": "admin@campus.edu",
    "avatar": null,
    "status": 1
  }
}
```

---

## 2. 院系管理

### GET /api/departments
分页查询院系列表

**Query Params:** `pageNo`, `pageSize`, `keyword`(名称/编码), `status`

**Response:**
```json
{
  "code": 200,
  "data": {
    "totalCount": 12,
    "pageSize": 15,
    "pageNo": 1,
    "pageTotal": 1,
    "list": [
      {
        "id": 1,
        "name": "计算机科学与技术学院",
        "code": "CS",
        "leader": "刘院长",
        "phone": "13800138001",
        "status": 1,
        "sort": 1,
        "description": null,
        "createTime": "2026-01-01 00:00:00",
        "updateTime": "2026-01-01 00:00:00"
      }
    ]
  }
}
```

### GET /api/departments/{id}
获取院系详情

### POST /api/departments
新增院系

**Request Body:**
```json
{
  "name": "新学院",
  "code": "NEW",
  "leader": "负责人",
  "phone": "13800138000",
  "sort": 10,
  "description": "描述信息"
}
```

### PUT /api/departments/{id}
更新院系

### DELETE /api/departments/{id}
删除院系

---

## 3. 专业管理

### GET /api/majors
分页查询专业列表

**Query Params:** `pageNo`, `pageSize`, `keyword`, `departmentId`, `level`

### GET /api/majors/{id}
获取专业详情

### POST /api/majors
新增专业

**Request Body:**
```json
{
  "name": "软件工程",
  "code": "SE",
  "departmentId": 1,
  "level": "本科",
  "years": 4,
  "sort": 1,
  "description": ""
}
```

### PUT /api/majors/{id}
更新专业

### DELETE /api/majors/{id}
删除专业

---

## 4. 班级管理

### GET /api/classes
分页查询班级列表

**Query Params:** `pageNo`, `pageSize`, `keyword`, `departmentId`, `majorId`

### POST /api/classes
新增班级

**Request Body:**
```json
{
  "name": "软件工程2024级1班",
  "code": "SE2024-1",
  "departmentId": 1,
  "majorId": 1,
  "year": 2024,
  "studentCount": 45,
  "sort": 1,
  "status": 1,
  "description": ""
}
```

---

## 5. 学生管理

### GET /api/students
分页查询学生列表

**Query Params:** `pageNo`, `pageSize`, `keyword`(学号/姓名), `departmentId`, `majorId`, `gender`, `status`

### POST /api/students
新增学生

**Request Body:**
```json
{
  "studentNo": "20240001",
  "name": "王小明",
  "gender": "男",
  "departmentId": 1,
  "majorId": 1,
  "classId": 1,
  "phone": "13700001001",
  "email": "wxm@campus.edu",
  "status": "在读",
  "address": ""
}
```

---

## 6. 教师管理

### GET /api/teachers
分页查询教师列表

**Query Params:** `pageNo`, `pageSize`, `keyword`(工号/姓名), `departmentId`, `title`

### POST /api/teachers
新增教师

**Request Body:**
```json
{
  "teacherNo": "T2020001",
  "name": "张教授",
  "gender": "男",
  "departmentId": 1,
  "title": "教授",
  "degree": "博士",
  "phone": "13900001001",
  "email": "zhang@campus.edu",
  "intro": ""
}
```

---

## 7. 课程管理

### GET /api/courses
分页查询课程列表

**Query Params:** `pageNo`, `pageSize`, `keyword`, `departmentId`, `type`, `credit`

### POST /api/courses
新增课程

**Request Body:**
```json
{
  "name": "数据结构与算法",
  "code": "CS101",
  "departmentId": 1,
  "teacherId": 1,
  "teacherName": "张教授",
  "type": "必修",
  "credit": 4,
  "hours": 64,
  "location": "教学楼A101",
  "description": ""
}
```

---

## 8. 资源管理

### GET /api/resources
分页查询资源列表

**Query Params:** `pageNo`, `pageSize`, `keyword`, `type`, `category`

### POST /api/resources/upload
上传资源文件（multipart/form-data）

### PUT /api/resources/{id}
更新资源信息

### DELETE /api/resources/{id}
删除资源

---

## 9. 习题管理

### GET /api/exercises
分页查询习题列表

**Query Params:** `pageNo`, `pageSize`, `keyword`(题目), `type`, `difficulty`, `courseId`

### POST /api/exercises
新增习题

**Request Body:**
```json
{
  "courseId": 1,
  "type": "单选题",
  "difficulty": "中等",
  "question": "以下哪种数据结构是线性结构？",
  "optionA": "树",
  "optionB": "图",
  "optionC": "栈",
  "optionD": "二叉树",
  "answer": "C",
  "analysis": "栈是一种线性数据结构..."
}
```

---

## 10. 试卷管理

### GET /api/papers
分页查询试卷列表

**Query Params:** `pageNo`, `pageSize`, `keyword`, `courseId`, `status`

### POST /api/papers
新增试卷

**Request Body:**
```json
{
  "name": "期中考试卷",
  "courseId": 1,
  "totalScore": 100,
  "passScore": 60,
  "duration": 120,
  "singleCount": 20,
  "singleScore": 2,
  "multiCount": 10,
  "multiScore": 3,
  "judgeCount": 10,
  "judgeScore": 2,
  "description": ""
}
```

### PUT /api/papers/{id}/publish
发布试卷

---

## 11. 考试管理

### GET /api/exams
分页查询考试列表

**Query Params:** `pageNo`, `pageSize`, `keyword`, `status`, `startDate`, `endDate`

### POST /api/exams
新增考试

**Request Body:**
```json
{
  "name": "2024-2025期末考",
  "courseId": 1,
  "paperId": 1,
  "examDate": "2026-07-01",
  "startTime": "08:30",
  "duration": 120,
  "location": "教学楼A201",
  "invigilator": "李老师",
  "remark": ""
}
```

---

## 12. 公告管理

### GET /api/announcements
分页查询公告列表

**Query Params:** `pageNo`, `pageSize`, `keyword`, `level`, `status`

### POST /api/announcements
新增公告

**Request Body:**
```json
{
  "title": "期末考试安排通知",
  "content": "根据学校教学工作安排...",
  "publisher": "教务处",
  "level": "紧急",
  "status": "已发布"
}
```

### PUT /api/announcements/{id}/toggle
切换发布/草稿状态

---

## 13. 权限管理

### GET /api/permissions/users
分页查询用户列表

**Query Params:** `pageNo`, `pageSize`, `keyword`, `role`

### PUT /api/permissions/users/{id}
更新用户信息

### DELETE /api/permissions/users/{id}
删除用户

### PUT /api/permissions/users/{id}/toggle-status
切换用户启用/禁用状态

### GET /api/permissions/roles
查询所有角色列表

### GET /api/permissions/menus
查询权限菜单树

### PUT /api/permissions/roles/{roleId}/permissions
分配角色权限

**Request Body:**
```json
{
  "permissionIds": [1, 2, 3, 5, 8]
}
```

---

## 14. 数据看板

所有看板接口均为 GET，无需分页。

### GET /api/dashboard/overview
校园概览数据

**Response:**
```json
{
  "totalStudents": 8642,
  "totalTeachers": 523,
  "totalDepartments": 12,
  "totalMajors": 48,
  "totalClasses": 186,
  "totalCourses": 320
}
```

### GET /api/dashboard/teaching
教学运行分析

### GET /api/dashboard/students
学生分布分析

### GET /api/dashboard/resources
资源统计分析

### GET /api/dashboard/exams
考试统计分析

### GET /api/dashboard/system
系统运行监控

---

## 15. 通用字典接口

### GET /api/common/departments
获取所有院系（下拉选项）

**Response:**
```json
{
  "code": 200,
  "data": [
    { "id": 1, "name": "计算机科学与技术学院" },
    { "id": 2, "name": "数学与统计学院" }
  ]
}
```

### GET /api/common/majors?departmentId=1
按院系获取专业列表

### GET /api/common/teachers
获取所有教师列表

### GET /api/common/classes
获取所有班级列表

### GET /api/common/courses
获取所有课程列表

---

## 统一错误码说明

| Code | 含义 |
|------|------|
| 200 | 操作成功 |
| 401 | 未登录或 Token 过期 |
| 403 | 无权限访问 |
| 500 | 服务器内部错误 / 业务异常 |

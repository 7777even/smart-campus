/*
 * 智慧校园系统 — 数据库初始化脚本
 * Database: smart_campus (UTF8MB4)
 */

CREATE DATABASE IF NOT EXISTS `smart_campus` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `smart_campus`;

-- ============================================================
-- 1. 系统认证
-- ============================================================

CREATE TABLE `sys_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`    VARCHAR(64)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(256) NOT NULL COMMENT '密码(BCrypt)',
    `real_name`   VARCHAR(64)  DEFAULT NULL COMMENT '真实姓名',
    `role`        VARCHAR(32)  NOT NULL DEFAULT 'admin' COMMENT '角色: super_admin/admin/teacher/student/parent',
    `email`       VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `avatar`      VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户';

CREATE TABLE `sys_role` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        VARCHAR(64) NOT NULL COMMENT '角色名称',
    `code`        VARCHAR(64) NOT NULL COMMENT '角色编码',
    `description` VARCHAR(256) DEFAULT NULL COMMENT '角色描述',
    `status`      TINYINT     NOT NULL DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色';

CREATE TABLE `sys_permission` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        VARCHAR(64)  NOT NULL COMMENT '权限名称',
    `perms`       VARCHAR(128) DEFAULT NULL COMMENT '权限标识(system:user:add)',
    `path`        VARCHAR(256) DEFAULT NULL COMMENT '路由路径',
    `icon`        VARCHAR(64)  DEFAULT NULL COMMENT '图标',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序号',
    `parent_id`   BIGINT       DEFAULT NULL COMMENT '父级ID',
    `type`        TINYINT      NOT NULL DEFAULT 1 COMMENT '类型: 1菜单 2按钮',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统权限';

CREATE TABLE `sys_role_permission` (
    `id`            BIGINT  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `role_id`       BIGINT  NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT  NOT NULL COMMENT '权限ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_perm` (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联';

-- ============================================================
-- 2. 基础组织
-- ============================================================

CREATE TABLE `sys_department` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        VARCHAR(128) NOT NULL COMMENT '院系名称',
    `code`        VARCHAR(64)  NOT NULL COMMENT '院系编码',
    `leader`      VARCHAR(64)  DEFAULT NULL COMMENT '负责人',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序号',
    `description` VARCHAR(512) DEFAULT NULL COMMENT '描述',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='院系';

CREATE TABLE `sys_major` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`          VARCHAR(128) NOT NULL COMMENT '专业名称',
    `code`          VARCHAR(64)  NOT NULL COMMENT '专业编码',
    `department_id` BIGINT       NOT NULL COMMENT '所属院系ID',
    `level`         VARCHAR(16)  NOT NULL COMMENT '层次: 本科/专科/硕士/博士',
    `years`         TINYINT      NOT NULL DEFAULT 4 COMMENT '学制(年)',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    `sort`          INT          NOT NULL DEFAULT 0 COMMENT '排序号',
    `description`   VARCHAR(512) DEFAULT NULL COMMENT '描述',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_department_id` (`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专业';

CREATE TABLE `sys_class` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`          VARCHAR(128) NOT NULL COMMENT '班级名称',
    `code`          VARCHAR(64)  NOT NULL COMMENT '班级编码',
    `department_id` BIGINT       NOT NULL COMMENT '所属院系ID',
    `major_id`      BIGINT       DEFAULT NULL COMMENT '所属专业ID',
    `year`          INT          NOT NULL COMMENT '入学年份',
    `student_count` INT          NOT NULL DEFAULT 0 COMMENT '学生人数',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    `sort`          INT          NOT NULL DEFAULT 0 COMMENT '排序号',
    `description`   VARCHAR(512) DEFAULT NULL COMMENT '描述',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_department_id` (`department_id`),
    KEY `idx_major_id` (`major_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级';

-- ============================================================
-- 3. 师生管理
-- ============================================================

CREATE TABLE `sys_student` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `student_no`    VARCHAR(32)  NOT NULL COMMENT '学号',
    `name`          VARCHAR(64)  NOT NULL COMMENT '姓名',
    `gender`        VARCHAR(4)   DEFAULT NULL COMMENT '性别',
    `department_id` BIGINT       DEFAULT NULL COMMENT '所属院系ID',
    `major_id`      BIGINT       DEFAULT NULL COMMENT '所属专业ID',
    `class_id`      BIGINT       DEFAULT NULL COMMENT '所属班级ID',
    `phone`         VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `email`         VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `password`      VARCHAR(256) DEFAULT '123456' COMMENT '密码',
    `status`        VARCHAR(16)  NOT NULL DEFAULT '在读' COMMENT '状态: 在读/休学/毕业/退学',
    `address`       VARCHAR(256) DEFAULT NULL COMMENT '住址',
    `avatar`        VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_no` (`student_no`),
    KEY `idx_department_id` (`department_id`),
    KEY `idx_major_id` (`major_id`),
    KEY `idx_class_id` (`class_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生';

CREATE TABLE `sys_teacher` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `teacher_no`    VARCHAR(32)  NOT NULL COMMENT '工号',
    `name`          VARCHAR(64)  NOT NULL COMMENT '姓名',
    `gender`        VARCHAR(4)   DEFAULT NULL COMMENT '性别',
    `department_id` BIGINT       DEFAULT NULL COMMENT '所属院系ID',
    `title`         VARCHAR(32)  DEFAULT NULL COMMENT '职称: 教授/副教授/讲师/助教',
    `degree`        VARCHAR(16)  DEFAULT NULL COMMENT '学历: 博士/硕士/本科',
    `phone`         VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `email`         VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `password`      VARCHAR(256) DEFAULT '123456' COMMENT '密码',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1在职 0离职',
    `intro`         TEXT         DEFAULT NULL COMMENT '个人简介',
    `avatar`        VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_teacher_no` (`teacher_no`),
    KEY `idx_department_id` (`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师';

-- ============================================================
-- 4. 教学业务
-- ============================================================

CREATE TABLE `biz_course` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`          VARCHAR(128) NOT NULL COMMENT '课程名称',
    `code`          VARCHAR(64)  NOT NULL COMMENT '课程编码',
    `department_id` BIGINT       DEFAULT NULL COMMENT '所属院系ID',
    `teacher_id`    BIGINT       DEFAULT NULL COMMENT '授课教师ID',
    `teacher_name`  VARCHAR(64)  DEFAULT NULL COMMENT '授课教师姓名',
    `type`          VARCHAR(16)  NOT NULL DEFAULT '必修' COMMENT '类型: 必修/选修/公共',
    `credit`        INT          NOT NULL DEFAULT 3 COMMENT '学分',
    `hours`         INT          NOT NULL DEFAULT 48 COMMENT '课时',
    `location`      VARCHAR(128) DEFAULT NULL COMMENT '上课地点',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1已开课 0未开课',
    `description`   TEXT         DEFAULT NULL COMMENT '课程描述',
    `cover`         VARCHAR(512) DEFAULT NULL COMMENT '封面图URL',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_department_id` (`department_id`),
    KEY `idx_teacher_id` (`teacher_id`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程';

CREATE TABLE `biz_exercise` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `course_id`   BIGINT       DEFAULT NULL COMMENT '所属课程ID',
    `type`        VARCHAR(16)  NOT NULL COMMENT '题型: 单选题/多选题/判断题/填空题/简答题',
    `difficulty`  VARCHAR(8)   NOT NULL DEFAULT '中等' COMMENT '难度: 简单/中等/困难',
    `question`    TEXT         NOT NULL COMMENT '题目内容',
    `option_a`    VARCHAR(512) DEFAULT NULL COMMENT '选项A',
    `option_b`    VARCHAR(512) DEFAULT NULL COMMENT '选项B',
    `option_c`    VARCHAR(512) DEFAULT NULL COMMENT '选项C',
    `option_d`    VARCHAR(512) DEFAULT NULL COMMENT '选项D',
    `answer`      TEXT         DEFAULT NULL COMMENT '参考答案',
    `analysis`    TEXT         DEFAULT NULL COMMENT '解析',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_type` (`type`),
    KEY `idx_difficulty` (`difficulty`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='习题';

CREATE TABLE `biz_paper` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`         VARCHAR(128) NOT NULL COMMENT '试卷名称',
    `course_id`    BIGINT       DEFAULT NULL COMMENT '所属课程ID',
    `total_score`  INT          NOT NULL DEFAULT 100 COMMENT '总分',
    `pass_score`   INT          NOT NULL DEFAULT 60 COMMENT '及格分',
    `duration`     INT          NOT NULL DEFAULT 120 COMMENT '考试时长(分钟)',
    `single_count` INT          NOT NULL DEFAULT 0 COMMENT '单选题数量',
    `single_score` INT          NOT NULL DEFAULT 0 COMMENT '单选题分值',
    `multi_count`  INT          NOT NULL DEFAULT 0 COMMENT '多选题数量',
    `multi_score`  INT          NOT NULL DEFAULT 0 COMMENT '多选题分值',
    `judge_count`  INT          NOT NULL DEFAULT 0 COMMENT '判断题数量',
    `judge_score`  INT          NOT NULL DEFAULT 0 COMMENT '判断题分值',
    `question_count` INT        NOT NULL DEFAULT 0 COMMENT '题目总数',
    `status`       VARCHAR(16)  NOT NULL DEFAULT '草稿' COMMENT '状态: 草稿/已发布',
    `description`  TEXT         DEFAULT NULL COMMENT '描述',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷';

CREATE TABLE `biz_exam` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`             VARCHAR(128) NOT NULL COMMENT '考试名称',
    `course_id`        BIGINT       DEFAULT NULL COMMENT '课程ID',
    `paper_id`         BIGINT       DEFAULT NULL COMMENT '试卷ID',
    `exam_date`        DATE         DEFAULT NULL COMMENT '考试日期',
    `start_time`       VARCHAR(16)  DEFAULT NULL COMMENT '开始时间(HH:mm)',
    `duration`         INT          NOT NULL DEFAULT 120 COMMENT '时长(分钟)',
    `location`         VARCHAR(128) DEFAULT NULL COMMENT '考试地点',
    `invigilator`      VARCHAR(64)  DEFAULT NULL COMMENT '监考人',
    `total_students`   INT          NOT NULL DEFAULT 0 COMMENT '应考人数',
    `attended_students` INT         NOT NULL DEFAULT 0 COMMENT '实考人数',
    `status`           VARCHAR(16)  NOT NULL DEFAULT '待开始' COMMENT '状态: 待开始/进行中/已结束',
    `remark`           TEXT         DEFAULT NULL COMMENT '备注',
    `create_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_paper_id` (`paper_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试';

CREATE TABLE `biz_exam_student` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `exam_id`    BIGINT       NOT NULL COMMENT '考试ID',
    `student_id` BIGINT       NOT NULL COMMENT '学生ID',
    `score`      INT          DEFAULT NULL COMMENT '成绩',
    `status`     VARCHAR(16)  NOT NULL DEFAULT '缺考' COMMENT '状态: 正常/缺考/作弊',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_exam_student` (`exam_id`, `student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试学生成绩';

CREATE TABLE `biz_resource` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        VARCHAR(256) NOT NULL COMMENT '资源名称',
    `type`        VARCHAR(16)  NOT NULL COMMENT '类型: 视频/文档/图片/音频/其他',
    `category`    VARCHAR(32)  DEFAULT NULL COMMENT '分类: 课程资料/课件/习题/参考书',
    `file_size`   BIGINT       NOT NULL DEFAULT 0 COMMENT '文件大小(bytes)',
    `file_path`   VARCHAR(512) DEFAULT NULL COMMENT '文件存储路径',
    `uploader`    VARCHAR(64)  DEFAULT NULL COMMENT '上传者',
    `downloads`   INT          NOT NULL DEFAULT 0 COMMENT '下载次数',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1已发布 0草稿',
    `description` TEXT         DEFAULT NULL COMMENT '描述',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源';

CREATE TABLE `biz_announcement` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title`       VARCHAR(256) NOT NULL COMMENT '标题',
    `content`     TEXT         DEFAULT NULL COMMENT '内容',
    `publisher`   VARCHAR(64)  DEFAULT NULL COMMENT '发布人',
    `level`       VARCHAR(8)   NOT NULL COMMENT '级别: 紧急/重要/普通',
    `status`      VARCHAR(16)  NOT NULL DEFAULT '草稿' COMMENT '状态: 已发布/草稿',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告';

CREATE TABLE `biz_student_course` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `student_id`  BIGINT       NOT NULL COMMENT '学生ID',
    `course_id`   BIGINT       NOT NULL COMMENT '课程ID',
    `score`       INT          DEFAULT NULL COMMENT '成绩',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_course` (`student_id`, `course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生选课';

-- ============================================================
-- 5. AI 与智能
-- ============================================================

CREATE TABLE `ai_conversation` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     BIGINT       NOT NULL COMMENT '用户ID',
    `user_role`   VARCHAR(32)  NOT NULL COMMENT '用户角色',
    `title`       VARCHAR(256) DEFAULT NULL COMMENT '对话标题',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 对话记录';

CREATE TABLE `ai_message` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `conversation_id` BIGINT       NOT NULL COMMENT '对话ID',
    `role`            VARCHAR(16)  NOT NULL COMMENT '角色: user/assistant/system',
    `content`         TEXT         NOT NULL COMMENT '消息内容',
    `tokens`          INT          DEFAULT NULL COMMENT 'Token 数',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_conversation_id` (`conversation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 对话消息';

CREATE TABLE `ai_knowledge_doc` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title`       VARCHAR(256) NOT NULL COMMENT '文档标题',
    `content`     TEXT         NOT NULL COMMENT '文档内容',
    `category`    VARCHAR(64)  DEFAULT NULL COMMENT '分类',
    `tags`        VARCHAR(512) DEFAULT NULL COMMENT '标签(逗号分隔)',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    `uploader`    VARCHAR(64)  DEFAULT NULL COMMENT '上传者',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 知识库文档';

CREATE TABLE `ai_student_profile` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `student_id`          BIGINT       NOT NULL COMMENT '学生ID',
    `student_no`          VARCHAR(32)  NOT NULL COMMENT '学号',
    `student_name`        VARCHAR(64)  NOT NULL COMMENT '学生姓名',
    `class_id`            BIGINT       DEFAULT NULL COMMENT '班级ID',
    `major_id`            BIGINT       DEFAULT NULL COMMENT '专业ID',
    `department_id`       BIGINT       DEFAULT NULL COMMENT '院系ID',
    `gpa`                 DECIMAL(4,2) DEFAULT NULL COMMENT 'GPA',
    `attendance_rate`     DECIMAL(5,2) DEFAULT NULL COMMENT '出勤率(%)',
    `homework_avg`        DECIMAL(5,2) DEFAULT NULL COMMENT '作业平均分',
    `exam_avg`            DECIMAL(5,2) DEFAULT NULL COMMENT '考试平均分',
    `comprehensive_score` DECIMAL(5,2) DEFAULT NULL COMMENT '综合评分',
    `risk_level`          VARCHAR(8)   DEFAULT 'green' COMMENT '风险等级: red/yellow/green',
    `trend`               VARCHAR(16)  DEFAULT 'stable' COMMENT '趋势: up/down/stable',
    `last_calc_time`      DATETIME     DEFAULT NULL COMMENT '最近计算时间',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_id` (`student_id`),
    KEY `idx_risk_level` (`risk_level`),
    KEY `idx_department_id` (`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生学业画像';

CREATE TABLE `ai_early_warning` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `student_id`   BIGINT       NOT NULL COMMENT '学生ID',
    `profile_id`   BIGINT       NOT NULL COMMENT '画像ID',
    `warning_type` VARCHAR(32)  NOT NULL COMMENT '预警类型: attendance/homework/exam/comprehensive',
    `level`        VARCHAR(8)   NOT NULL COMMENT '等级: red/yellow',
    `score`        DECIMAL(5,2) DEFAULT NULL COMMENT '触发分值',
    `threshold`    DECIMAL(5,2) DEFAULT NULL COMMENT '阈值',
    `description`  VARCHAR(512) DEFAULT NULL COMMENT '描述',
    `suggestion`   VARCHAR(512) DEFAULT NULL COMMENT 'AI 干预建议',
    `status`       VARCHAR(16)  NOT NULL DEFAULT 'pending' COMMENT '状态: pending/resolved/ignored',
    `resolver`     VARCHAR(64)  DEFAULT NULL COMMENT '处理人',
    `resolve_time` DATETIME     DEFAULT NULL COMMENT '处理时间',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_level` (`level`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学业预警记录';

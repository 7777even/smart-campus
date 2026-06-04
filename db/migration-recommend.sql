-- ============================================================
-- 个性化推荐系统 — 数据库迁移
-- ============================================================
USE `smart_campus`;

-- 1. sys_user 表增加 student_id 字段，建立与 sys_student 的关联
ALTER TABLE `sys_user` ADD COLUMN `student_id` BIGINT DEFAULT NULL COMMENT '关联学生ID' AFTER `status`;

-- 2. 更新 demo 数据的映射关系 (student1 -> 王小明)
UPDATE `sys_user` SET `student_id` = 1 WHERE `username` = 'student1';

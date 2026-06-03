package com.smart.campus.admin.service;

import com.smart.campus.admin.entity.*;
import com.smart.campus.admin.mappers.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

import com.campus.entity.PageResult;

/**
 * 学生学业画像服务
 * <p>
 * 基于学生成绩、出勤等多维度数据，计算综合学业画像并评估风险等级
 */
@Service
public class AiProfileService {

    private final AiStudentProfileMapper profileMapper;
    private final StudentMapper studentMapper;
    private final AiEarlyWarningMapper warningMapper;

    public AiProfileService(AiStudentProfileMapper profileMapper,
                            StudentMapper studentMapper,
                            AiEarlyWarningMapper warningMapper) {
        this.profileMapper = profileMapper;
        this.studentMapper = studentMapper;
        this.warningMapper = warningMapper;
    }

    /**
     * 分页查询学业画像
     */
    public PageResult<AiStudentProfile> page(int pageNo, int pageSize, Map<String, Object> params) {
        params.put("pageNo", (pageNo - 1) * pageSize);
        params.put("pageSize", pageSize);
        long total = profileMapper.selectCount(params);
        List<AiStudentProfile> list = profileMapper.selectList(params);
        return new PageResult<>(total, pageSize, pageNo, list);
    }

    /**
     * 获取单个画像
     */
    public AiStudentProfile getById(Long id) {
        return profileMapper.selectById(id);
    }

    /**
     * 计算单个学生的学业画像
     */
    @Transactional
    public AiStudentProfile calculateProfile(Long studentId) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在: " + studentId);
        }

        // 1. 计算各维度得分
        BigDecimal examAvg = calculateExamAvg(studentId);
        BigDecimal homeworkAvg = calculateHomeworkAvg(studentId);
        BigDecimal attendanceRate = simulateAttendanceRate(studentId);

        // 2. 计算综合评分
        BigDecimal comprehensiveScore = calculateComprehensive(examAvg, homeworkAvg, attendanceRate);

        // 3. 确定风险等级
        String riskLevel = determineRiskLevel(comprehensiveScore);
        String trend = "stable";

        // 4. 计算 GPA（简化：考试平均分/100 * 4.0）
        BigDecimal gpa = examAvg != null
                ? examAvg.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(4.0))
                : null;

        // 5. 查找或创建画像
        AiStudentProfile profile = profileMapper.selectByStudentId(studentId);
        boolean isNew = (profile == null);
        if (isNew) {
            profile = new AiStudentProfile();
        }

        profile.setStudentId(student.getId());
        profile.setStudentNo(student.getStudentNo());
        profile.setStudentName(student.getName());
        profile.setClassId(student.getClassId());
        profile.setMajorId(student.getMajorId());
        profile.setDepartmentId(student.getDepartmentId());
        profile.setGpa(gpa);
        profile.setAttendanceRate(attendanceRate);
        profile.setHomeworkAvg(homeworkAvg);
        profile.setExamAvg(examAvg);
        profile.setComprehensiveScore(comprehensiveScore);
        profile.setRiskLevel(riskLevel);
        profile.setTrend(trend);
        profile.setLastCalcTime(LocalDateTime.now());

        if (isNew) {
            profileMapper.insert(profile);
        } else {
            profileMapper.update(profile);
        }

        return profile;
    }

    /**
     * 计算所有学生的学业画像
     */
    @Transactional
    public int calculateAllProfiles() {
        // 获取所有在读学生
        Map<String, Object> params = new HashMap<>();
        params.put("pageNo", 0);
        params.put("pageSize", 10000);
        // 筛选状态为"在读"的学生
        params.put("status", "在读");
        List<Student> students = studentMapper.selectList(params);

        int count = 0;
        for (Student student : students) {
            try {
                calculateProfile(student.getId());
                count++;
            } catch (Exception e) {
                // 单个学生计算失败不影响其他学生
            }
        }
        return count;
    }

    /**
     * 查询画像统计信息
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 各等级人数
        for (String level : new String[]{"green", "yellow", "red"}) {
            Map<String, Object> params = new HashMap<>();
            params.put("riskLevel", level);
            params.put("pageNo", 0);
            params.put("pageSize", 1);
            stats.put(level + "Count", profileMapper.selectCount(params));
        }

        // 总人数
        Map<String, Object> allParams = new HashMap<>();
        allParams.put("pageNo", 0);
        allParams.put("pageSize", 1);
        stats.put("totalCount", profileMapper.selectCount(allParams));

        return stats;
    }

    /**
     * 计算考试平均分（从 biz_exam_student 和 biz_student_course 获取）
     */
    private BigDecimal calculateExamAvg(Long studentId) {
        // 使用模拟数据：基于学生 ID 生成差异化的成绩
        // 在实际生产环境中，此处应查询 biz_exam_student 表
        double baseScore = 65 + (studentId % 5) * 7 + Math.random() * 10;
        return BigDecimal.valueOf(Math.min(100, Math.max(30, baseScore)))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算作业平均分（从 biz_student_course 获取）
     */
    private BigDecimal calculateHomeworkAvg(Long studentId) {
        // 使用模拟数据
        double baseScore = 60 + (studentId % 4) * 8 + Math.random() * 15;
        return BigDecimal.valueOf(Math.min(100, Math.max(20, baseScore)))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 模拟出勤率
     */
    private BigDecimal simulateAttendanceRate(Long studentId) {
        // 使用模拟数据
        double rate = 0.75 + (studentId % 3) * 0.05 + Math.random() * 0.1;
        return BigDecimal.valueOf(Math.min(1.0, Math.max(0.3, rate)))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算综合评分
     * 公式：examAvg * 0.4 + homeworkAvg * 0.3 + attendanceRate * 100 * 0.3
     */
    private BigDecimal calculateComprehensive(BigDecimal examAvg, BigDecimal homeworkAvg, BigDecimal attendanceRate) {
        double examWeight = 0.4;
        double homeworkWeight = 0.3;
        double attendanceWeight = 0.3;

        double examVal = examAvg != null ? examAvg.doubleValue() : 60;
        double homeworkVal = homeworkAvg != null ? homeworkAvg.doubleValue() : 60;
        double attendanceVal = attendanceRate != null ? attendanceRate.doubleValue() * 100 : 80;

        double result = examVal * examWeight + homeworkVal * homeworkWeight + attendanceVal * attendanceWeight;
        return BigDecimal.valueOf(result).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 确定风险等级
     */
    private String determineRiskLevel(BigDecimal score) {
        if (score == null) return "green";
        double val = score.doubleValue();
        if (val < 60) return "red";
        if (val < 75) return "yellow";
        return "green";
    }
}

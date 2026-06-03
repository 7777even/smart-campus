package com.smart.campus.admin.service;

import com.smart.campus.admin.entity.AiEarlyWarning;
import com.smart.campus.admin.entity.AiStudentProfile;
import com.smart.campus.admin.mappers.AiEarlyWarningMapper;
import com.smart.campus.admin.mappers.AiStudentProfileMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import com.campus.entity.PageResult;

/**
 * 学业预警引擎服务
 * <p>
 * 基于规则引擎的学业预警：根据学生画像数据与配置阈值对比，
 * 自动生成红/黄二级预警，并提供 AI 干预建议。
 */
@Service
public class AiWarningService {

    private final AiStudentProfileMapper profileMapper;
    private final AiEarlyWarningMapper warningMapper;

    @Value("${ai.warning.attendance-threshold:0.7}")
    private double attendanceThreshold;

    @Value("${ai.warning.homework-threshold:60}")
    private double homeworkThreshold;

    @Value("${ai.warning.exam-threshold:60}")
    private double examThreshold;

    @Value("${ai.warning.red-attendance-threshold:0.5}")
    private double redAttendanceThreshold;

    @Value("${ai.warning.red-homework-threshold:40}")
    private double redHomeworkThreshold;

    @Value("${ai.warning.red-exam-threshold:40}")
    private double redExamThreshold;

    public AiWarningService(AiStudentProfileMapper profileMapper,
                            AiEarlyWarningMapper warningMapper) {
        this.profileMapper = profileMapper;
        this.warningMapper = warningMapper;
    }

    /**
     * 分页查询预警
     */
    public PageResult<AiEarlyWarning> page(int pageNo, int pageSize, Map<String, Object> params) {
        params.put("pageNo", (pageNo - 1) * pageSize);
        params.put("pageSize", pageSize);
        long total = warningMapper.selectCount(params);
        List<AiEarlyWarning> list = warningMapper.selectList(params);
        return new PageResult<>(total, pageSize, pageNo, list);
    }

    /**
     * 获取单个预警
     */
    public AiEarlyWarning getById(Long id) {
        return warningMapper.selectById(id);
    }

    /**
     * 对单个学生画像执行评估，生成预警
     */
    @Transactional
    public List<AiEarlyWarning> evaluateProfile(Long profileId) {
        AiStudentProfile profile = profileMapper.selectById(profileId);
        if (profile == null) {
            throw new RuntimeException("画像不存在: " + profileId);
        }

        List<AiEarlyWarning> warnings = new ArrayList<>();

        // 1. 出勤率评估
        if (profile.getAttendanceRate() != null) {
            double rate = profile.getAttendanceRate().doubleValue();
            if (rate < redAttendanceThreshold) {
                warnings.add(createWarning(profile, "attendance", "red",
                        profile.getAttendanceRate(), BigDecimal.valueOf(redAttendanceThreshold),
                        "出勤率严重偏低（" + String.format("%.1f", rate * 100) + "%），存在挂科风险"));
            } else if (rate < attendanceThreshold) {
                warnings.add(createWarning(profile, "attendance", "yellow",
                        profile.getAttendanceRate(), BigDecimal.valueOf(attendanceThreshold),
                        "出勤率偏低（" + String.format("%.1f", rate * 100) + "%），需关注"));
            }
        }

        // 2. 作业成绩评估
        if (profile.getHomeworkAvg() != null) {
            double avg = profile.getHomeworkAvg().doubleValue();
            if (avg < redHomeworkThreshold) {
                warnings.add(createWarning(profile, "homework", "red",
                        profile.getHomeworkAvg(), BigDecimal.valueOf(redHomeworkThreshold),
                        "作业平均分严重偏低（" + String.format("%.1f", avg) + "分）"));
            } else if (avg < homeworkThreshold) {
                warnings.add(createWarning(profile, "homework", "yellow",
                        profile.getHomeworkAvg(), BigDecimal.valueOf(homeworkThreshold),
                        "作业平均分偏低（" + String.format("%.1f", avg) + "分），需加强练习"));
            }
        }

        // 3. 考试成绩评估
        if (profile.getExamAvg() != null) {
            double avg = profile.getExamAvg().doubleValue();
            if (avg < redExamThreshold) {
                warnings.add(createWarning(profile, "exam", "red",
                        profile.getExamAvg(), BigDecimal.valueOf(redExamThreshold),
                        "考试平均分严重偏低（" + String.format("%.1f", avg) + "分）"));
            } else if (avg < examThreshold) {
                warnings.add(createWarning(profile, "exam", "yellow",
                        profile.getExamAvg(), BigDecimal.valueOf(examThreshold),
                        "考试平均分偏低（" + String.format("%.1f", avg) + "分），需重点关注"));
            }
        }

        // 4. 综合评分评估
        if (profile.getComprehensiveScore() != null) {
            double score = profile.getComprehensiveScore().doubleValue();
            if (score < 60) {
                warnings.add(createWarning(profile, "comprehensive", "red",
                        profile.getComprehensiveScore(), BigDecimal.valueOf(60),
                        "综合评分偏低（" + String.format("%.1f", score) + "分），学业状态需紧急干预"));
            } else if (score < 75) {
                warnings.add(createWarning(profile, "comprehensive", "yellow",
                        profile.getComprehensiveScore(), BigDecimal.valueOf(75),
                        "综合评分处于中等偏下水平（" + String.format("%.1f", score) + "分），建议制定提升计划"));
            }
        }

        // 5. 批量保存预警
        for (AiEarlyWarning warning : warnings) {
            warningMapper.insert(warning);
        }

        // 6. 更新画像风险等级
        if (profile.getComprehensiveScore() != null) {
            double score = profile.getComprehensiveScore().doubleValue();
            String level = score < 60 ? "red" : (score < 75 ? "yellow" : "green");
            profile.setRiskLevel(level);
            profileMapper.update(profile);
        }

        return warnings;
    }

    /**
     * 对所有学生画像执行评估
     */
    @Transactional
    public Map<String, Object> evaluateAll() {
        // 获取所有画像
        Map<String, Object> params = new HashMap<>();
        params.put("pageNo", 0);
        params.put("pageSize", 10000);
        List<AiStudentProfile> profiles = profileMapper.selectList(params);

        int totalWarnings = 0;
        int redCount = 0;
        int yellowCount = 0;

        for (AiStudentProfile profile : profiles) {
            try {
                List<AiEarlyWarning> warnings = evaluateProfile(profile.getId());
                totalWarnings += warnings.size();
                for (AiEarlyWarning w : warnings) {
                    if ("red".equals(w.getLevel())) redCount++;
                    else if ("yellow".equals(w.getLevel())) yellowCount++;
                }
            } catch (Exception e) {
                // 单个评估失败不影响整体
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalProfiles", profiles.size());
        result.put("totalWarnings", totalWarnings);
        result.put("redCount", redCount);
        result.put("yellowCount", yellowCount);
        return result;
    }

    /**
     * 处理预警（标记为已解决）
     */
    @Transactional
    public AiEarlyWarning resolveWarning(Long id, String resolver) {
        AiEarlyWarning warning = warningMapper.selectById(id);
        if (warning == null) {
            throw new RuntimeException("预警记录不存在: " + id);
        }
        warning.setStatus("resolved");
        warning.setResolver(resolver);
        warning.setResolveTime(LocalDateTime.now());
        warningMapper.update(warning);
        return warning;
    }

    /**
     * 获取预警统计
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 各状态数量
        for (String status : new String[]{"pending", "resolved", "ignored"}) {
            Map<String, Object> params = new HashMap<>();
            params.put("status", status);
            params.put("pageNo", 0);
            params.put("pageSize", 1);
            stats.put(status + "Count", warningMapper.selectCount(params));
        }

        // 各等级数量
        for (String level : new String[]{"red", "yellow"}) {
            Map<String, Object> params = new HashMap<>();
            params.put("level", level);
            params.put("pageNo", 0);
            params.put("pageSize", 1);
            stats.put(level + "Count", warningMapper.selectCount(params));
        }

        return stats;
    }

    /**
     * 创建预警记录
     */
    private AiEarlyWarning createWarning(AiStudentProfile profile, String type, String level,
                                          BigDecimal score, BigDecimal threshold, String description) {
        AiEarlyWarning warning = new AiEarlyWarning();
        warning.setStudentId(profile.getStudentId());
        warning.setProfileId(profile.getId());
        warning.setWarningType(type);
        warning.setLevel(level);
        warning.setScore(score);
        warning.setThreshold(threshold);
        warning.setDescription(description);
        warning.setStatus("pending");

        // 生成干预建议
        warning.setSuggestion(generateSuggestion(type, level, profile));
        return warning;
    }

    /**
     * 生成 AI 干预建议
     */
    private String generateSuggestion(String type, String level, AiStudentProfile profile) {
        String name = profile.getStudentName();
        switch (type) {
            case "attendance":
                return "建议辅导员与" + name + "谈话，了解缺勤原因，提醒按时出勤的重要性。";
            case "homework":
                return "建议任课教师关注" + name + "的作业完成情况，提供额外辅导。";
            case "exam":
                return "建议" + name + "制定复习计划，参加答疑课，" +
                        (level.equals("red") ? "并联系教学秘书安排一对一学业辅导。" : "必要时可申请学业帮扶。");
            case "comprehensive":
                return "建议学院教学秘书召集" + name + "及其辅导员、任课教师，制定综合学业提升方案。" +
                        (level.equals("red") ? "同时通知家长知情。" : "");
            default:
                return "建议关注" + name + "的学业状态，适时提供帮助。";
        }
    }
}

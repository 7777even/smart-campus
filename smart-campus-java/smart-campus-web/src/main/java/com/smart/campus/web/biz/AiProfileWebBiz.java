package com.smart.campus.web.biz;

import com.campus.entity.AiEarlyWarning;
import com.campus.entity.AiStudentProfile;
import com.smart.campus.web.mappers.WebAiEarlyWarningMapper;
import com.smart.campus.web.mappers.WebAiStudentProfileMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * AI 学业画像业务（学生端）
 */
@Component
public class AiProfileWebBiz {

    private final WebAiStudentProfileMapper profileMapper;
    private final WebAiEarlyWarningMapper warningMapper;

    public AiProfileWebBiz(WebAiStudentProfileMapper profileMapper,
                           WebAiEarlyWarningMapper warningMapper) {
        this.profileMapper = profileMapper;
        this.warningMapper = warningMapper;
    }

    /**
     * 获取学生学业画像（无则返回默认值）
     */
    public Map<String, Object> getProfile(Long studentId) {
        AiStudentProfile profile = profileMapper.selectByStudentId(studentId);
        Map<String, Object> result = new HashMap<>();

        if (profile == null) {
            result.put("studentId", studentId);
            result.put("studentNo", "");
            result.put("studentName", "");
            result.put("gpa", 0.0);
            result.put("attendanceRate", 0.0);
            result.put("homeworkAvg", 0.0);
            result.put("examAvg", 0.0);
            result.put("comprehensiveScore", 0.0);
            result.put("riskLevel", "green");
            result.put("trend", "stable");
            result.put("lastCalcTime", null);
        } else {
            result.put("studentId", profile.getStudentId());
            result.put("studentNo", profile.getStudentNo());
            result.put("studentName", profile.getStudentName());
            result.put("gpa", profile.getGpa() != null ? profile.getGpa().doubleValue() : 0.0);
            result.put("attendanceRate", profile.getAttendanceRate() != null ? profile.getAttendanceRate().doubleValue() : 0.0);
            result.put("homeworkAvg", profile.getHomeworkAvg() != null ? profile.getHomeworkAvg().doubleValue() : 0.0);
            result.put("examAvg", profile.getExamAvg() != null ? profile.getExamAvg().doubleValue() : 0.0);
            result.put("comprehensiveScore", profile.getComprehensiveScore() != null ? profile.getComprehensiveScore().doubleValue() : 0.0);
            result.put("riskLevel", profile.getRiskLevel());
            result.put("trend", profile.getTrend());
            result.put("lastCalcTime", profile.getLastCalcTime());
        }
        return result;
    }

    /**
     * 获取学业预警列表
     */
    public List<Map<String, Object>> getWarnings(Long studentId) {
        List<AiEarlyWarning> warnings = warningMapper.selectByStudentId(studentId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (AiEarlyWarning w : warnings) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", String.valueOf(w.getId()));
            m.put("studentId", w.getStudentId());
            m.put("profileId", w.getProfileId());
            m.put("warningType", w.getWarningType());
            m.put("level", w.getLevel());
            m.put("score", w.getScore() != null ? w.getScore().doubleValue() : 0.0);
            m.put("description", w.getDescription());
            m.put("suggestion", w.getSuggestion());
            m.put("status", w.getStatus());
            result.add(m);
        }
        return result;
    }
}

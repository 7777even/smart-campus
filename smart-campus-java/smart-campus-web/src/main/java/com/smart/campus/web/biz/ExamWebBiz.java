package com.smart.campus.web.biz;

import com.campus.entity.Course;
import com.campus.entity.Exam;
import com.campus.entity.Exercise;
import com.campus.entity.StudentCourse;
import com.campus.mappers.CourseMapper;
import com.campus.mappers.ExamMapper;
import com.campus.mappers.StudentCourseMapper;
import com.smart.campus.web.entity.ExamRecord;
import com.smart.campus.web.mappers.ExamRecordMapper;
import com.smart.campus.web.mappers.WebExamMapper;
import com.smart.campus.web.mappers.WebExerciseMapper;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 考试业务（学生端）
 */
@Component
public class ExamWebBiz {

    private final WebExamMapper examMapper;
    private final WebExerciseMapper exerciseMapper;
    private final StudentCourseMapper studentCourseMapper;
    private final ExamRecordMapper examRecordMapper;
    private final ExamMapper examMapperCommon;
    private final CourseMapper courseMapper;

    public ExamWebBiz(WebExamMapper examMapper,
                      WebExerciseMapper exerciseMapper,
                      StudentCourseMapper studentCourseMapper,
                      ExamRecordMapper examRecordMapper,
                      ExamMapper examMapperCommon,
                      CourseMapper courseMapper) {
        this.examMapper = examMapper;
        this.exerciseMapper = exerciseMapper;
        this.studentCourseMapper = studentCourseMapper;
        this.examRecordMapper = examRecordMapper;
        this.examMapperCommon = examMapperCommon;
        this.courseMapper = courseMapper;
    }

    /**
     * 我的考试列表（学生已选课程的考试）
     */
    public List<Map<String, Object>> getMyExams(Long studentId) {
        List<Long> courseIds = studentCourseMapper.selectCourseIdsByStudentId(studentId);
        if (courseIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Exam> allExams = new ArrayList<>();
        for (Long courseId : courseIds) {
            List<Exam> exams = examMapper.selectByCourseId(String.valueOf(courseId));
            allExams.addAll(exams);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Exam exam : allExams) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", String.valueOf(exam.getId()));
            m.put("name", exam.getName());
            m.put("courseId", exam.getCourseId());
            m.put("examDate", exam.getExamDate());
            m.put("startTime", exam.getStartTime());
            m.put("duration", exam.getDuration());
            m.put("location", exam.getLocation());
            m.put("invigilator", exam.getInvigilator());
            m.put("status", exam.getStatus());
            result.add(m);
        }
        return result;
    }

    /**
     * 考试详情
     */
    public Map<String, Object> getDetail(String id) {
        Exam exam = examMapper.selectById(Long.valueOf(id));
        if (exam == null) {
            throw new RuntimeException("考试不存在");
        }
        Map<String, Object> m = new HashMap<>();
        m.put("id", String.valueOf(exam.getId()));
        m.put("name", exam.getName());
        m.put("courseId", exam.getCourseId());
        m.put("examDate", exam.getExamDate());
        m.put("startTime", exam.getStartTime());
        m.put("duration", exam.getDuration());
        m.put("location", exam.getLocation());
        m.put("invigilator", exam.getInvigilator());
        m.put("status", exam.getStatus());
        return m;
    }

    /**
     * 开始考试
     */
    public Map<String, Object> startExam(Long studentId, String examId) {
        Exam exam = examMapper.selectById(Long.valueOf(examId));
        if (exam == null) {
            throw new RuntimeException("考试不存在");
        }

        if (!checkEnrolled(String.valueOf(exam.getCourseId()), studentId)) {
            throw new RuntimeException("请先选修该课程");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("examId", String.valueOf(exam.getId()));
        result.put("examName", exam.getName());
        result.put("examDate", exam.getExamDate());
        result.put("startTime", exam.getStartTime());
        result.put("duration", exam.getDuration());
        result.put("location", exam.getLocation());
        result.put("invigilator", exam.getInvigilator());

        // 获取该课程的所有习题作为考题
        List<Exercise> exercises = exerciseMapper.selectByCourseId(String.valueOf(exam.getCourseId()));
        List<Map<String, Object>> questions = new ArrayList<>();
        for (Exercise ex : exercises) {
            Map<String, Object> q = new HashMap<>();
            q.put("exerciseId", String.valueOf(ex.getId()));
            q.put("type", ex.getType());
            q.put("difficulty", ex.getDifficulty());
            q.put("question", ex.getQuestion());
            q.put("optionA", ex.getOptionA());
            q.put("optionB", ex.getOptionB());
            q.put("optionC", ex.getOptionC());
            q.put("optionD", ex.getOptionD());
            questions.add(q);
        }
        result.put("questions", questions);
        result.put("questionCount", questions.size());
        return result;
    }

    /**
     * 提交答卷并自动评分
     */
    public Map<String, Object> submitAnswers(Long studentId, String examId, Map<String, String> answers) {
        Exam exam = examMapperCommon.selectById(Long.valueOf(examId));
        if (exam == null) {
            throw new RuntimeException("考试不存在");
        }

        List<Exercise> exercises = exerciseMapper.selectByCourseId(String.valueOf(exam.getCourseId()));
        int totalScore = 0;
        int correctCount = 0;
        int scorePerQuestion = exercises.isEmpty() ? 0 : 100 / exercises.size();

        for (Exercise exercise : exercises) {
            String correctAnswer = exercise.getAnswer();
            String submittedAnswer = answers.get(String.valueOf(exercise.getId()));

            if (submittedAnswer != null && submittedAnswer.trim().equals(correctAnswer.trim())) {
                correctCount++;
                totalScore += scorePerQuestion;
            }
        }

        // 持久化考试成绩
        ExamRecord existing = examRecordMapper.selectByExamAndStudent(exam.getId(), studentId);
        if (existing != null) {
            existing.setScore(totalScore);
            existing.setStatus(correctCount > 0 ? "正常" : "缺考");
            examRecordMapper.update(existing);
        } else {
            ExamRecord record = new ExamRecord();
            record.setExamId(exam.getId());
            record.setStudentId(studentId);
            record.setScore(totalScore);
            record.setStatus(correctCount > 0 ? "正常" : "缺考");
            examRecordMapper.insert(record);
        }

        // 更新实考人数
        exam.setAttendedStudents(exam.getAttendedStudents() + 1);
        examMapperCommon.update(exam);

        Map<String, Object> result = new HashMap<>();
        result.put("totalScore", totalScore);
        result.put("correctCount", correctCount);
        result.put("totalCount", exercises.size());
        result.put("scorePerQuestion", scorePerQuestion);
        return result;
    }

    /**
     * 成绩查询（学生端）
     */
    public List<Map<String, Object>> getExamResults(Long studentId) {
        List<ExamRecord> records = examRecordMapper.selectByStudentId(studentId);
        if (records.isEmpty()) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (ExamRecord record : records) {
            Exam exam = examMapperCommon.selectById(record.getExamId());
            if (exam == null) continue;

            Map<String, Object> m = new HashMap<>();
            m.put("examId", String.valueOf(exam.getId()));
            m.put("examName", exam.getName());
            m.put("courseId", exam.getCourseId());
            m.put("score", record.getScore());
            m.put("status", record.getStatus());
            m.put("examDate", exam.getExamDate());

            // 通过 courseId 查课程名称
            Course course = courseMapper.selectById(exam.getCourseId());
            m.put("courseName", course != null ? course.getName() : "未知课程");

            // 正确/总题数从考试关联的习题估算
            List<Exercise> exercises = exerciseMapper.selectByCourseId(String.valueOf(exam.getCourseId()));
            int total = exercises.size();
            int correct = record.getScore() != null && total > 0
                    ? (int) Math.round(record.getScore() / (100.0 / total))
                    : 0;
            m.put("correctCount", correct);
            m.put("totalCount", total);

            result.add(m);
        }
        return result;
    }

    private boolean checkEnrolled(String courseId, Long studentId) {
        StudentCourse sc = studentCourseMapper.selectByStudentIdAndCourseId(studentId, Long.valueOf(courseId));
        return sc != null;
    }
}

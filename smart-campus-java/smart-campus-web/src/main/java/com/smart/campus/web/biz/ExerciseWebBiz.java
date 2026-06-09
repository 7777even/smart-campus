package com.smart.campus.web.biz;

import com.campus.entity.Exercise;
import com.smart.campus.web.mappers.WebExerciseMapper;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 习题业务（学生端）
 */
@Component
public class ExerciseWebBiz {

    private final WebExerciseMapper exerciseMapper;

    public ExerciseWebBiz(WebExerciseMapper exerciseMapper) {
        this.exerciseMapper = exerciseMapper;
    }

    /**
     * 按课程获取习题列表（隐藏答案）
     */
    public List<Map<String, Object>> listByCourse(String courseId) {
        List<Exercise> exercises = exerciseMapper.selectByCourseId(courseId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Exercise ex : exercises) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", String.valueOf(ex.getId()));
            m.put("courseId", ex.getCourseId());
            m.put("type", ex.getType());
            m.put("difficulty", ex.getDifficulty());
            m.put("question", ex.getQuestion());
            m.put("optionA", ex.getOptionA());
            m.put("optionB", ex.getOptionB());
            m.put("optionC", ex.getOptionC());
            m.put("optionD", ex.getOptionD());
            result.add(m);
        }
        return result;
    }

    /**
     * 习题详情（显示答案）
     */
    public Map<String, Object> getDetail(String id) {
        Exercise exercise = exerciseMapper.selectById(Long.valueOf(id));
        if (exercise == null) {
            throw new RuntimeException("习题不存在");
        }

        Map<String, Object> m = new HashMap<>();
        m.put("id", String.valueOf(exercise.getId()));
        m.put("courseId", exercise.getCourseId());
        m.put("type", exercise.getType());
        m.put("difficulty", exercise.getDifficulty());
        m.put("question", exercise.getQuestion());
        m.put("optionA", exercise.getOptionA());
        m.put("optionB", exercise.getOptionB());
        m.put("optionC", exercise.getOptionC());
        m.put("optionD", exercise.getOptionD());
        m.put("answer", exercise.getAnswer());
        m.put("analysis", exercise.getAnalysis());
        return m;
    }

    /**
     * 提交答案并返回判题结果
     */
    public Map<String, Object> submitAnswer(String exerciseId, String studentAnswer) {
        Exercise exercise = exerciseMapper.selectById(Long.valueOf(exerciseId));
        if (exercise == null) {
            throw new RuntimeException("习题不存在");
        }

        String correctAnswer = exercise.getAnswer();
        boolean correct = studentAnswer != null && studentAnswer.trim().equals(correctAnswer.trim());
        int score = correct ? 100 : 0;

        Map<String, Object> result = new HashMap<>();
        result.put("correct", correct);
        result.put("score", score);
        result.put("correctAnswer", correctAnswer);
        result.put("analysis", exercise.getAnalysis());
        return result;
    }
}

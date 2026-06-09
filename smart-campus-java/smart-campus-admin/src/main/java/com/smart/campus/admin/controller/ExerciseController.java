package com.smart.campus.admin.controller;

import com.campus.entity.PageResult;
import com.campus.result.R;
import com.campus.entity.Exercise;
import com.smart.campus.admin.service.impl.ExerciseServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 习题 Controller
 */
@RestController
@RequestMapping("/exercises")
@Tag(name = "习题管理")
public class ExerciseController {

    private final ExerciseServiceImpl exerciseService;

    public ExerciseController(ExerciseServiceImpl exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询习题")
    public R<PageResult<Exercise>> page(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "15") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) Long courseId) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("type", type);
        params.put("difficulty", difficulty);
        params.put("courseId", courseId);
        return R.ok(exerciseService.page(pageNo, pageSize, params));
    }

    @GetMapping("/list")
    @Operation(summary = "查询习题列表")
    public R<List<Exercise>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) Long courseId) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("type", type);
        params.put("difficulty", difficulty);
        params.put("courseId", courseId);
        return R.ok(exerciseService.list(params));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询习题")
    public R<Exercise> getById(@PathVariable Long id) {
        return R.ok(exerciseService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增习题")
    public R<Void> save(@RequestBody Exercise exercise) {
        exerciseService.save(exercise);
        return R.ok();
    }

    @PutMapping
    @Operation(summary = "修改习题")
    public R<Void> update(@RequestBody Exercise exercise) {
        exerciseService.update(exercise);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除习题")
    public R<Void> delete(@PathVariable Long id) {
        exerciseService.delete(id);
        return R.ok();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除习题")
    public R<Void> deleteBatch(@RequestBody Map<String, List<Long>> request) {
        exerciseService.deleteBatch(request.get("ids"));
        return R.ok();
    }
}

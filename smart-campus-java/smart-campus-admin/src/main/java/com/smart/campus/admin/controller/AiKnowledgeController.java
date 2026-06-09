package com.smart.campus.admin.controller;

import com.campus.entity.PageResult;
import com.campus.result.R;
import com.campus.entity.AiKnowledgeDoc;
import com.smart.campus.admin.service.AiKnowledgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 知识库控制器
 */
@RestController
@RequestMapping("/ai/knowledge")
@Tag(name = "AI 知识库")
public class AiKnowledgeController {

    private final AiKnowledgeService knowledgeService;

    public AiKnowledgeController(AiKnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询知识库")
    public R<PageResult<AiKnowledgeDoc>> page(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("category", category);
        params.put("status", status);
        PageResult<AiKnowledgeDoc> result = knowledgeService.page(pageNo, pageSize, params);
        return R.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取知识库文档详情")
    public R<AiKnowledgeDoc> getById(@PathVariable Long id) {
        return R.ok(knowledgeService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增知识库文档")
    public R<Void> save(@RequestBody AiKnowledgeDoc doc) {
        knowledgeService.save(doc);
        return R.ok();
    }

    @PutMapping
    @Operation(summary = "修改知识库文档")
    public R<Void> update(@RequestBody AiKnowledgeDoc doc) {
        knowledgeService.update(doc);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除知识库文档")
    public R<Void> delete(@PathVariable Long id) {
        knowledgeService.delete(id);
        return R.ok();
    }

    @GetMapping("/search")
    @Operation(summary = "搜索知识库文档")
    public R<List<AiKnowledgeDoc>> search(@RequestParam String keyword) {
        return R.ok(knowledgeService.search(keyword));
    }
}

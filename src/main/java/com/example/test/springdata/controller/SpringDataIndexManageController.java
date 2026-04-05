package com.example.test.springdata.controller;

import com.example.test.springdata.dto.SpringDataIndexExistsResult;
import com.example.test.springdata.dto.SpringDataIndexOperationResult;
import com.example.test.springdata.service.SpringDataIndexManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * SpringData 链路索引管理 REST 接口。
 *
 * <p>支持 query 参数 indexName：
 * 1. 传 indexName：操作指定索引。
 * 2. 不传 indexName：回退到配置默认索引。
 */
@RestController
@RequestMapping("/api/springdata/index")
public class SpringDataIndexManageController {

    @Autowired
    private SpringDataIndexManageService springDataIndexManageService;

    /**
     * 查询索引是否存在。
     *
     * @param indexName 索引名（可选）
     * @return 存在性结果
     */
    @GetMapping("/exists")
    public SpringDataIndexExistsResult exists(@RequestParam(value = "indexName", required = false) String indexName) {
        String targetIndexName = springDataIndexManageService.resolveIndexName(indexName);
        boolean exists = springDataIndexManageService.exists(targetIndexName);
        return new SpringDataIndexExistsResult(targetIndexName, exists);
    }

    /**
     * 创建索引（不存在才创建）。
     *
     * @param indexName 索引名（可选）
     * @return 操作结果
     */
    @PostMapping("/create")
    public SpringDataIndexOperationResult create(@RequestParam(value = "indexName", required = false) String indexName) {
        String targetIndexName = springDataIndexManageService.resolveIndexName(indexName);
        boolean changed = springDataIndexManageService.createIfAbsent(targetIndexName);
        String message = changed ? "索引创建成功" : "索引已存在，无需创建";
        return new SpringDataIndexOperationResult(targetIndexName, "create", changed, message);
    }

    /**
     * 删除索引（存在才删除）。
     *
     * @param indexName 索引名（可选）
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    public SpringDataIndexOperationResult delete(@RequestParam(value = "indexName", required = false) String indexName) {
        String targetIndexName = springDataIndexManageService.resolveIndexName(indexName);
        boolean changed = springDataIndexManageService.deleteIfExists(targetIndexName);
        String message = changed ? "索引删除成功" : "索引不存在，无需删除";
        return new SpringDataIndexOperationResult(targetIndexName, "delete", changed, message);
    }
}

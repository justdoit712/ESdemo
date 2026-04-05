package com.example.test.officialclient.controller;

import com.example.test.officialclient.dto.OfficialIndexExistsResult;
import com.example.test.officialclient.dto.OfficialIndexOperationResult;
import com.example.test.officialclient.service.OfficialIndexManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 官方 Client 索引管理 REST 接口。
 *
 * <p>支持 query 参数 {@code indexName}：
 * 1. 传参：操作指定索引。
 * 2. 不传：回退到默认索引。
 */
@RestController
@RequestMapping("/api/officialclient/index")
public class OfficialIndexManageController {

    @Autowired
    private OfficialIndexManageService officialIndexManageService;

    /**
     * 查询索引是否存在。
     *
     * @param indexName 索引名（可选）
     * @return 存在性结果
     * @throws IOException ES 通信异常
     */
    @GetMapping("/exists")
    public OfficialIndexExistsResult exists(@RequestParam(value = "indexName", required = false) String indexName)
            throws IOException {
        String targetIndexName = officialIndexManageService.resolveIndexName(indexName);
        boolean exists = officialIndexManageService.exists(targetIndexName);
        return new OfficialIndexExistsResult(targetIndexName, exists);
    }

    /**
     * 创建索引（不存在才创建）。
     *
     * @param indexName 索引名（可选）
     * @return 操作结果
     * @throws IOException ES 通信异常
     */
    @PostMapping("/create")
    public OfficialIndexOperationResult create(@RequestParam(value = "indexName", required = false) String indexName)
            throws IOException {
        String targetIndexName = officialIndexManageService.resolveIndexName(indexName);
        boolean changed = officialIndexManageService.createIfAbsent(targetIndexName);
        String message = changed ? "索引创建成功" : "索引已存在，无需创建";
        return new OfficialIndexOperationResult(targetIndexName, "create", changed, message);
    }

    /**
     * 删除索引（存在才删除）。
     *
     * @param indexName 索引名（可选）
     * @return 操作结果
     * @throws IOException ES 通信异常
     */
    @DeleteMapping("/delete")
    public OfficialIndexOperationResult delete(@RequestParam(value = "indexName", required = false) String indexName)
            throws IOException {
        String targetIndexName = officialIndexManageService.resolveIndexName(indexName);
        boolean changed = officialIndexManageService.deleteIfExists(targetIndexName);
        String message = changed ? "索引删除成功" : "索引不存在，无需删除";
        return new OfficialIndexOperationResult(targetIndexName, "delete", changed, message);
    }
}

package com.example.test.springdata.controller;

import com.example.test.springdata.dto.ProductSyncResult;
import com.example.test.springdata.service.ProductSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品同步操作的 REST 入口控制器。
 *
 * <p>用于对外提供手动触发同步的接口，便于联调和运维操作。
 */
@RestController
@RequestMapping("/api/springdata/sync/products")
public class ProductSyncController {

    @Autowired
    private ProductSyncService productSyncService;

    /**
     * 触发一次从 MySQL 商品表到 ES 索引的全量同步。
     *
     * @return 本次同步的结果摘要
     */
    @PostMapping("/full")
    public ProductSyncResult fullSync() {
        return productSyncService.syncActiveProductsToEs();
    }
}

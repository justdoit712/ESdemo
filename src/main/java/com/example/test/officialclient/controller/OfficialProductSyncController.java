package com.example.test.officialclient.controller;

import com.example.test.officialclient.dto.OfficialProductSyncResult;
import com.example.test.officialclient.service.OfficialProductSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 官方 Client 商品同步 REST 入口。
 */
@RestController
@RequestMapping("/api/officialclient/sync/products")
public class OfficialProductSyncController {

    @Autowired
    private OfficialProductSyncService officialProductSyncService;

    /**
     * 触发官方 Client 的全量同步。
     *
     * @return 同步结果
     * @throws IOException 与 ES 通信异常
     */
    @PostMapping("/full")
    public OfficialProductSyncResult fullSync() throws IOException {
        return officialProductSyncService.fullSync();
    }
}

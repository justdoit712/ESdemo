package com.example.test.officialclient.service;

import com.example.test.officialclient.convert.ProductIndexDocConverter;
import com.example.test.officialclient.dto.OfficialProductSyncResult;
import com.example.test.officialclient.model.ProductIndexDoc;
import com.example.test.springdata.entity.ProductEntity;
import com.example.test.springdata.mapper.mysql.ProductMybatisMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * 官方 Client 全量同步编排服务。
 */
@Service
public class OfficialProductSyncService {

    @Autowired
    private ProductMybatisMapper productMybatisMapper;

    @Autowired
    private ProductIndexDocConverter productIndexDocConverter;

    @Autowired
    private OfficialProductEsWriteService officialProductEsWriteService;

    /**
     * 执行一次全量同步。
     *
     * @return 同步结果摘要
     * @throws IOException 与 ES 通信异常
     */
    public OfficialProductSyncResult fullSync() throws IOException {
        long start = System.currentTimeMillis();

        List<ProductEntity> entities = productMybatisMapper.findActiveProducts();
        if (entities == null) {
            entities = Collections.emptyList();
        }

        List<ProductIndexDoc> docs = productIndexDocConverter.toDocs(entities);
        int writtenRows = officialProductEsWriteService.saveBatch(docs);

        long duration = System.currentTimeMillis() - start;
        return new OfficialProductSyncResult(
                entities.size(),
                docs.size(),
                writtenRows,
                duration
        );
    }
}

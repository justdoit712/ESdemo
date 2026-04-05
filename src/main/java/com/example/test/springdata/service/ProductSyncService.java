package com.example.test.springdata.service;

import com.example.test.springdata.convert.ProductDocumentConverter;
import com.example.test.springdata.document.ProductDocument;
import com.example.test.springdata.dto.ProductSyncResult;
import com.example.test.springdata.entity.ProductEntity;
import com.example.test.springdata.mapper.mysql.ProductMybatisMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * MySQL 商品数据到 Elasticsearch 的单向同步编排服务。
 *
 * <p>该服务只负责同步流程编排，不直接包含 SQL 细节和 Elasticsearch 持久化细节。
 */
@Service
public class ProductSyncService {

    @Autowired
    private ProductMybatisMapper productMybatisMapper;

    @Autowired
    private ProductDocumentConverter productDocumentConverter;

    @Autowired
    private ProductEsWriteService productEsWriteService;

    /**
     * 执行一次全量同步（仅同步未删除商品）。
     *
     * @return 本次同步的统计摘要
     */
    public ProductSyncResult syncActiveProductsToEs() {
        long start = System.currentTimeMillis();

        List<ProductEntity> entities = productMybatisMapper.findActiveProducts();
        if (entities == null) {
            entities = Collections.emptyList();
        }

        List<ProductDocument> documents = productDocumentConverter.toDocuments(entities);
        List<ProductDocument> savedDocuments = productEsWriteService.saveBatch(documents);

        long duration = System.currentTimeMillis() - start;
        return new ProductSyncResult(
                entities.size(),
                documents.size(),
                savedDocuments.size(),
                duration
        );
    }
}

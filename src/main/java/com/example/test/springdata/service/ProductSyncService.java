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
 * Coordinates one-way synchronization from MySQL product data to Elasticsearch.
 *
 * <p>This service only orchestrates the pipeline and does not include SQL details
 * or Elasticsearch persistence details directly.
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
     * Executes one full synchronization for all active products.
     *
     * @return summary statistics for this synchronization run
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

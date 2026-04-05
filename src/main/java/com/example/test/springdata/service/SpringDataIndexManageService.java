package com.example.test.springdata.service;

import com.example.test.springdata.document.ProductDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * SpringData 链路索引管理服务。
 *
 * <p>职责：
 * 1. 解析并校验目标索引名（支持动态参数与默认配置）。
 * 2. 查询索引是否存在。
 * 3. 在索引不存在时创建索引并写入 ProductDocument 映射。
 * 4. 在索引存在时删除索引。
 */
@Service
public class SpringDataIndexManageService {

    /**
     * Spring Data Elasticsearch 操作入口。
     */
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    /**
     * 默认索引名，来自配置项：app.es.springdata.index-name。
     */
    @Value("${app.es.springdata.index-name}")
    private String defaultIndexName;

    /**
     * 解析目标索引名。
     *
     * <p>当参数为空时回退到默认索引名；最终会执行命名校验。
     *
     * @param indexName 请求参数中的索引名，可为空
     * @return 解析后的目标索引名
     */
    public String resolveIndexName(String indexName) {
        String resolved = (indexName == null || indexName.isBlank()) ? defaultIndexName : indexName.trim();
        validateIndexName(resolved);
        return resolved;
    }

    /**
     * 查询指定索引是否存在。
     *
     * @param indexName 目标索引名，可为空
     * @return true 表示存在，false 表示不存在
     */
    public boolean exists(String indexName) {
        String targetIndexName = resolveIndexName(indexName);
        IndexOperations indexOperations = elasticsearchOperations.indexOps(IndexCoordinates.of(targetIndexName));
        return indexOperations.exists();
    }

    /**
     * 在索引不存在时创建索引并写入映射。
     *
     * @param indexName 目标索引名，可为空
     * @return true 表示本次创建成功，false 表示索引已存在
     */
    public boolean createIfAbsent(String indexName) {
        String targetIndexName = resolveIndexName(indexName);
        IndexOperations indexOperations = elasticsearchOperations.indexOps(IndexCoordinates.of(targetIndexName));
        if (indexOperations.exists()) {
            return false;
        }

        boolean created = indexOperations.create();
        if (!created) {
            return false;
        }

        Document mapping = indexOperations.createMapping(ProductDocument.class);
        boolean mappingApplied = indexOperations.putMapping(mapping);
        return mappingApplied;
    }

    /**
     * 在索引存在时删除索引。
     *
     * @param indexName 目标索引名，可为空
     * @return true 表示本次删除成功，false 表示索引不存在
     */
    public boolean deleteIfExists(String indexName) {
        String targetIndexName = resolveIndexName(indexName);
        IndexOperations indexOperations = elasticsearchOperations.indexOps(IndexCoordinates.of(targetIndexName));
        if (!indexOperations.exists()) {
            return false;
        }
        return indexOperations.delete();
    }

    /**
     * 索引名基础校验。
     *
     * @param indexName 待校验索引名
     */
    private void validateIndexName(String indexName) {
        if (indexName == null || indexName.isBlank()) {
            throw new IllegalArgumentException("indexName must not be blank");
        }
        if (!indexName.equals(indexName.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("indexName must be lowercase");
        }
    }
}

package com.example.test.officialclient.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import com.example.test.officialclient.model.ProductIndexDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * 商品 ES 写入服务（官方 Java Client 版本）。
 *
 * <p>该服务负责将业务文档批量写入 ES。索引创建与删除等管理动作，
 * 统一交由 {@link OfficialIndexManageService}，避免同一份 mapping 维护多处。
 */
@Service
public class OfficialProductEsWriteService {

    /**
     * 官方 Elasticsearch Java Client。
     */
    @Autowired
    private ElasticsearchClient elasticsearchClient;

    /**
     * 索引管理服务。
     */
    @Autowired
    private OfficialIndexManageService officialIndexManageService;

    /**
     * 目标索引名，来自配置项：app.es.officialclient.index-name。
     */
    @Value("${app.es.officialclient.index-name}")
    private String indexName;

    /**
     * 批量写入商品文档到 ES。
     *
     * <p>处理规则：
     * 1. 空集合直接返回 0。
     * 2. 自动过滤 null 文档和主键为空的文档。
     * 3. 写入前先确保索引存在（不存在则自动创建）。
     *
     * @param docs 待写入文档列表
     * @return 成功写入数量
     * @throws IOException ES 通信异常
     */
    public int saveBatch(List<ProductIndexDoc> docs) throws IOException {
        if (docs == null || docs.isEmpty()) {
            return 0;
        }

        officialIndexManageService.createIfAbsent();

        BulkRequest.Builder requestBuilder = new BulkRequest.Builder();
        for (ProductIndexDoc doc : docs) {
            if (doc == null || doc.getId() == null) {
                continue;
            }
            requestBuilder.operations(op -> op.index(i -> i
                    .index(indexName)
                    .id(String.valueOf(doc.getId()))
                    .document(doc)
            ));
        }

        BulkResponse response = elasticsearchClient.bulk(requestBuilder.build());
        if (!response.errors()) {
            return response.items().size();
        }

        int success = 0;
        for (BulkResponseItem item : response.items()) {
            if (item.error() == null) {
                success++;
            }
        }
        return success;
    }
}

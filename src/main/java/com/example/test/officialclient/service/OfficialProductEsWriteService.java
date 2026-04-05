package com.example.test.officialclient.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.example.test.officialclient.model.ProductIndexDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * 商品 ES 写入服务（官方 Java Client 版本）。
 *
 * <p>该服务负责两件事：
 * 1. 首次写入前确保索引存在，并按预定义 mapping 创建索引。
 * 2. 使用 bulk API 批量写入商品文档，并返回成功写入条数。
 */
@Service
public class OfficialProductEsWriteService {

    /**
     * 官方 Elasticsearch Java Client。
     */
    @Autowired
    private ElasticsearchClient elasticsearchClient;

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
     * 3. 若 bulk 全部成功，直接返回 items 数量。
     * 4. 若 bulk 部分失败，返回 error 为空的成功条数。
     *
     * @param docs 待写入文档列表
     * @return 成功写入数量
     * @throws IOException 与 ES 通信异常
     */
    public int saveBatch(List<ProductIndexDoc> docs) throws IOException {
        if (docs == null || docs.isEmpty()) {
            return 0;
        }

        ensureIndex();

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

    /**
     * 确保目标索引存在。
     *
     * <p>当索引不存在时，按固定 mapping 创建，避免依赖动态映射导致字段类型漂移。
     *
     * @throws IOException 与 ES 通信异常
     */
    private void ensureIndex() throws IOException {
        BooleanResponse exists = elasticsearchClient.indices().exists(e -> e.index(indexName));
        if (exists.value()) {
            return;
        }

        elasticsearchClient.indices().create(c -> c
                .index(indexName)
                .settings(s -> s
                        .numberOfShards("1")
                        .numberOfReplicas("1")
                )
                .mappings(m -> m
                        // MySQL 主键，对应 ES 文档 id 字段。
                        .properties("id", p -> p.long_(l -> l))
                        // 名称字段：支持全文检索，并保留 keyword 用于精确匹配/聚合。
                        .properties("name", p -> p.text(t -> t
                                .fields("keyword", f -> f.keyword(k -> k))
                        ))
                        // 商品描述，全文检索场景。
                        .properties("description", p -> p.text(t -> t))
                        // 库存数量。
                        .properties("count", p -> p.integer(i -> i))
                        // 价格字段，按分值缩放，便于精度和范围查询。
                        .properties("price", p -> p.scaledFloat(sf -> sf.scalingFactor(100.0)))
                        // 日期字段，兼容常见日期和 epoch_millis 格式。
                        .properties("date", p -> p.date(d -> d.format("strict_date_optional_time||epoch_millis")))
                        // 逻辑删除标记。
                        .properties("isDeleted", p -> p.boolean_(b -> b))
                )
        );
    }
}

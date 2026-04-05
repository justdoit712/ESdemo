package com.example.test.officialclient.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Locale;

/**
 * 官方 Elasticsearch Java Client 链路的索引管理服务。
 *
 * <p>该服务统一封装索引生命周期相关能力，包含：
 * 1. 解析并校验目标索引名（支持请求参数与默认配置两种来源）。
 * 2. 判断索引是否存在。
 * 3. 在索引不存在时创建索引（包含固定 settings 与 mapping）。
 * 4. 在索引存在时删除索引。
 *
 * <p>设计说明：
 * 1. Controller 不直接处理索引名规则，统一下沉到 Service，避免规则分散。
 * 2. 提供无参方法和有参方法两套入口，兼容默认索引与动态索引场景。
 */
@Service
public class OfficialIndexManageService {

    /**
     * 官方 Elasticsearch Java Client。
     *
     * <p>用于执行索引存在性检查、创建索引和删除索引等操作。
     */
    @Autowired
    private ElasticsearchClient elasticsearchClient;

    /**
     * 默认索引名。
     *
     * <p>来源：配置项 {@code app.es.officialclient.index-name}。
     * 当接口未传入 {@code indexName} 参数时，回退使用该值。
     */
    @Value("${app.es.officialclient.index-name}")
    private String defaultIndexName;

    /**
     * 解析目标索引名并执行合法性校验。
     *
     * <p>规则：
     * 1. 若入参为空（{@code null}、空串、仅空白字符），使用默认索引名。
     * 2. 若入参不为空，先做 {@code trim()} 去除首尾空白。
     * 3. 对最终索引名执行格式校验，校验不通过则抛出异常。
     *
     * @param indexName 请求参数中的索引名，可为空
     * @return 解析后的最终索引名
     * @throws IllegalArgumentException 当索引名为空或不满足命名规范时抛出
     */
    public String resolveIndexName(String indexName) {
        String resolved = (indexName == null || indexName.isBlank()) ? defaultIndexName : indexName.trim();
        validateIndexName(resolved);
        return resolved;
    }

    /**
     * 判断默认索引是否存在。
     *
     * @return {@code true} 表示索引存在；{@code false} 表示索引不存在
     * @throws IOException 当调用 Elasticsearch 接口失败时抛出
     * @throws IllegalArgumentException 当默认索引名不合法时抛出
     */
    public boolean exists() throws IOException {
        return exists(defaultIndexName);
    }

    /**
     * 判断指定索引是否存在。
     *
     * <p>该方法会先解析并校验索引名，再调用 Elasticsearch
     * {@code indices.exists} 接口查询存在性。
     *
     * @param indexName 目标索引名，可为空（为空时回退默认索引）
     * @return {@code true} 表示索引存在；{@code false} 表示索引不存在
     * @throws IOException 当调用 Elasticsearch 接口失败时抛出
     * @throws IllegalArgumentException 当索引名不合法时抛出
     */
    public boolean exists(String indexName) throws IOException {
        String targetIndexName = resolveIndexName(indexName);
        BooleanResponse exists = elasticsearchClient.indices().exists(e -> e.index(targetIndexName));
        return exists.value();
    }

    /**
     * 在默认索引不存在时创建索引。
     *
     * @return {@code true} 表示本次已创建索引；
     * {@code false} 表示索引原本已存在，未执行创建
     * @throws IOException 当调用 Elasticsearch 接口失败时抛出
     * @throws IllegalArgumentException 当默认索引名不合法时抛出
     */
    public boolean createIfAbsent() throws IOException {
        return createIfAbsent(defaultIndexName);
    }

    /**
     * 在指定索引不存在时创建索引。
     *
     * <p>若索引已存在，直接返回 {@code false}。若索引不存在，则按统一 settings
     * 和 mapping 创建索引并返回 {@code true}。
     *
     * @param indexName 目标索引名，可为空（为空时回退默认索引）
     * @return {@code true} 表示本次已创建索引；
     * {@code false} 表示索引原本已存在，未执行创建
     * @throws IOException 当调用 Elasticsearch 接口失败时抛出
     * @throws IllegalArgumentException 当索引名不合法时抛出
     */
    public boolean createIfAbsent(String indexName) throws IOException {
        String targetIndexName = resolveIndexName(indexName);
        if (exists(targetIndexName)) {
            return false;
        }

        elasticsearchClient.indices().create(c -> c
                .index(targetIndexName)
                .settings(s -> s
                        .numberOfShards("1")
                        .numberOfReplicas("1")
                )
                .mappings(m -> m
                        // 主键字段，对应 MySQL id。
                        .properties("id", p -> p.long_(l -> l))
                        // 名称字段：支持全文检索，并提供 keyword 子字段用于精确匹配/聚合。
                        .properties("name", p -> p.text(t -> t
                                .fields("keyword", f -> f.keyword(k -> k))
                        ))
                        // 商品描述字段。
                        .properties("description", p -> p.text(t -> t))
                        // 库存数量字段。
                        .properties("count", p -> p.integer(i -> i))
                        // 价格字段，使用 scaled_float，缩放因子 100 以提高金额精度控制。
                        .properties("price", p -> p.scaledFloat(sf -> sf.scalingFactor(100.0)))
                        // 日期字段，兼容 ISO 日期与 epoch_millis。
                        .properties("date", p -> p.date(d -> d.format("strict_date_optional_time||epoch_millis")))
                        // 逻辑删除标记字段。
                        .properties("isDeleted", p -> p.boolean_(b -> b))
                )
        );
        return true;
    }

    /**
     * 在默认索引存在时删除索引。
     *
     * @return {@code true} 表示本次已删除索引；
     * {@code false} 表示索引原本不存在，未执行删除
     * @throws IOException 当调用 Elasticsearch 接口失败时抛出
     * @throws IllegalArgumentException 当默认索引名不合法时抛出
     */
    public boolean deleteIfExists() throws IOException {
        return deleteIfExists(defaultIndexName);
    }

    /**
     * 在指定索引存在时删除索引。
     *
     * <p>若索引不存在，直接返回 {@code false}。若索引存在，则执行删除并返回 {@code true}。
     *
     * @param indexName 目标索引名，可为空（为空时回退默认索引）
     * @return {@code true} 表示本次已删除索引；
     * {@code false} 表示索引原本不存在，未执行删除
     * @throws IOException 当调用 Elasticsearch 接口失败时抛出
     * @throws IllegalArgumentException 当索引名不合法时抛出
     */
    public boolean deleteIfExists(String indexName) throws IOException {
        String targetIndexName = resolveIndexName(indexName);
        if (!exists(targetIndexName)) {
            return false;
        }
        elasticsearchClient.indices().delete(d -> d.index(targetIndexName));
        return true;
    }

    /**
     * 校验索引名合法性。
     *
     * <p>当前校验规则：
     * 1. 索引名不能为空。
     * 2. 索引名必须为小写。
     *
     * @param indexName 待校验的索引名
     * @throws IllegalArgumentException 当索引名不满足规则时抛出
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

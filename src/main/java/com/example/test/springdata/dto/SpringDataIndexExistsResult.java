package com.example.test.springdata.dto;

/**
 * SpringData 链路索引存在性查询结果。
 *
 * @param indexName 索引名称
 * @param exists 索引是否存在
 */
public record SpringDataIndexExistsResult(
        String indexName,
        boolean exists
) {
}

package com.example.test.officialclient.dto;

/**
 * 索引存在性查询结果。
 *
 * @param indexName 索引名称
 * @param exists 索引是否存在
 */
public record OfficialIndexExistsResult(
        String indexName,
        boolean exists
) {
}

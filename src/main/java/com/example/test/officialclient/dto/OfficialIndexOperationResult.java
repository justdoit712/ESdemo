package com.example.test.officialclient.dto;

/**
 * 索引操作结果。
 *
 * @param indexName 索引名称
 * @param operation 操作类型（create/delete）
 * @param changed 是否发生实际变更
 * @param message 操作说明
 */
public record OfficialIndexOperationResult(
        String indexName,
        String operation,
        boolean changed,
        String message
) {
}

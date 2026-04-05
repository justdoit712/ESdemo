package com.example.test.springdata.dto;

/**
 * SpringData 链路索引操作结果。
 *
 * @param indexName 索引名称
 * @param operation 操作类型（create/delete）
 * @param changed 是否发生实际变更
 * @param message 操作说明
 */
public record SpringDataIndexOperationResult(
        String indexName,
        String operation,
        boolean changed,
        String message
) {
}

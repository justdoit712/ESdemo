package com.example.test.springdata.dto;

/**
 * 一次全量同步（MySQL -> Elasticsearch）的结果摘要。
 *
 * <p>该记录由同步服务返回，并通过接口对外暴露，便于快速查看本次处理规模。
 *
 * @param mysqlRows 从 MySQL 读取到的行数
 * @param convertedRows 成功转换为 ES 文档的行数
 * @param writtenRows 成功写入 Elasticsearch 的文档数
 * @param durationMs 本次同步总耗时（毫秒）
 */
public record ProductSyncResult(
        int mysqlRows,
        int convertedRows,
        int writtenRows,
        long durationMs
) {
}

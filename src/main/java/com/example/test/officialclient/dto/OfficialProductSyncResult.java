package com.example.test.officialclient.dto;

/**
 * 官方 Client 全量同步结果。
 *
 * @param mysqlRows MySQL 读取行数
 * @param convertedRows 转换成功行数
 * @param writtenRows ES 写入成功行数
 * @param durationMs 本次耗时（毫秒）
 */
public record OfficialProductSyncResult(
        int mysqlRows,
        int convertedRows,
        int writtenRows,
        long durationMs
) {
}

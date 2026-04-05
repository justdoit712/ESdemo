package com.example.test.officialclient.config;

import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 官方 Elasticsearch Java Client 的 JSON 序列化配置。
 *
 * <p>仅用于 official-client 链路：
 * Elasticsearch Java Client 默认 JsonpMapper 在处理 LocalDate 等时间类型时，
 * 不会自动复用 Spring MVC 的 Jackson 模块配置，可能导致序列化失败。
 *
 * <p>这里将 JsonpMapper 绑定到 Spring Boot 已配置好的 ObjectMapper，
 * 以确保 LocalDate/LocalDateTime 等类型可以被正常序列化。
 */
@Configuration
public class OfficialClientJsonpMapperConfig {

    /**
     * 提供官方 Client 使用的 JsonpMapper。
     *
     * @param objectMapper Spring Boot 自动配置的 ObjectMapper
     * @return 使用 Spring ObjectMapper 的 JsonpMapper
     */
    @Bean
    public JsonpMapper jsonpMapper(ObjectMapper objectMapper) {
        return new JacksonJsonpMapper(objectMapper.copy());
    }
}

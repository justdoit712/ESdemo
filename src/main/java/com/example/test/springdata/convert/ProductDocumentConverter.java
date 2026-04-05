package com.example.test.springdata.convert;

import com.example.test.springdata.document.ProductDocument;
import com.example.test.springdata.entity.ProductEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 商品文档转换器。
 * 用于将 MySQL 查询结果的 ProductEntity 转换为 Elasticsearch 的 ProductDocument。
 */
@Component
public class ProductDocumentConverter {

    /**
     * 单个对象转换。
     *
     * @param entity MySQL 实体
     * @return ES 文档对象；入参为 null 时返回 null
     */
    public ProductDocument toDocument(ProductEntity entity) {
        if (entity == null) {
            return null;
        }

        return new ProductDocument(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getCount(),
                entity.getPrice(),
                entity.getDate(),
                entity.getIsDeleted()
        );
    }

    /**
     * 批量转换。
     *
     * @param entities MySQL 实体集合
     * @return ES 文档集合；不会返回 null
     */
    public List<ProductDocument> toDocuments(List<ProductEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .map(this::toDocument)
                .toList();
    }
}

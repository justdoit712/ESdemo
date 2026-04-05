package com.example.test.officialclient.convert;

import com.example.test.officialclient.model.ProductIndexDoc;
import com.example.test.springdata.entity.ProductEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 将 MySQL 商品实体转换为官方 Client 写入 ES 所需的文档对象。
 */
@Component
public class ProductIndexDocConverter {

    /**
     * 单个对象转换。
     *
     * @param entity MySQL 商品实体
     * @return ES 文档对象；入参为空时返回 null
     */
    public ProductIndexDoc toDoc(ProductEntity entity) {
        if (entity == null) {
            return null;
        }

        ProductIndexDoc doc = new ProductIndexDoc();
        doc.setId(entity.getId());
        doc.setName(entity.getName());
        doc.setDescription(entity.getDescription());
        doc.setCount(entity.getCount());
        doc.setPrice(entity.getPrice());
        doc.setDate(entity.getDate());
        doc.setIsDeleted(entity.getIsDeleted());
        return doc;
    }

    /**
     * 批量转换。
     *
     * @param entities MySQL 商品实体集合
     * @return ES 文档集合；保证不为 null
     */
    public List<ProductIndexDoc> toDocs(List<ProductEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .map(this::toDoc)
                .toList();
    }
}

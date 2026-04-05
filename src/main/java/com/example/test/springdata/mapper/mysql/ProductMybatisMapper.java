package com.example.test.springdata.mapper.mysql;

import com.example.test.springdata.entity.ProductEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ProductMybatisMapper {

    @Select("""
            SELECT id, name, description, count, price, date, is_deleted
            FROM product
            WHERE is_deleted = 0
            """)
    List<ProductEntity> findActiveProducts();
}

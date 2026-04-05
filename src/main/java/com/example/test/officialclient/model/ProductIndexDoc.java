package com.example.test.officialclient.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 官方 Elasticsearch Java Client 使用的商品索引文档模型。
 */
public class ProductIndexDoc {

    private Long id;
    private String name;
    private String description;
    private Integer count;
    private BigDecimal price;
    private LocalDate date;
    private Boolean isDeleted;

    public ProductIndexDoc() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}

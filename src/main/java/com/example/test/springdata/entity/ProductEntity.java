package com.example.test.springdata.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProductEntity {

    private Long id;
    private String name;
    private String description;
    private Integer count;
    private BigDecimal price;
    private LocalDate date;
    private Boolean isDeleted;

    public ProductEntity() {
    }

    public ProductEntity(Long id, String name, String description, Integer count,
                         BigDecimal price, LocalDate date, Boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.count = count;
        this.price = price;
        this.date = date;
        this.isDeleted = isDeleted;
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

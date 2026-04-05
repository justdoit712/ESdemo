package com.example.test.springdata.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import java.math.BigDecimal;
import java.time.LocalDate;

// 标记这是一个 Elasticsearch 文档，indexName 指定写入的索引名。
@Document(indexName = "product")
public class ProductDocument {

    // ES 文档主键，通常与 MySQL 主键保持一致，便于同步和覆盖写入。
    @Id
    private Long id;

    // name 支持全文检索，同时保留 keyword 子字段用于精确匹配和聚合。
    @MultiField(
            mainField = @Field(type = FieldType.Text),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword)
            }
    )
    private String name;

    // 描述字段使用 text 类型，适合做 match 查询。
    @Field(type = FieldType.Text)
    private String description;

    // 库存数量。
    @Field(type = FieldType.Integer)
    private Integer count;

    // 价格字段使用 scaled_float，适合金额类数据。
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100)
    private BigDecimal price;

    // 上架日期，按 date 格式存储。
    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate date;

    // 逻辑删除标记。
    @Field(type = FieldType.Boolean)
    private Boolean isDeleted;

    public ProductDocument() {
    }

    public ProductDocument(
            Long id,
            String name,
            String description,
            Integer count,
            BigDecimal price,
            LocalDate date,
            Boolean isDeleted
    ) {
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

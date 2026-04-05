package com.example.test.springdata.repository.es;

import com.example.test.springdata.document.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductEsRepository extends ElasticsearchRepository<ProductDocument, Long> {
}

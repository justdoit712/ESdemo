package com.example.test.springdata.service;

import com.example.test.springdata.document.ProductDocument;
import com.example.test.springdata.repository.es.ProductEsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class ProductEsWriteService {

    @Autowired
    private ProductEsRepository productEsRepository;

    public ProductDocument save(ProductDocument document) {
        if (document == null) {
            return null;
        }
        return productEsRepository.save(document);
    }

    public List<ProductDocument> saveBatch(List<ProductDocument> documents) {
        if (documents == null || documents.isEmpty()) {
            return Collections.emptyList();
        }

        List<ProductDocument> validDocuments = documents.stream()
                .filter(Objects::nonNull)
                .toList();
        if (validDocuments.isEmpty()) {
            return Collections.emptyList();
        }

        Iterable<ProductDocument> saved = productEsRepository.saveAll(validDocuments);
        List<ProductDocument> savedDocuments = new ArrayList<>();
        saved.forEach(savedDocuments::add);
        return savedDocuments;
    }
}

package com.stockify.project.repository;

import com.stockify.project.model.entity.SalesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SalesRepository extends JpaRepository<SalesEntity, Long> {

    @Query(value = "SELECT MAX(CAST(SUBSTRING(document_number, 3) AS INTEGER)) FROM sales", nativeQuery = true)
    Integer findMaxDocumentNumberNumeric();
}

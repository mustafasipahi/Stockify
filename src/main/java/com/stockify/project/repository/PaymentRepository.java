package com.stockify.project.repository;

import com.stockify.project.model.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    @Query(value = "SELECT MAX(CAST(SUBSTRING(document_number, 3) AS UNSIGNED)) FROM payment", nativeQuery = true)
    Integer findMaxDocumentNumberNumeric();
}

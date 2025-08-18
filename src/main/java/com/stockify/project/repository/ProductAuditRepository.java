package com.stockify.project.repository;

import com.stockify.project.model.entity.ProductAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductAuditRepository extends JpaRepository<ProductAuditEntity, Long> {
}

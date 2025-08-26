package com.stockify.project.repository;

import com.stockify.project.model.entity.SalesItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesItemRepository extends JpaRepository<SalesItemEntity, Long> {
}

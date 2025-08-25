package com.stockify.project.repository;

import com.stockify.project.model.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryEntity, Long>, JpaSpecificationExecutor<InventoryEntity> {

    Optional<InventoryEntity> findByIdAndTenantId(Long id, Long tenantId);
}

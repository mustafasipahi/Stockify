package com.stockify.project.repository.tenant;

import com.stockify.project.model.entity.tenant.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InventoryRepository extends JpaRepository<InventoryEntity, Long>, JpaSpecificationExecutor<InventoryEntity> {
}

package com.stockify.project.repository;

import com.stockify.project.enums.InventoryStatus;
import com.stockify.project.model.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryEntity, Long>, JpaSpecificationExecutor<InventoryEntity> {

    Optional<InventoryEntity> findByIdAndTenantId(Long id, Long tenantId);

    Optional<InventoryEntity> findByProductIdAndTenantId(Long productId, Long tenantId);

    @Modifying
    @Query("UPDATE InventoryEntity i " +
            "SET i.productCount = i.productCount - :decreaseProductCount, i.status = :status " +
            "WHERE i.productId = :productId AND i.tenantId = :tenantId")
    void decreaseProductCount(Long productId, Long tenantId, Integer decreaseProductCount, InventoryStatus status);
}

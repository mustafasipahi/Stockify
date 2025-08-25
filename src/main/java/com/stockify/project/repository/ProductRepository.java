package com.stockify.project.repository;

import com.stockify.project.model.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {

    Optional<ProductEntity> findByIdAndTenantId(Long id, Long tenantId);

    Optional<ProductEntity> findByNameAndTenantId(String name, Long tenantId);

    Optional<ProductEntity> findFirstByTenantIdOrderByCreatedDateDesc(Long tenantId);
}

package com.stockify.project.repository.tenant;

import com.stockify.project.model.entity.tenant.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {

    Optional<ProductEntity> findFirstByOrderByCreatedDateDesc();

    Optional<ProductEntity> findByName(String name);
}

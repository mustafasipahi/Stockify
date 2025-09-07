package com.stockify.project.repository;

import com.stockify.project.enums.CategoryStatus;
import com.stockify.project.model.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    Optional<CategoryEntity> findByIdAndTenantId(Long id, Long tenantId);

    Optional<CategoryEntity> findByNameAndTenantId(String name, Long tenantId);

    List<CategoryEntity> findAllByStatusAndTenantIdOrderByCreatedDateDesc(CategoryStatus status, Long tenantId);
}

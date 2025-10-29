package com.stockify.project.repository;

import com.stockify.project.enums.ProductStatus;
import com.stockify.project.model.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {

    Optional<ProductEntity> findByIdAndTenantId(Long id, Long tenantId);

    List<ProductEntity> findAllByIdInAndTenantId(List<Long> idList, Long tenantId);

    Optional<ProductEntity> findByCreatorUserIdAndCategoryIdAndTenantIdAndStatus(Long creatorUserId, Long categoryId, Long tenantId, ProductStatus status);

    List<ProductEntity> findByCreatorUserIdAndCategoryIdAndTenantId(Long creatorUserId, Long categoryId, Long tenantId);

    Optional<ProductEntity> findByCreatorUserIdAndNameAndTenantId(Long creatorUserId, String name, Long tenantId);

    Optional<ProductEntity> findFirstByCreatorUserIdAndTenantIdOrderByCreatedDateDesc(Long creatorUserId, Long tenantId);
}

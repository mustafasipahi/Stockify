package com.project.envantra.repository;

import com.project.envantra.enums.ProductStatus;
import com.project.envantra.model.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {

    List<ProductEntity> findAllByIdIn(List<Long> idList);

    Optional<ProductEntity> findByCreatorUserIdAndCategoryId(Long creatorUserId, Long categoryId);

    Optional<ProductEntity> findByCreatorUserIdAndCategoryIdAndStatus(Long creatorUserId, Long categoryId, ProductStatus status);

    Optional<ProductEntity> findByCreatorUserIdAndName(Long creatorUserId, String name);

    Optional<ProductEntity> findFirstByCreatorUserIdOrderByCreatedDateDesc(Long creatorUserId);
}

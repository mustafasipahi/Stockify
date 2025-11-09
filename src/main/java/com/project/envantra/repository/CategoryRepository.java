package com.project.envantra.repository;

import com.project.envantra.enums.CategoryStatus;
import com.project.envantra.model.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    Optional<CategoryEntity> findByNameAndCreatorUserId(String name, Long creatorUserId);

    List<CategoryEntity> findAllByCreatorUserIdAndStatusOrderByCreatedDateDesc(Long creatorUserId, CategoryStatus status);
}

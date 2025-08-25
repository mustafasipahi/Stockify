package com.stockify.project.repository.tenant;

import com.stockify.project.model.entity.tenant.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    Optional<CategoryEntity> findByName(String name);
    List<CategoryEntity> findAllByOrderByNameAsc();
}

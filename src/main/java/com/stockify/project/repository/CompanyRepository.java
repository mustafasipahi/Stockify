package com.stockify.project.repository;

import com.stockify.project.model.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {

    Optional<CompanyEntity> findByCreatorUserId(Long creatorUserId);
}

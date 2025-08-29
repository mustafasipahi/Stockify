package com.stockify.project.repository;

import com.stockify.project.model.entity.CompanyInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyInfoRepository extends JpaRepository<CompanyInfoEntity, Long> {

    Optional<CompanyInfoEntity> findByTenantId(Long tenantId);
}

package com.stockify.project.repository;

import com.stockify.project.model.entity.DocumentEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    List<DocumentEntity> findByBrokerIdAndTenantId(Long brokerId, Long tenantId, Sort sort);

    List<DocumentEntity> findAllByIdInAndTenantId(Set<Long> ids, Long tenantId);

    Optional<DocumentEntity> findByIdAndTenantId(Long id, Long tenantId);
}

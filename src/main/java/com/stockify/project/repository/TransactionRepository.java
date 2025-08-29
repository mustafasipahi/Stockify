package com.stockify.project.repository;

import com.stockify.project.model.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long>, JpaSpecificationExecutor<TransactionEntity> {

    Optional<TransactionEntity> findTopByBrokerIdAndTenantIdOrderByCreatedDateDesc(Long brokerId, Long tenantId);
}

package com.stockify.project.repository;

import com.stockify.project.enums.BrokerStatus;
import com.stockify.project.model.entity.BrokerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BrokerRepository extends JpaRepository<BrokerEntity, Long> {

    Optional<BrokerEntity> findByIdAndTenantId(Long id, Long tenantId);

    List<BrokerEntity> findAllByStatusAndTenantIdOrderByCreatedDateDesc(BrokerStatus status, Long tenantId);
}

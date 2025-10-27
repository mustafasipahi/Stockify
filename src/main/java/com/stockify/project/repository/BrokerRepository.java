package com.stockify.project.repository;

import com.stockify.project.enums.BrokerStatus;
import com.stockify.project.model.entity.BrokerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BrokerRepository extends JpaRepository<BrokerEntity, Long> {

    Optional<BrokerEntity> findByIdAndTenantId(Long id, Long tenantId);

    @Query("SELECT b FROM BrokerEntity b " +
            "WHERE b.creatorUserId = :creatorUserId " +
            "AND b.status = :status " +
            "AND b.tenantId = :tenantId " +
            "ORDER BY b.createdDate DESC")
    List<BrokerEntity> getUserBrokerList(Long creatorUserId, BrokerStatus status, Long tenantId);
}

package com.stockify.project.repository;

import com.stockify.project.enums.BrokerStatus;
import com.stockify.project.model.entity.BrokerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BrokerRepository extends JpaRepository<BrokerEntity, Long> {

    @Query("SELECT b FROM BrokerEntity b " +
            "WHERE b.creatorUserId = :creatorUserId " +
            "AND b.status = :status " +
            "ORDER BY b.createdDate DESC")
    List<BrokerEntity> getUserBrokerList(Long creatorUserId, BrokerStatus status);
}

package com.project.envantra.repository;

import com.project.envantra.enums.BrokerStatus;
import com.project.envantra.model.entity.BrokerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BrokerRepository extends JpaRepository<BrokerEntity, Long> {

    @Query("SELECT MAX(b.orderNo) FROM BrokerEntity b WHERE b.creatorUserId = :creatorUserId")
    Integer findMaxOrderNoByCreatorUserId(Long creatorUserId);

    @Query("SELECT b FROM BrokerEntity b WHERE b.creatorUserId = :creatorUserId ORDER BY b.orderNo ASC")
    List<BrokerEntity> findByCreatorUserIdOrderByOrderNoAsc(Long creatorUserId);

    @Query("SELECT b FROM BrokerEntity b " +
            "WHERE b.creatorUserId = :creatorUserId " +
            "AND b.status = :status " +
            "ORDER BY b.createdDate DESC")
    List<BrokerEntity> getUserBrokerList(Long creatorUserId, BrokerStatus status);
}

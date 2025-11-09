package com.stockify.project.repository;

import com.stockify.project.model.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long>, JpaSpecificationExecutor<TransactionEntity> {

    Optional<TransactionEntity> findTopByBrokerIdOrderByCreatedDateDesc(Long brokerId);

    @Query("""
        SELECT t FROM TransactionEntity t
        WHERE t.brokerId IN :brokerIds
        AND t.createdDate = (
            SELECT MAX(t2.createdDate)
            FROM TransactionEntity t2
            WHERE t2.brokerId = t.brokerId
        )
        """)
    List<TransactionEntity> findLatestTransactionsByBrokerIds(List<Long> brokerIds);
}

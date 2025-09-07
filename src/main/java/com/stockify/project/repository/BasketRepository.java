package com.stockify.project.repository;

import com.stockify.project.model.entity.BasketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BasketRepository extends JpaRepository<BasketEntity, Long> {

    List<BasketEntity> findAllByBrokerIdAndTenantIdOrderByCreatedDateAsc(Long brokerId, Long tenantId);

    @Modifying
    @Query("DELETE FROM BasketEntity b WHERE b.createdDate < :date")
    int deleteBasketsByCreatedDateBefore(LocalDateTime date);
}

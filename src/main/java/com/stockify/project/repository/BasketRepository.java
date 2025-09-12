package com.stockify.project.repository;

import com.stockify.project.model.entity.BasketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BasketRepository extends JpaRepository<BasketEntity, Long> {

    List<BasketEntity> findAllByBrokerIdAndTenantIdOrderByCreatedDateAsc(Long brokerId, Long tenantId);

    List<BasketEntity> findAllByCreatedDateBefore(LocalDateTime createdDateBefore);
}

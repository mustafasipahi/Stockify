package com.project.envantra.repository;

import com.project.envantra.model.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long>, JpaSpecificationExecutor<PaymentEntity> {

    List<PaymentEntity> findAllByBrokerIdInAndCreatedDateBetween(Collection<Long> brokerIds, LocalDateTime startDate, LocalDateTime endDate);
}

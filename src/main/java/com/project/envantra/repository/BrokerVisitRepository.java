package com.project.envantra.repository;

import com.project.envantra.model.entity.BrokerVisitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BrokerVisitRepository extends JpaRepository<BrokerVisitEntity, Long> {

    Optional<BrokerVisitEntity> findByBrokerIdAndVisitDateBetween(Long brokerId, LocalDateTime startDate, LocalDateTime endDate);

    List<BrokerVisitEntity> findByBrokerIdInAndVisitDateBetween(List<Long> brokerIds, LocalDateTime startDate, LocalDateTime endDate);

    List<BrokerVisitEntity> findByCreatorUserIdAndVisitDateBetween(Long creatorUserId, LocalDateTime startDate, LocalDateTime endDate);
}

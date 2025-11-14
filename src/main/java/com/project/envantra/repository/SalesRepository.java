package com.project.envantra.repository;

import com.project.envantra.model.entity.SalesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface SalesRepository extends JpaRepository<SalesEntity, Long> {

    List<SalesEntity> findAllByBrokerIdInAndCreatedDateBetween(Collection<Long> brokerIds, LocalDateTime startDate, LocalDateTime endDate);
}

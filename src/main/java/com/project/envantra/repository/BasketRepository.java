package com.project.envantra.repository;

import com.project.envantra.model.entity.BasketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BasketRepository extends JpaRepository<BasketEntity, Long> {

    List<BasketEntity> findAllByBrokerIdOrderByCreatedDateAsc(Long brokerId);

    List<BasketEntity> findAllByCreatedDateBefore(LocalDateTime createdDateBefore);
}

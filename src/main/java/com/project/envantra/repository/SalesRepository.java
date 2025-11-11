package com.project.envantra.repository;

import com.project.envantra.model.entity.SalesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SalesRepository extends JpaRepository<SalesEntity, Long> {

    List<SalesEntity> findAllByBrokerIdIn(Collection<Long> brokerIds);
}

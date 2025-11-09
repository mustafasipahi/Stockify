package com.stockify.project.repository;

import com.stockify.project.model.entity.DocumentEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    List<DocumentEntity> findByBrokerId(Long brokerId, Sort sort);

    List<DocumentEntity> findAllByIdIn(Set<Long> ids);
}

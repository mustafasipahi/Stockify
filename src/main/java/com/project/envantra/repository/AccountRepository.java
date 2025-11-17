package com.project.envantra.repository;

import com.project.envantra.model.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByBrokerId(Long brokerId);

    List<AccountEntity> findAllByBrokerIdIn(List<Long> brokerIds);
}

package com.project.envantra.service;

import com.project.envantra.converter.BrokerVisitConverter;
import com.project.envantra.model.entity.BrokerVisitEntity;
import com.project.envantra.model.request.BrokerVisitRequest;
import com.project.envantra.repository.BrokerVisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.project.envantra.util.DateUtil.getTodayEndDate;
import static com.project.envantra.util.DateUtil.getTodayStartDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrokerVisitPostService {

    private final BrokerVisitRepository brokerVisitRepository;

    public void updateVisitInfo(BrokerVisitRequest request) {
        LocalDateTime startDate = getTodayStartDate(null);
        LocalDateTime endDate = getTodayEndDate(null);
        Optional<BrokerVisitEntity> visitEntity = brokerVisitRepository.findByBrokerIdAndVisitDateBetween(request.getBrokerId(), startDate, endDate);
        BrokerVisitEntity visit;
        if (visitEntity.isPresent()) {
            visit = visitEntity.get();
            visit.setStatus(request.getStatus());
            visit.setNote(request.getNote());
        } else {
            visit = BrokerVisitConverter.toEntity(request);
        }
        brokerVisitRepository.save(visit);
    }
}

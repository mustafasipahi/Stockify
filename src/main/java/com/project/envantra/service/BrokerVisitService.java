package com.project.envantra.service;

import com.project.envantra.converter.BrokerVisitConverter;
import com.project.envantra.model.dto.BrokerDto;
import com.project.envantra.model.dto.BrokerVisitDto;
import com.project.envantra.model.entity.BrokerVisitEntity;
import com.project.envantra.model.request.BrokerVisitRequest;
import com.project.envantra.repository.BrokerVisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.project.envantra.util.DateUtil.getEndDate;
import static com.project.envantra.util.DateUtil.getStartDate;
import static com.project.envantra.util.LoginContext.getUserId;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrokerVisitService {

    private final BrokerVisitRepository brokerVisitRepository;
    private final BrokerGetService brokerGetService;

    public List<BrokerDto> getTodayBrokers() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        return brokerGetService.getAllBrokers().stream()
                .filter(broker -> today.equals(broker.getTargetDayOfWeek()))
                .toList();
    }

    public BrokerVisitDto getVisitByBrokerId(Long brokerId) {
        LocalDateTime startDate = getStartDate(null);
        LocalDateTime endDate = getEndDate(null);
        return brokerVisitRepository.findByBrokerIdAndVisitDateBetween(brokerId, startDate, endDate)
                .map(BrokerVisitConverter::toDto)
                .orElse(new BrokerVisitDto());
    }

    public List<BrokerVisitDto> getVisitsByBrokerIdIn(List<Long> brokerIds) {
        LocalDateTime startDate = getStartDate(null);
        LocalDateTime endDate = getEndDate(null);
        return brokerVisitRepository.findByBrokerIdInAndVisitDateBetween(brokerIds, startDate, endDate).stream()
                .map(BrokerVisitConverter::toDto)
                .toList();
    }

    public List<BrokerVisitDto> getVisitsByDate(LocalDateTime startDate, LocalDateTime endDate) {
        return brokerVisitRepository.findByCreatorUserIdAndVisitDateBetween(getUserId(), startDate, endDate).stream()
                .map(BrokerVisitConverter::toDto)
                .toList();
    }

    public void updateVisit(BrokerVisitRequest request) {
        LocalDateTime startDate = getStartDate(null);
        LocalDateTime endDate = getEndDate(null);
        Optional<BrokerVisitEntity> visitEntity = brokerVisitRepository.findByBrokerIdAndVisitDateBetween(request.getBrokerId(), startDate, endDate);
        if (visitEntity.isPresent()) {
            BrokerVisitEntity visit = visitEntity.get();
            visit.setStatus(request.getStatus());
            visit.setNote(request.getNote());
            brokerVisitRepository.save(visit);
        } else {
            BrokerVisitEntity visit = BrokerVisitConverter.toEntity(request);
            brokerVisitRepository.save(visit);
        }
    }
}

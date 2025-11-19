package com.project.envantra.service;

import com.project.envantra.converter.BrokerVisitConverter;
import com.project.envantra.model.dto.BrokerVisitDto;
import com.project.envantra.repository.BrokerVisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.project.envantra.util.DateUtil.getTodayEndDate;
import static com.project.envantra.util.DateUtil.getTodayStartDate;
import static com.project.envantra.util.LoginContext.getUserId;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrokerVisitGetService {

    private final BrokerVisitRepository brokerVisitRepository;

    public BrokerVisitDto getTodayVisitInfoByBrokerId(Long brokerId) {
        LocalDateTime startDate = getTodayStartDate(null);
        LocalDateTime endDate = getTodayEndDate(null);
        return brokerVisitRepository.findByBrokerIdAndVisitDateBetween(brokerId, startDate, endDate)
                .map(BrokerVisitConverter::toDto)
                .orElse(new BrokerVisitDto());
    }

    public List<BrokerVisitDto> getTodayVisitInfoListByBrokerIdIn(List<Long> brokerIds) {
        LocalDateTime startDate = getTodayStartDate(null);
        LocalDateTime endDate = getTodayEndDate(null);
        return brokerVisitRepository.findByBrokerIdInAndVisitDateBetween(brokerIds, startDate, endDate).stream()
                .map(BrokerVisitConverter::toDto)
                .toList();
    }

    public List<BrokerVisitDto> getVisitInfoListByDate(LocalDateTime startDate, LocalDateTime endDate) {
        return brokerVisitRepository.findByCreatorUserIdAndVisitDateBetween(getUserId(), startDate, endDate).stream()
                .map(BrokerVisitConverter::toDto)
                .toList();
    }
}

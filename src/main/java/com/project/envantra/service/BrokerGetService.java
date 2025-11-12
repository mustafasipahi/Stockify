package com.project.envantra.service;

import com.project.envantra.converter.BrokerConverter;
import com.project.envantra.enums.BrokerStatus;
import com.project.envantra.exception.BrokerNotFoundException;
import com.project.envantra.model.dto.BrokerDto;
import com.project.envantra.model.dto.BrokerVisitDto;
import com.project.envantra.model.entity.BrokerEntity;
import com.project.envantra.model.entity.UserEntity;
import com.project.envantra.repository.BrokerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.project.envantra.util.LoginContext.getUserId;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrokerGetService {

    private final BrokerRepository brokerRepository;
    private final BalanceService balanceService;
    private final UserGetService userGetService;
    private final BrokerVisitService brokerVisitService;

    public BrokerDto getActiveBroker(Long brokerId) {
        BrokerEntity brokerEntity = brokerRepository.findById(brokerId)
                .filter(broker -> broker.getStatus() == BrokerStatus.ACTIVE)
                .orElseThrow(() -> new BrokerNotFoundException(brokerId));
        UserEntity brokerUser = userGetService.findById(brokerEntity.getBrokerUserId());
        BrokerVisitDto visitInfo = brokerVisitService.getVisitInfoByBrokerId(brokerId);
        return BrokerConverter.toDto(brokerEntity, brokerUser, visitInfo, getBrokerCurrentBalance(brokerId));
    }

    public List<BrokerDto> getTodayBrokers() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        return getAllBrokers().stream()
                .filter(broker -> today.equals(broker.getTargetDayOfWeek()))
                .toList();
    }

    public List<BrokerDto> getAllBrokers() {
        return getAllBrokersByStatus(BrokerStatus.ACTIVE);
    }

    public List<BrokerDto> getAllPassiveBrokers() {
        return getAllBrokersByStatus(BrokerStatus.PASSIVE);
    }

    private List<BrokerDto> getAllBrokersByStatus(BrokerStatus status) {
        Long userId = getUserId();
        List<BrokerEntity> userBrokerList = brokerRepository.getUserBrokerList(userId, status);
        List<Long> brokerIds = userBrokerList.stream()
                .map(BrokerEntity::getId)
                .toList();
        List<Long> brokerUserIds = userBrokerList.stream()
                .map(BrokerEntity::getBrokerUserId)
                .distinct()
                .toList();
        Map<Long, BigDecimal> brokerCurrentBalanceMap = getBrokerCurrentBalanceMap(brokerIds);
        Map<Long, UserEntity> brokerUserMap = getBrokerUserMap(brokerUserIds);
        Map<Long, BrokerVisitDto> brokerVisitMap = getBrokerVisitMap(brokerIds);
        return userBrokerList.stream()
                .map(brokerEntity -> {
                    BigDecimal brokerCurrentBalance = brokerCurrentBalanceMap.getOrDefault(brokerEntity.getId(), BigDecimal.ZERO);
                    UserEntity brokerUser = brokerUserMap.getOrDefault(brokerEntity.getBrokerUserId(), new UserEntity());
                    BrokerVisitDto visitInfo = brokerVisitMap.getOrDefault(brokerEntity.getId(), new BrokerVisitDto());
                    return BrokerConverter.toDto(brokerEntity, brokerUser, visitInfo, brokerCurrentBalance);
                })
                .toList();
    }

    private BigDecimal getBrokerCurrentBalance(Long brokerId) {
        return balanceService.getBrokerCurrentBalance(brokerId);
    }

    private Map<Long, BigDecimal> getBrokerCurrentBalanceMap(List<Long> brokerIds) {
        return balanceService.getBrokerCurrentBalanceMap(brokerIds);
    }

    private Map<Long, UserEntity> getBrokerUserMap(List<Long> brokerUserIds) {
        return userGetService.findAllByIdIn(brokerUserIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, userEntity -> userEntity));
    }

    private Map<Long, BrokerVisitDto> getBrokerVisitMap(List<Long> brokerUserIds) {
        return brokerVisitService.getVisitInfoListByBrokerIdIn(brokerUserIds).stream()
                .collect(Collectors.toMap(BrokerVisitDto::getBrokerId, visitInfo -> visitInfo));
    }
}

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
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.project.envantra.util.DateUtil.isAfter;
import static com.project.envantra.util.LoginContext.getUserId;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrokerGetService {

    private final BrokerRepository brokerRepository;
    private final AccountService accountService;
    private final UserGetService userGetService;
    private final BrokerVisitGetService brokerVisitGetService;

    public BrokerDto getActiveBroker(Long brokerId) {
        BrokerEntity brokerEntity = brokerRepository.findById(brokerId)
                .filter(broker -> broker.getStatus() == BrokerStatus.ACTIVE)
                .orElseThrow(() -> new BrokerNotFoundException(brokerId));
        UserEntity brokerUser = userGetService.findById(brokerEntity.getBrokerUserId());
        BrokerVisitDto visitInfo = brokerVisitGetService.getTodayVisitInfoByBrokerId(brokerId);
        return BrokerConverter.toDto(brokerEntity, brokerUser, visitInfo, getBrokerCurrentBalance(brokerId));
    }

    public List<BrokerDto> getActiveBrokers(List<Long> brokerIds) {
        List<BrokerEntity> brokerList = brokerRepository.findAllById(brokerIds).stream()
                .filter(broker -> broker.getStatus() == BrokerStatus.ACTIVE)
                .toList();
        Map<Long, UserEntity> brokerUserMap = getBrokerUserMap(brokerIds);
        Map<Long, BrokerVisitDto> brokerVisitMap = getBrokerVisitMap(brokerIds);
        Map<Long, BigDecimal> brokerCurrentBalanceMap = getBrokerCurrentBalanceMap(brokerIds);
        return brokerList.stream()
                .map(broker -> {
                    UserEntity brokerUser = brokerUserMap.getOrDefault(broker.getBrokerUserId(), new UserEntity());
                    BrokerVisitDto visitInfo = brokerVisitMap.getOrDefault(broker.getBrokerUserId(), new BrokerVisitDto());
                    BigDecimal balance = brokerCurrentBalanceMap.getOrDefault(broker.getBrokerUserId(), BigDecimal.ZERO);
                    return BrokerConverter.toDto(broker, brokerUser, visitInfo, balance);
                }).toList();
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

    public Map<Long, BrokerDto> getBrokerMap(List<Long> brokerIds) {
        if (CollectionUtils.isNotEmpty(brokerIds)) {
            Long userId = getUserId();
            return getActiveBrokers(brokerIds).stream()
                    .filter(activeBroker -> Objects.equals(activeBroker.getCreatorUserId(), userId))
                    .collect(Collectors.toMap(BrokerDto::getBrokerId, brokerDto -> brokerDto));
        } else {
            List<BrokerDto> activeBrokers = getAllBrokers();
            return activeBrokers.stream()
                    .collect(Collectors.toMap(BrokerDto::getBrokerId, broker -> broker));
        }
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
        return accountService.getBalance(brokerId);
    }

    private Map<Long, BigDecimal> getBrokerCurrentBalanceMap(List<Long> brokerIds) {
        return accountService.getBalanceMap(brokerIds);
    }

    private Map<Long, UserEntity> getBrokerUserMap(List<Long> brokerUserIds) {
        return userGetService.findAllByIdIn(brokerUserIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, userEntity -> userEntity));
    }

    private Map<Long, BrokerVisitDto> getBrokerVisitMap(List<Long> brokerUserIds) {
        return brokerVisitGetService.getTodayVisitInfoListByBrokerIdIn(brokerUserIds)
                .stream()
                .collect(Collectors.toMap(
                        BrokerVisitDto::getCreatorUserId,
                        visit -> visit,
                        (v1, v2) -> isAfter(v1.getVisitDate(), v2.getVisitDate()) ? v1 : v2
                ));
    }
}

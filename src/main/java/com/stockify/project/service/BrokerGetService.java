package com.stockify.project.service;

import com.stockify.project.converter.BrokerConverter;
import com.stockify.project.enums.BrokerStatus;
import com.stockify.project.exception.BrokerNotFoundException;
import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.entity.BrokerEntity;
import com.stockify.project.model.entity.UserEntity;
import com.stockify.project.repository.BrokerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.stockify.project.util.LoginContext.getUserId;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrokerGetService {

    private final BrokerRepository brokerRepository;
    private final BalanceService balanceService;
    private final UserGetService userGetService;

    public BrokerDto getActiveBroker(Long brokerId) {
        BrokerEntity brokerEntity = brokerRepository.findById(brokerId)
                .filter(broker -> broker.getStatus() == BrokerStatus.ACTIVE)
                .orElseThrow(() -> new BrokerNotFoundException(brokerId));
        UserEntity brokerUser = userGetService.findById(brokerEntity.getBrokerUserId());
        return BrokerConverter.toDto(brokerEntity, brokerUser, getBrokerCurrentBalance(brokerId));
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
        return userBrokerList.stream()
                .map(brokerEntity -> {
                    BigDecimal brokerCurrentBalance = brokerCurrentBalanceMap.getOrDefault(brokerEntity.getId(), BigDecimal.ZERO);
                    UserEntity brokerUser = brokerUserMap.getOrDefault(brokerEntity.getBrokerUserId(), new UserEntity());
                    return BrokerConverter.toDto(brokerEntity, brokerUser, brokerCurrentBalance);
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
}

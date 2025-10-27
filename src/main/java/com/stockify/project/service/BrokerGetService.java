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

import static com.stockify.project.util.TenantContext.getTenantId;
import static com.stockify.project.util.TenantContext.getUserId;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrokerGetService {

    private final BrokerRepository brokerRepository;
    private final TransactionGetService transactionGetService;
    private final UserGetService userGetService;

    public BrokerDto info(Long brokerId) {
        Long tenantId = getTenantId();
        BrokerEntity broker = brokerRepository.findByIdAndTenantId(brokerId, tenantId)
                .orElseThrow(() -> new BrokerNotFoundException(brokerId));
        UserEntity brokerUser = userGetService.findById(broker.getBrokerUserId());
        return BrokerConverter.toDto(broker, brokerUser, null);
    }

    public BrokerDto detail(Long brokerId) {
        Long tenantId = getTenantId();
        BigDecimal brokerCurrentBalance = getBrokerCurrentBalance(brokerId, tenantId);
        BrokerEntity broker = brokerRepository.findByIdAndTenantId(brokerId, tenantId)
                .orElseThrow(() -> new BrokerNotFoundException(brokerId));
        UserEntity brokerUser = userGetService.findById(broker.getBrokerUserId());
        return BrokerConverter.toDto(broker, brokerUser, brokerCurrentBalance);
    }

    public List<BrokerDto> getAllBrokers() {
        Long userId = getUserId();
        Long tenantId = getTenantId();
        List<BrokerEntity> userBrokerList = brokerRepository.getUserBrokerList(userId, BrokerStatus.ACTIVE, tenantId);
        List<Long> brokerIds = userBrokerList.stream()
                .map(BrokerEntity::getId)
                .toList();
        List<Long> brokerUserIds = userBrokerList.stream()
                .map(BrokerEntity::getBrokerUserId)
                .distinct()
                .toList();
        Map<Long, BigDecimal> brokerCurrentBalanceMap = getBrokerCurrentBalanceMap(brokerIds, tenantId);
        Map<Long, UserEntity> brokerUserMap = getBrokerUserMap(brokerUserIds);
        return userBrokerList.stream()
                .map(brokerEntity -> {
                    BigDecimal brokerCurrentBalance = brokerCurrentBalanceMap.getOrDefault(brokerEntity.getId(), BigDecimal.ZERO);
                    UserEntity brokerUser = brokerUserMap.getOrDefault(brokerEntity.getId(), new UserEntity());
                    return BrokerConverter.toDto(brokerEntity, brokerUser, brokerCurrentBalance);
                })
                .toList();
    }

    private BigDecimal getBrokerCurrentBalance(Long brokerId, Long tenantId) {
        return transactionGetService.getBrokerCurrentBalance(brokerId, tenantId);
    }

    private Map<Long, BigDecimal> getBrokerCurrentBalanceMap(List<Long> brokerIds, Long tenantId) {
        return transactionGetService.getBrokerCurrentBalanceMap(brokerIds, tenantId);
    }

    private Map<Long, UserEntity> getBrokerUserMap(List<Long> brokerUserIds) {
        return userGetService.findAllByIdIn(brokerUserIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, userEntity -> userEntity));
    }
}

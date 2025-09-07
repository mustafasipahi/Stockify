package com.stockify.project.service;

import com.stockify.project.converter.BrokerConverter;
import com.stockify.project.enums.BrokerStatus;
import com.stockify.project.exception.BrokerNotFoundException;
import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.repository.BrokerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.stockify.project.util.TenantContext.getTenantId;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrokerGetService {

    private final BrokerRepository brokerRepository;
    private final TransactionGetService transactionGetService;

    public BrokerDto detail(Long brokerId) {
        Long tenantId = getTenantId();
        BigDecimal brokerCurrentBalance = getBrokerCurrentBalance(brokerId, tenantId);
        return brokerRepository.findByIdAndTenantId(brokerId, tenantId)
                .map(brokerEntity -> BrokerConverter.toDto(brokerEntity, brokerCurrentBalance))
                .orElseThrow(() -> new BrokerNotFoundException(brokerId));
    }

    public List<BrokerDto> getAllBrokers() {
        Long tenantId = getTenantId();
        return brokerRepository.findAllByStatusAndTenantIdOrderByCreatedDateDesc(BrokerStatus.ACTIVE, tenantId).stream()
                .map(brokerEntity -> {
                    BigDecimal brokerCurrentBalance = getBrokerCurrentBalance(brokerEntity.getId(), tenantId);
                    return BrokerConverter.toDto(brokerEntity, brokerCurrentBalance);
                })
                .toList();
    }

    private BigDecimal getBrokerCurrentBalance(Long brokerId, Long tenantId) {
        return transactionGetService.getBrokerCurrentBalance(brokerId, tenantId);
    }
}

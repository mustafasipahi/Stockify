package com.stockify.project.service;

import com.stockify.project.converter.BrokerConverter;
import com.stockify.project.enums.BrokerStatus;
import com.stockify.project.exception.BrokerDiscountRateException;
import com.stockify.project.exception.BrokerIdException;
import com.stockify.project.exception.BrokerNotFoundException;
import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.entity.BrokerEntity;
import com.stockify.project.model.request.BrokerCreateRequest;
import com.stockify.project.model.request.BrokerUpdateRequest;
import com.stockify.project.model.request.DiscountUpdateRequest;
import com.stockify.project.repository.BrokerRepository;
import com.stockify.project.validator.BrokerCreateValidator;
import com.stockify.project.validator.BrokerUpdateValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.stockify.project.constant.CacheConstants.BROKER_DETAIL;
import static com.stockify.project.util.TenantContext.getTenantId;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrokerService {

    private final BrokerRepository brokerRepository;
    private final BrokerCreateValidator brokerCreateValidator;
    private final BrokerUpdateValidator brokerUpdateValidator;
    private final BalanceService balanceService;

    @Transactional
    public BrokerDto save(BrokerCreateRequest request) {
        brokerCreateValidator.validate(request);
        BrokerEntity brokerEntity = BrokerEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .discountRate(Optional.ofNullable(request.getDiscountRate())
                        .orElse(BigDecimal.ZERO))
                .status(BrokerStatus.ACTIVE)
                .tenantId(getTenantId())
                .build();
        BrokerEntity savedBrokerEntity = brokerRepository.save(brokerEntity);
        return BrokerConverter.toIdDto(savedBrokerEntity);
    }

    @Transactional
    @CacheEvict(value = BROKER_DETAIL, key = "#request.brokerId")
    public BrokerDto update(BrokerUpdateRequest request) {
        if (request.getBrokerId() == null) {
            throw new BrokerIdException();
        }
        BrokerEntity brokerEntity = brokerRepository.findByIdAndTenantId(request.getBrokerId(), getTenantId())
                .orElseThrow(() -> new BrokerNotFoundException(request.getBrokerId()));
        if (StringUtils.isNotBlank(request.getFirstName()) && StringUtils.isNotBlank(request.getLastName())) {
            brokerUpdateValidator.validateFirstNameAndLastName(request.getFirstName(), request.getLastName());
            brokerEntity.setFirstName(request.getFirstName());
            brokerEntity.setLastName(request.getLastName());
        } else if (StringUtils.isNotBlank(request.getFirstName())) {
            brokerUpdateValidator.validateFirstName(request.getFirstName(), brokerEntity.getLastName());
            brokerEntity.setFirstName(request.getFirstName());
        } else if (StringUtils.isNotBlank(request.getLastName())) {
            brokerUpdateValidator.validateLastName(brokerEntity.getFirstName(), request.getLastName());
            brokerEntity.setLastName(request.getLastName());
        }
        if (request.getDiscountRate() != null) {
            brokerUpdateValidator.validateDiscountRate(request.getDiscountRate());
            brokerEntity.setDiscountRate(request.getDiscountRate());
        }
        BrokerEntity updatedBrokerEntity = brokerRepository.save(brokerEntity);
        return BrokerConverter.toIdDto(updatedBrokerEntity);
    }

    @Transactional
    @CacheEvict(value = BROKER_DETAIL, key = "#brokerId")
    public void evictBrokerCache(Long brokerId) {
        log.info("Evicted broker id {}", brokerId);
    }

    @Transactional
    @CacheEvict(value = BROKER_DETAIL, key = "#request.brokerId")
    public void updateDiscountRate(DiscountUpdateRequest request) {
        if (request.getBrokerId() == null) {
            throw new BrokerIdException();
        }
        if (request.getDiscountRate() == null) {
            throw new BrokerDiscountRateException();
        }
        if (request.getDiscountRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new BrokerDiscountRateException();
        }
        BrokerEntity brokerEntity = brokerRepository.findByIdAndTenantId(request.getBrokerId(), getTenantId())
                .orElseThrow(() -> new BrokerNotFoundException(request.getBrokerId()));
        brokerEntity.setDiscountRate(request.getDiscountRate());
        brokerRepository.save(brokerEntity);
    }

    @Transactional
    @CacheEvict(value = BROKER_DETAIL, key = "#brokerId")
    public BrokerDto delete(Long brokerId) {
        BrokerEntity brokerEntity = brokerRepository.findByIdAndTenantId(brokerId, getTenantId())
                .orElseThrow(() -> new BrokerNotFoundException(brokerId));
        brokerEntity.setStatus(BrokerStatus.PASSIVE);
        BrokerEntity deletedBrokerEntity = brokerRepository.save(brokerEntity);
        return BrokerConverter.toIdDto(deletedBrokerEntity);
    }

    @Cacheable(value = BROKER_DETAIL, key = "#brokerId")
    public BrokerDto detail(Long brokerId) {
        Long tenantId = getTenantId();
        BigDecimal brokerCurrentBalance = getBrokerCurrentBalance(brokerId, tenantId);
        return brokerRepository.findByIdAndTenantId(brokerId, tenantId)
                .map(brokerEntity -> BrokerConverter.toDto(brokerEntity, brokerCurrentBalance))
                .orElseThrow(() -> new BrokerNotFoundException(brokerId));
    }

    public List<BrokerDto> getAllBrokers() {
        Long tenantId = getTenantId();
        return brokerRepository.findAllByStatusAndTenantIdOrderByFirstNameAsc(BrokerStatus.ACTIVE, tenantId).stream()
                .map(brokerEntity -> {
                    BigDecimal brokerCurrentBalance = getBrokerCurrentBalance(brokerEntity.getId(), tenantId);
                    return BrokerConverter.toDto(brokerEntity, brokerCurrentBalance);
                })
                .toList();
    }

    private BigDecimal getBrokerCurrentBalance(Long brokerId, Long tenantId) {
        return balanceService.getBrokerCurrentBalance(brokerId, tenantId);
    }
}

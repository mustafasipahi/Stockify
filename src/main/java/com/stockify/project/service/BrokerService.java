package com.stockify.project.service;

import com.stockify.project.converter.BrokerConverter;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.stockify.project.util.TenantContext.getTenantId;

@Service
@RequiredArgsConstructor
public class BrokerService {

    private final BrokerRepository brokerRepository;
    private final BrokerCreateValidator brokerCreateValidator;
    private final BrokerUpdateValidator brokerUpdateValidator;

    @Transactional
    public BrokerDto save(BrokerCreateRequest request) {
        brokerCreateValidator.validate(request);
        BrokerEntity brokerEntity = BrokerEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .discountRate(request.getDiscountRate())
                .tenantId(getTenantId())
                .build();
        BrokerEntity savedBrokerEntity = brokerRepository.save(brokerEntity);
        return BrokerConverter.toIdDto(savedBrokerEntity);
    }

    @Transactional
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

    public void updateDiscount(DiscountUpdateRequest request) {
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

    public BrokerDto detail(Long brokerId) {
        return brokerRepository.findByIdAndTenantId(brokerId, getTenantId())
                .map(BrokerConverter::toDto)
                .orElseThrow(() -> new BrokerNotFoundException(brokerId));
    }

    public List<BrokerDto> getAllBrokers() {
        return brokerRepository.findAllByTenantIdOrderByFirstNameAsc(getTenantId()).stream()
                .map(BrokerConverter::toDto)
                .toList();
    }
}

package com.stockify.project.validator;

import com.stockify.project.exception.BrokerDiscountRateException;
import com.stockify.project.exception.BrokerNameException;
import com.stockify.project.model.entity.BrokerEntity;
import com.stockify.project.model.request.BrokerCreateRequest;
import com.stockify.project.repository.BrokerRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

import static com.stockify.project.util.TenantContext.getTenantId;

@Component
@AllArgsConstructor
public class BrokerCreateValidator {

    private final BrokerRepository brokerRepository;

    public void validate(BrokerCreateRequest request) {
        validateName(request);
        validateDiscountRate(request.getDiscountRate());
    }

    private void validateName(BrokerCreateRequest request) {
        if (StringUtils.isBlank(request.getFirstName())) {
            throw new BrokerNameException("Broker First Name Required!");
        }
        if (StringUtils.isBlank(request.getLastName())) {
            throw new BrokerNameException("Broker First Name Required!");
        }
        Optional<BrokerEntity> alreadyUsed = brokerRepository.findByFirstNameAndLastNameAndTenantId(
                request.getFirstName(),
                request.getLastName(),
                getTenantId());
        if (alreadyUsed.isPresent()) {
            throw new BrokerNameException("Broker Name Already Used!");
        }
    }

    private void validateDiscountRate(BigDecimal discountRate) {
        if (discountRate == null) {
            return;
        }
        if (discountRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new BrokerDiscountRateException();
        }
    }
}

package com.stockify.project.validator;

import com.stockify.project.exception.BrokerDiscountRateException;
import com.stockify.project.exception.BrokerEmailException;
import com.stockify.project.exception.BrokerNameException;
import com.stockify.project.exception.BrokerVknException;
import com.stockify.project.model.request.BrokerCreateRequest;
import com.stockify.project.service.UserGetService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@AllArgsConstructor
public class BrokerCreateValidator {

    private final UserGetService userGetService;

    public void validate(BrokerCreateRequest request) {
        validateName(request);
        validateEmail(request.getEmail());
        validateVkn(request.getVkn());
        validateDiscountRate(request.getDiscountRate());
    }

    private void validateName(BrokerCreateRequest request) {
        if (StringUtils.isBlank(request.getFirstName())) {
            throw new BrokerNameException("Broker First Name Required!");
        }
        if (StringUtils.isBlank(request.getLastName())) {
            throw new BrokerNameException("Broker First Name Required!");
        }
        if (alreadyUsed(request.getFirstName(), request.getLastName())) {
            throw new BrokerNameException("Broker Name Already Used!");
        }
    }

    private void validateEmail(String email) {
        if (StringUtils.isBlank(email)) {
            throw new BrokerEmailException();
        }
    }

    private void validateVkn(String vkn) {
        if (StringUtils.isBlank(vkn)) {
            throw new BrokerVknException();
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

    private boolean alreadyUsed(String firstName, String lastName) {
        return userGetService.findByFirstNameAndLastNameAndTenantId(firstName, lastName)
                .isPresent();
    }
}

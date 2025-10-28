package com.stockify.project.validator;

import com.stockify.project.exception.*;
import com.stockify.project.model.request.BrokerCreateRequest;
import com.stockify.project.service.UserGetService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.stockify.project.util.DateUtil.getLocalDate;

@Component
@AllArgsConstructor
public class BrokerCreateValidator {

    private final UserGetService userGetService;

    public void validate(BrokerCreateRequest request) {
        validateName(request);
        validateEmail(request.getEmail());
        validateTkn(request);
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

    private void validateTkn(BrokerCreateRequest request) {
        boolean isValidTkn = false;
        try {
            isValidTkn = TknValidator.validateTkn(
                    request.getTkn(),
                    request.getFirstName(),
                    request.getLastName(),
                    getLocalDate(request.getBirthDate()).getYear());
        } catch (Exception e) {
            throw new BrokerTknException();
        }
        if (!isValidTkn) {
            throw new BrokerTknException();
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
        return userGetService.findByFirstNameAndLastName(firstName, lastName)
                .isPresent();
    }
}

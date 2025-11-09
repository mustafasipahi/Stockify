package com.project.envantra.validator;

import com.project.envantra.exception.*;
import com.project.envantra.model.request.BrokerCreateRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrokerCreateValidator {

    public static void validate(BrokerCreateRequest request) {
        validateName(request);
        validateEmail(request.getEmail());
        validateTkn(request);
        validateVkn(request.getVkn());
        validateDiscountRate(request.getDiscountRate());
    }

    private static void validateName(BrokerCreateRequest request) {
        if (StringUtils.isBlank(request.getFirstName())) {
            throw new BrokerNameException("Broker First Name Required!");
        }
        if (StringUtils.isBlank(request.getLastName())) {
            throw new BrokerNameException("Broker First Name Required!");
        }
    }

    private static void validateEmail(String email) {
        if (StringUtils.isBlank(email)) {
            throw new BrokerEmailException();
        }
    }

    private static void validateTkn(BrokerCreateRequest request) {
        if (StringUtils.isBlank(request.getTkn())) {
            throw new BrokerTknException();
        }
    }

    private static void validateVkn(String vkn) {
        if (StringUtils.isBlank(vkn)) {
            throw new BrokerVknException();
        }
    }

    private static void validateDiscountRate(BigDecimal discountRate) {
        if (discountRate == null) {
            return;
        }
        if (discountRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new BrokerDiscountRateException();
        }
    }
}

package com.stockify.project.validator;

import com.stockify.project.exception.BrokerDiscountRateException;
import com.stockify.project.exception.BrokerNameException;
import com.stockify.project.service.UserGetService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@AllArgsConstructor
public class BrokerUpdateValidator {

    private final UserGetService userGetService;

    public void validateFirstNameAndLastName(Long brokerId, String firstName, String lastName) {
        if (alreadyUsed(brokerId, firstName, lastName)) {
            throw new BrokerNameException("Broker Name Already Used!");
        }
    }

    public void validateFirstName(Long brokerId, String firstName, String lastName) {
        if (alreadyUsed(brokerId, firstName, lastName)) {
            throw new BrokerNameException("Broker First Name Already Used!");
        }
    }

    public void validateLastName(Long brokerId, String firstName, String lastName) {
        if (alreadyUsed(brokerId, firstName, lastName)) {
            throw new BrokerNameException("Broker Last Name Already Used!");
        }
    }

    public void validateDiscountRate(BigDecimal discountRate) {
        if (discountRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new BrokerDiscountRateException();
        }
    }

    private boolean alreadyUsed(Long brokerId, String firstName, String lastName) {
        return userGetService.findByFirstNameAndLastName(firstName, lastName)
                .filter(broker -> !broker.getId().equals(brokerId))
                .isPresent();
    }
}

package com.stockify.project.validator;

import com.stockify.project.exception.BrokerDiscountRateException;
import com.stockify.project.exception.BrokerNameException;
import com.stockify.project.repository.BrokerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.stockify.project.util.TenantContext.getTenantId;

@Component
@AllArgsConstructor
public class BrokerUpdateValidator {

    private final BrokerRepository brokerRepository;

    public void validateFirstNameAndLastName(String firstName, String lastName) {
        if (alreadyUsed(firstName, lastName)) {
            throw new BrokerNameException("Broker Name Already Used!");
        }
    }

    public void validateFirstName(String firstName, String lastName) {
        if (alreadyUsed(firstName, lastName)) {
            throw new BrokerNameException("Broker First Name Already Used!");
        }
    }

    public void validateLastName(String firstName, String lastName) {
        if (alreadyUsed(firstName, lastName)) {
            throw new BrokerNameException("Broker Last Name Already Used!");
        }
    }

    public void validateDiscountRate(BigDecimal discountRate) {
        if (discountRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new BrokerDiscountRateException();
        }
    }

    private boolean alreadyUsed(String firstName, String lastName) {
        return brokerRepository.findByFirstNameAndLastNameAndTenantId(firstName, lastName, getTenantId())
                .isPresent();
    }
}

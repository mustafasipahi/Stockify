package com.project.envantra.validator;

import com.project.envantra.exception.BasketEmptyException;
import com.project.envantra.exception.BrokerIdException;
import com.project.envantra.model.dto.BasketDto;
import com.project.envantra.model.request.SalesRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SalesValidator {

    public static void validate(SalesRequest request) {
        if (request.getBrokerId() == null) {
            throw new BrokerIdException();
        }
    }

    public static void validate(Long brokerId) {
        if (brokerId == null) {
            throw new BrokerIdException();
        }
    }

    public static void validateBasket(List<BasketDto> basket) {
        if (CollectionUtils.isEmpty(basket)) {
            throw new BasketEmptyException();
        }
    }
}

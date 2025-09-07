package com.stockify.project.validator;

import com.stockify.project.exception.BasketEmptyException;
import com.stockify.project.exception.BrokerIdException;
import com.stockify.project.model.dto.BasketDto;
import com.stockify.project.model.request.SalesRequest;
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

    public static void validateBasket(List<BasketDto> basket) {
        if (CollectionUtils.isEmpty(basket)) {
            throw new BasketEmptyException();
        }
    }
}

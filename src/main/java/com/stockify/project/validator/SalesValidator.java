package com.stockify.project.validator;

import com.stockify.project.exception.BrokerIdException;
import com.stockify.project.exception.ProductNotFoundException;
import com.stockify.project.exception.SalesValidationException;
import com.stockify.project.model.request.SalesProductRequest;
import com.stockify.project.model.request.SalesRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SalesValidator {

    public static void validate(SalesRequest request) {
        if (request.getBrokerId() == null) {
            throw new BrokerIdException();
        }
        if (CollectionUtils.isEmpty(request.getProducts())) {
            throw new ProductNotFoundException();
        }
        Set<Long> productIdSet = new HashSet<>();
        for (SalesProductRequest product : request.getProducts()) {
            if (!productIdSet.add(product.getProductId())) {
                throw new SalesValidationException(product.getProductId());
            }
        }
    }
}

package com.project.envantra.service;

import com.project.envantra.model.dto.BasketDto;
import com.project.envantra.model.dto.SalesProductDto;
import com.project.envantra.model.entity.BasketEntity;
import com.project.envantra.model.request.BasketAddRequest;
import com.project.envantra.model.request.BasketRemoveRequest;
import com.project.envantra.repository.BasketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.project.envantra.converter.BasketConverter.toEntity;
import static com.project.envantra.converter.SalesItemConverter.validateAndPrepareProducts;

@Service
@RequiredArgsConstructor
public class BasketPostService {

    private final BasketRepository basketRepository;
    private final BasketGetService basketGetService;
    private final BrokerGetService brokerGetService;
    private final InventoryGetService inventoryGetService;

    @Transactional
    public void addToBasket(BasketAddRequest request) {
        validateBroker(request.getBrokerId());
        validateProduct(request.getProductId(), request.getProductCount());
        List<BasketEntity> brokerBasket = basketGetService.getBrokerBasket(request.getBrokerId());
        boolean exists = exists(brokerBasket, request.getProductId());
        if (exists) {
            BasketEntity basketEntity = get(brokerBasket, request.getProductId());
            assert basketEntity != null;
            if (request.getProductCount() <= 0) {
                basketRepository.delete(basketEntity);
            } else {
                basketEntity.setProductCount(request.getProductCount());
                basketRepository.save(basketEntity);
            }
        } else {
            basketRepository.save(toEntity(request));
        }
    }

    @Transactional
    public void removeToBasket(BasketRemoveRequest request) {
        validateBroker(request.getBrokerId());
        List<BasketEntity> brokerBasket = basketGetService.getBrokerBasket(request.getBrokerId());
        boolean exists = exists(brokerBasket, request.getProductId());
        if (exists) {
            BasketEntity basketEntity = get(brokerBasket, request.getProductId());
            assert basketEntity != null;
            basketRepository.delete(basketEntity);
        }
    }

    @Transactional
    public void clearBasket(Long brokerId) {
        validateBroker(brokerId);
        List<BasketEntity> brokerBasket = basketGetService.getBrokerBasket(brokerId);
        basketRepository.deleteAll(brokerBasket);
    }

    private void validateBroker(Long brokerId) {
        brokerGetService.getActiveBroker(brokerId);
    }

    private void validateProduct(Long productId, Integer productCount) {
        BasketDto basketDto = new BasketDto();
        basketDto.setProductId(productId);
        basketDto.setProductCount(productCount);
        List<SalesProductDto> products = inventoryGetService.getSalesInventory();
        validateAndPrepareProducts(List.of(basketDto), products, true, BigDecimal.ZERO);
    }

    private boolean exists(List<BasketEntity> brokerBasket, Long productId) {
        return brokerBasket.stream().anyMatch(basket -> basket.getProductId().equals(productId));
    }

    private BasketEntity get(List<BasketEntity> brokerBasket, Long productId) {
        return brokerBasket.stream()
                .filter(basket -> basket.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
    }
}

package com.stockify.project.service;

import com.stockify.project.model.entity.BasketEntity;
import com.stockify.project.model.request.BasketAddRequest;
import com.stockify.project.model.request.BasketRemoveRequest;
import com.stockify.project.repository.BasketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.stockify.project.converter.BasketConverter.toEntity;

@Service
@RequiredArgsConstructor
public class BasketPostService {

    private final BasketRepository basketRepository;
    private final BasketGetService basketGetService;

    @Transactional
    public void addToBasket(BasketAddRequest request) {
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
        List<BasketEntity> brokerBasket = basketGetService.getBrokerBasket(brokerId);
        basketRepository.deleteAll(brokerBasket);
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

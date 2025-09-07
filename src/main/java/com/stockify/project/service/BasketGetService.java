package com.stockify.project.service;

import com.stockify.project.converter.BasketConverter;
import com.stockify.project.model.dto.BasketDto;
import com.stockify.project.model.entity.BasketEntity;
import com.stockify.project.repository.BasketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.stockify.project.util.TenantContext.getTenantId;

@Service
@RequiredArgsConstructor
public class BasketGetService {

    private final BasketRepository basketRepository;

    public List<BasketEntity> getBrokerBasket(Long brokerId) {
        return basketRepository.findAllByBrokerIdAndTenantIdOrderByCreatedDateAsc(brokerId, getTenantId());
    }

    public List<BasketDto> getBrokerAllBasket(Long brokerId) {
        return basketRepository.findAllByBrokerIdAndTenantIdOrderByCreatedDateAsc(brokerId, getTenantId()).stream()
                .map(BasketConverter::toDto)
                .toList();
    }
}

package com.stockify.project.service;

import com.stockify.project.converter.BasketConverter;
import com.stockify.project.model.dto.BasketDto;
import com.stockify.project.model.entity.BasketEntity;
import com.stockify.project.repository.BasketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BasketGetService {

    private final BasketRepository basketRepository;

    public List<BasketEntity> getBrokerBasket(Long brokerId) {
        return basketRepository.findAllByBrokerIdOrderByCreatedDateAsc(brokerId);
    }

    public List<BasketDto> getBrokerAllBasket(Long brokerId) {
        return basketRepository.findAllByBrokerIdOrderByCreatedDateAsc(brokerId).stream()
                .map(BasketConverter::toDto)
                .toList();
    }
}

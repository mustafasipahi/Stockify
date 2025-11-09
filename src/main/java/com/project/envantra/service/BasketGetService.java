package com.project.envantra.service;

import com.project.envantra.converter.BasketConverter;
import com.project.envantra.model.dto.BasketDto;
import com.project.envantra.model.entity.BasketEntity;
import com.project.envantra.repository.BasketRepository;
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

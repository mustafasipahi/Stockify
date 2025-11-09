package com.project.envantra.service;

import com.project.envantra.model.entity.BasketEntity;
import com.project.envantra.repository.BasketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final BasketRepository basketRepository;

    public void removeUnusedBasket() {
        LocalDateTime now = LocalDateTime.now().minusDays(1);
        List<BasketEntity> basketEntityList = basketRepository.findAllByCreatedDateBefore(now.minusDays(1));
        basketEntityList.forEach(entity -> basketRepository.deleteById(entity.getId()));
        log.info("Deleted unused response count: {}", basketEntityList.size());
    }
}

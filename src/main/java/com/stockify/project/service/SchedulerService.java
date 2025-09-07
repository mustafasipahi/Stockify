package com.stockify.project.service;

import com.stockify.project.repository.BasketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final BasketRepository basketRepository;

    public void removeUnusedBasket() {
        LocalDateTime now = LocalDateTime.now().minusDays(1);
        int deletedResponseCount = basketRepository.deleteBasketsByCreatedDateBefore(now);
        log.info("Deleted unused response count: {}", deletedResponseCount);
    }
}

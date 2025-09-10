package com.stockify.project.scheduler;

import com.stockify.project.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final SchedulerService schedulerService;

    //@Scheduled(cron = "0 0 9 * * ?")
    @Scheduled(cron = "0 */1 * * * ?")
    public void removeUnusedBasket() {
        log.info("removeUnusedBasket starting...");
        schedulerService.removeUnusedBasket();
        log.info("removeUnusedBasket ended.");
    }
}

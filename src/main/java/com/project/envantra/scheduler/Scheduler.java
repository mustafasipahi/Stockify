package com.project.envantra.scheduler;

import com.project.envantra.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final SchedulerService schedulerService;

    @Scheduled(cron = "0 0 9 * * ?")
    public void removeUnusedBasket() {
        log.info("removeUnusedBasket starting...");
        schedulerService.removeUnusedBasket();
        log.info("removeUnusedBasket ended.");
    }
}

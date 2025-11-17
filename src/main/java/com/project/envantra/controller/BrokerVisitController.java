package com.project.envantra.controller;

import com.project.envantra.model.request.BrokerVisitRequest;
import com.project.envantra.service.BrokerVisitPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/broker-visits")
@RequiredArgsConstructor
public class BrokerVisitController {

    private final BrokerVisitPostService brokerVisitPostService;

    @PutMapping("/update")
    public void updateVisit(@RequestBody BrokerVisitRequest request) {
        brokerVisitPostService.updateVisitInfo(request);
    }
}

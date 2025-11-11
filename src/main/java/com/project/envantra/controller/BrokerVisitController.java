package com.project.envantra.controller;

import com.project.envantra.model.dto.BrokerDto;
import com.project.envantra.model.request.BrokerVisitRequest;
import com.project.envantra.service.BrokerVisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/broker-visits")
@RequiredArgsConstructor
public class BrokerVisitController {

    private final BrokerVisitService brokerVisitService;



    @PutMapping("/update")
    public void updateVisit(@RequestBody BrokerVisitRequest request) {
        brokerVisitService.updateVisitInfo(request);
    }
}

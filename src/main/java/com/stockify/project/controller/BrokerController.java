package com.stockify.project.controller;

import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.request.BrokerCreateRequest;
import com.stockify.project.model.request.BrokerUpdateRequest;
import com.stockify.project.model.request.DiscountUpdateRequest;
import com.stockify.project.service.BrokerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/broker")
public class BrokerController {

    private final BrokerService brokerService;

    @PostMapping("/save")
    public BrokerDto save(@RequestBody BrokerCreateRequest request) {
        return brokerService.save(request);
    }

    @PutMapping("/update")
    public BrokerDto update(@RequestBody BrokerUpdateRequest request) {
        return brokerService.update(request);
    }

    @PutMapping("/update/discount-rate")
    public void updateDiscount(@RequestBody DiscountUpdateRequest request) {
        brokerService.updateDiscount(request);
    }

    @DeleteMapping("/delete/{id}")
    public BrokerDto delete(@PathVariable Long id) {
        return brokerService.delete(id);
    }

    @GetMapping("/detail/{id}")
    public BrokerDto detail(@PathVariable Long id) {
        return brokerService.detail(id);
    }

    @GetMapping("/all")
    public List<BrokerDto> getAllBrokers() {
        return brokerService.getAllBrokers();
    }
}

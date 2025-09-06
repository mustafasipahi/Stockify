package com.stockify.project.controller;

import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.request.BrokerCreateRequest;
import com.stockify.project.model.request.BrokerUpdateRequest;
import com.stockify.project.model.request.DiscountUpdateRequest;
import com.stockify.project.service.BrokerPostService;
import com.stockify.project.service.BrokerGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/broker")
public class BrokerController {

    private final BrokerPostService brokerPostService;
    private final BrokerGetService brokerGetService;

    @PostMapping("/save")
    public BrokerDto save(@RequestBody BrokerCreateRequest request) {
        return brokerPostService.save(request);
    }

    @PutMapping("/update")
    public BrokerDto update(@RequestBody BrokerUpdateRequest request) {
        return brokerPostService.update(request);
    }

    @DeleteMapping("/delete/{id}")
    public BrokerDto delete(@PathVariable Long id) {
        return brokerPostService.delete(id);
    }

    @PutMapping("/update/discount-rate")
    public void updateDiscount(@RequestBody DiscountUpdateRequest request) {
        brokerPostService.updateDiscountRate(request);
    }

    @GetMapping("/detail/{id}")
    public BrokerDto detail(@PathVariable Long id) {
        return brokerGetService.detail(id);
    }

    @GetMapping("/all")
    public List<BrokerDto> getAllBrokers() {
        return brokerGetService.getAllBrokers();
    }
}

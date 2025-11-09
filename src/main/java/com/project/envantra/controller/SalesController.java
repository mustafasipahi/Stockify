package com.project.envantra.controller;

import com.project.envantra.model.dto.SalesItemDto;
import com.project.envantra.model.dto.SalesProductDto;
import com.project.envantra.model.request.SalesRequest;
import com.project.envantra.model.response.SalesResponse;
import com.project.envantra.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sales")
public class SalesController {

    private final SalesService salesService;

    @PostMapping("/calculate")
    public SalesResponse salesCalculate(@RequestBody SalesRequest request) {
        return salesService.salesCalculate(request);
    }

    @PostMapping("/confirm")
    public SalesResponse salesConfirm(@RequestBody SalesRequest request) {
        return salesService.salesConfirm(request);
    }

    @PostMapping("/cancel")
    public void salesCancel(@RequestBody SalesRequest request) {
        salesService.salesCancel(request);
    }

    @GetMapping("/products")
    public List<SalesProductDto> getSalesInventory() {
        return salesService.getSalesInventory();
    }

    @GetMapping("/basket/{brokerId}")
    public List<SalesItemDto> getBrokerBasket(@PathVariable Long brokerId) {
        return salesService.getBrokerBasketDetail(brokerId);
    }
}

package com.project.envantra.controller;

import com.project.envantra.model.dto.BrokerDto;
import com.project.envantra.model.request.BrokerCreateRequest;
import com.project.envantra.model.request.BrokerOrderUpdateRequest;
import com.project.envantra.model.request.BrokerUpdateRequest;
import com.project.envantra.model.request.DiscountUpdateRequest;
import com.project.envantra.service.BrokerPostService;
import com.project.envantra.service.BrokerGetService;
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

    @PutMapping("/update/order")
    public BrokerDto updateOrder(@RequestBody BrokerOrderUpdateRequest request) {
        return brokerPostService.updateOrder(request);
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
        return brokerGetService.getActiveBroker(id);
    }

    @GetMapping("/all")
    public List<BrokerDto> getAllBrokers() {
        return brokerGetService.getAllBrokers();
    }

    @GetMapping("/all/passive")
    public List<BrokerDto> getAllPassiveBrokers() {
        return brokerGetService.getAllPassiveBrokers();
    }

    @GetMapping("/activate/{id}")
    public BrokerDto activate(@PathVariable Long id) {
        return brokerPostService.activate(id);
    }
}

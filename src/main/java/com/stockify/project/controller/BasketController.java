package com.stockify.project.controller;

import com.stockify.project.model.dto.BasketDto;
import com.stockify.project.model.request.BasketAddRequest;
import com.stockify.project.model.request.BasketRemoveRequest;
import com.stockify.project.service.BasketGetService;
import com.stockify.project.service.BasketPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/basket")
public class BasketController {

    private final BasketPostService basketPostService;
    private final BasketGetService basketGetService;

    @PostMapping("/add")
    public void addToBasket(@RequestBody BasketAddRequest request) {
        basketPostService.addToBasket(request);
    }

    @PostMapping("/update")
    public void updateToBasket(@RequestBody BasketAddRequest request) {
        basketPostService.addToBasket(request);
    }

    @PostMapping("/remove")
    public void removeToBasket(@RequestBody BasketRemoveRequest request) {
        basketPostService.removeToBasket(request);
    }

    @GetMapping("/all/{brokerId}")
    public List<BasketDto> getBaskets(@PathVariable Long brokerId) {
        return basketGetService.getBrokerAllBasket(brokerId);
    }
}

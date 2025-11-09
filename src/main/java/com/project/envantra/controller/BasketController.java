package com.project.envantra.controller;

import com.project.envantra.model.request.BasketAddRequest;
import com.project.envantra.model.request.BasketRemoveRequest;
import com.project.envantra.service.BasketPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/basket")
public class BasketController {

    private final BasketPostService basketPostService;

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
}

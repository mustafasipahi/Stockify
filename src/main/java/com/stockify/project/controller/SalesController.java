package com.stockify.project.controller;

import com.stockify.project.model.dto.SalesProductDto;
import com.stockify.project.model.request.SalesRequest;
import com.stockify.project.model.response.SalesResponse;
import com.stockify.project.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sales")
public class SalesController {

    private final SalesService salesService;

    @PostMapping("/preview")
    public SalesResponse salesPreview(@RequestBody SalesRequest request) {
        return salesService.salesPreview(request);
    }

    @PostMapping("/confirm")
    public SalesResponse salesConfirm(@RequestBody SalesRequest request) {
        return salesService.salesConfirm(request);
    }

    @GetMapping("/products")
    public List<SalesProductDto> getProducts() {
        return salesService.getProducts();
    }
}

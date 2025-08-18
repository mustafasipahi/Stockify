package com.stockify.project.controller;

import com.stockify.project.model.dto.ProductPriceHistoryDto;
import com.stockify.project.service.ProductHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product/history")
public class ProductHistoryController {

    private final ProductHistoryService productHistoryService;

    @GetMapping("/price/{id}")
    public List<ProductPriceHistoryDto> getPriceHistory(@PathVariable Long id) {
        return productHistoryService.getPriceHistory(id);
    }
}

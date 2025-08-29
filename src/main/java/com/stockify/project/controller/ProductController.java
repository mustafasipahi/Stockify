package com.stockify.project.controller;

import com.stockify.project.model.dto.ProductDto;
import com.stockify.project.model.request.ProductCreateRequest;
import com.stockify.project.model.request.ProductSearchRequest;
import com.stockify.project.model.request.ProductUpdateRequest;
import com.stockify.project.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/save")
    public ProductDto save(@RequestBody ProductCreateRequest request) {
        return productService.save(request);
    }

    @PutMapping("/update")
    public ProductDto update(@RequestBody ProductUpdateRequest request) {
        return productService.update(request);
    }

    @DeleteMapping("/delete/{id}")
    public ProductDto delete(@PathVariable Long id) {
        return productService.delete(id);
    }

    @GetMapping("/detail/{id}")
    public ProductDto detail(@PathVariable Long id) {
        return productService.detail(id);
    }

    @GetMapping("/all")
    public List<ProductDto> getAll(@ModelAttribute ProductSearchRequest request) {
        return productService.getAll(request);
    }
}

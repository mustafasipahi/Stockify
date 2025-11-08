package com.stockify.project.controller;

import com.stockify.project.model.dto.ProductDto;
import com.stockify.project.model.request.ProductCreateRequest;
import com.stockify.project.model.request.ProductSearchRequest;
import com.stockify.project.model.request.ProductUpdateRequest;
import com.stockify.project.service.ProductGetService;
import com.stockify.project.service.ProductPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductPostService productPostService;
    private final ProductGetService productGetService;

    @PostMapping("/save")
    public ProductDto save(@RequestBody ProductCreateRequest request) {
        return productPostService.save(request);
    }

    @PutMapping("/update")
    public ProductDto update(@RequestBody ProductUpdateRequest request) {
        return productPostService.update(request);
    }

    @DeleteMapping("/delete/{id}")
    public ProductDto delete(@PathVariable Long id) {
        return productPostService.delete(id);
    }

    @GetMapping("/detail/{id}")
    public ProductDto detail(@PathVariable Long id) {
        return productGetService.detail(id);
    }

    @GetMapping("/all")
    public List<ProductDto> getAll(@ModelAttribute ProductSearchRequest request) {
        return productGetService.getAll(request);
    }

    @GetMapping("/all/passive")
    public List<ProductDto> getAllPassive() {
        return productGetService.getAllPassive();
    }

    @GetMapping("/activate/{id}")
    public ProductDto activate(@PathVariable Long id) {
        return productPostService.activate(id);
    }
}

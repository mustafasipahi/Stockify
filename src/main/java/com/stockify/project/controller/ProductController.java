package com.stockify.project.controller;

import com.stockify.project.model.dto.ProductDto;
import com.stockify.project.model.request.ProductCreateRequest;
import com.stockify.project.model.request.ProductSearchRequest;
import com.stockify.project.model.request.ProductUpdateRequest;
import com.stockify.project.security.userdetail.UserPrincipal;
import com.stockify.project.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/save")
    public ProductDto save(@AuthenticationPrincipal final UserPrincipal userPrincipal,
                           @RequestBody ProductCreateRequest request) {
        return productService.save(userPrincipal.getUserEntity().getId(), request);
    }

    @PutMapping("/update")
    public ProductDto update(@AuthenticationPrincipal final UserPrincipal userPrincipal,
                             @RequestBody ProductUpdateRequest request) {
        return productService.update(userPrincipal.getUserEntity().getId(), request);
    }

    @DeleteMapping("/delete/{id}")
    public ProductDto delete(@AuthenticationPrincipal final UserPrincipal userPrincipal,
                             @PathVariable Long id) {
        return productService.delete(userPrincipal.getUserEntity().getId(), id);
    }

    @GetMapping("/detail/{id}")
    public ProductDto detail(@PathVariable Long id) {
        return productService.detail(id);
    }

    @GetMapping("/search")
    public Page<ProductDto> search(@ModelAttribute ProductSearchRequest request) {
        return productService.search(request);
    }
}

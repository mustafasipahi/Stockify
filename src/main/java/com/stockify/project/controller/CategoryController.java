package com.stockify.project.controller;

import com.stockify.project.model.dto.CategoryDto;
import com.stockify.project.model.request.CategoryCreateRequest;
import com.stockify.project.model.request.CategoryUpdateRequest;
import com.stockify.project.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/save")
    public void save(@RequestBody CategoryCreateRequest request) {
        categoryService.save(request);
    }

    @PutMapping("/update")
    public void update(@RequestBody CategoryUpdateRequest request) {
        categoryService.update(request);
    }

    @GetMapping("/all")
    public List<CategoryDto> getAll() {
        return categoryService.getAll();
    }
}

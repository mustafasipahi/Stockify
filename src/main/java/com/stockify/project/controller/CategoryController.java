package com.stockify.project.controller;

import com.stockify.project.model.dto.CategoryDto;
import com.stockify.project.model.request.CategoryCreateRequest;
import com.stockify.project.model.request.CategoryUpdateRequest;
import com.stockify.project.service.CategoryGetService;
import com.stockify.project.service.CategoryPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryPostService categoryPostService;
    private final CategoryGetService categoryGetService;

    @PostMapping("/save")
    public void save(@RequestBody CategoryCreateRequest request) {
        categoryPostService.save(request);
    }

    @PutMapping("/update")
    public void update(@RequestBody CategoryUpdateRequest request) {
        categoryPostService.update(request);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        categoryPostService.delete(id);
    }

    @GetMapping("/all")
    public List<CategoryDto> getAll() {
        return categoryGetService.getAll();
    }
}

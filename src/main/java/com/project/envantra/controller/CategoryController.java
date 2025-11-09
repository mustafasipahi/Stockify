package com.project.envantra.controller;

import com.project.envantra.model.dto.CategoryDto;
import com.project.envantra.model.request.CategoryCreateRequest;
import com.project.envantra.model.request.CategoryUpdateRequest;
import com.project.envantra.service.CategoryGetService;
import com.project.envantra.service.CategoryPostService;
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

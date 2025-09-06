package com.stockify.project.controller;

import com.stockify.project.model.dto.InventoryDto;
import com.stockify.project.model.request.InventoryCreateRequest;
import com.stockify.project.model.request.InventoryUpdateRequest;
import com.stockify.project.service.InventoryGetService;
import com.stockify.project.service.InventoryPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryPostService inventoryPostService;
    private final InventoryGetService inventoryGetService;

    @PostMapping("/save")
    public InventoryDto save(@RequestBody InventoryCreateRequest request) {
        return inventoryPostService.save(request);
    }

    @PutMapping("/update")
    public InventoryDto update(@RequestBody InventoryUpdateRequest request) {
        return inventoryPostService.update(request);
    }

    @GetMapping("/detail/{id}")
    public InventoryDto detail(@PathVariable Long id) {
        return inventoryGetService.detail(id);
    }

    @GetMapping("/all")
    public List<InventoryDto> getAllInventory() {
        return inventoryGetService.getAllInventory();
    }

    @GetMapping("/available")
    public List<InventoryDto> getAvailableInventory() {
        return inventoryGetService.getAvailableInventory();
    }

    @GetMapping("/critical")
    public List<InventoryDto> getCriticalInventory() {
        return inventoryGetService.getCriticalInventory();
    }

    @GetMapping("outOf")
    public List<InventoryDto> getOutOfInventory() {
        return inventoryGetService.getOutOfInventory();
    }
}

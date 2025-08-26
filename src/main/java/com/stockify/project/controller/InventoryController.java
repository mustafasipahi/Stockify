package com.stockify.project.controller;

import com.stockify.project.model.dto.InventoryDto;
import com.stockify.project.model.request.InventoryCreateRequest;
import com.stockify.project.model.request.InventoryUpdateRequest;
import com.stockify.project.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/save")
    public InventoryDto save(@RequestBody InventoryCreateRequest request) {
        return inventoryService.save(request);
    }

    @PutMapping("/update")
    public InventoryDto update(@RequestBody InventoryUpdateRequest request) {
        return inventoryService.update(request);
    }

    @GetMapping("/all")
    public List<InventoryDto> getAllInventory() {
        return inventoryService.getAllInventory();
    }

    @GetMapping("/available")
    public List<InventoryDto> getAvailableInventory() {
        return inventoryService.getAvailableInventory();
    }

    @GetMapping("/critical")
    public List<InventoryDto> getCriticalInventory() {
        return inventoryService.getCriticalInventory();
    }

    @GetMapping("outOf")
    public List<InventoryDto> getOutOfInventory() {
        return inventoryService.getOutOfInventory();
    }
}

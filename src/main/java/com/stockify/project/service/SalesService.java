package com.stockify.project.service;

import com.stockify.project.model.dto.SalesProductDto;
import com.stockify.project.model.request.SalesRequest;
import com.stockify.project.model.response.SalesConfirmResponse;
import com.stockify.project.model.response.SalesPreviewResponse;
import com.stockify.project.model.response.SalesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final SalesPersistenceService salesPersistenceService;

    public SalesResponse salesCalculate(SalesRequest request) {
        return salesPersistenceService.salesPreview(request);
    }

    public SalesPreviewResponse salesPreview(SalesRequest request) {
        SalesResponse salesResponse = salesPersistenceService.salesPreview(request);
        return SalesPreviewResponse.builder()
                .salesResponse(salesResponse)
                .build();
    }

    public SalesConfirmResponse salesConfirm(SalesRequest request) {
        SalesResponse salesResponse = salesPersistenceService.salesConfirm(request);
        return SalesConfirmResponse.builder()
                .salesResponse(salesResponse)
                .build();
    }

    public List<SalesProductDto> getProducts() {
        return salesPersistenceService.getProducts();
    }
}

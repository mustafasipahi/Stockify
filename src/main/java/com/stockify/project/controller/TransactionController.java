package com.stockify.project.controller;

import com.stockify.project.model.dto.TransactionDto;
import com.stockify.project.model.request.TransactionSearchRequest;
import com.stockify.project.service.TransactionGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionGetService transactionGetService;

    @GetMapping("/all")
    public Page<TransactionDto> getAllTransactions(@ModelAttribute TransactionSearchRequest request,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return transactionGetService.getAllTransactions(request, page, size);
    }
}

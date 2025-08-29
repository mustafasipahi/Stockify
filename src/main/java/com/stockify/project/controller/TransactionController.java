package com.stockify.project.controller;

import com.stockify.project.model.dto.TransactionDto;
import com.stockify.project.model.request.TransactionSearchRequest;
import com.stockify.project.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/all")
    public List<TransactionDto> getAllTransactions(@ModelAttribute TransactionSearchRequest request) {
        return transactionService.getTransactions(request);
    }
}

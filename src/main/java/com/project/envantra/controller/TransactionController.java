package com.project.envantra.controller;

import com.project.envantra.model.dto.TransactionDto;
import com.project.envantra.model.request.TransactionSearchRequest;
import com.project.envantra.service.TransactionGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        Pageable pageable = PageRequest.of(page, size);
        return transactionGetService.getAllTransactions(request, pageable);
    }
}

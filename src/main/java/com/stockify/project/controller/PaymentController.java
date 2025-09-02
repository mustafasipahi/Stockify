package com.stockify.project.controller;

import com.stockify.project.model.request.PaymentCreateRequest;
import com.stockify.project.model.response.PaymentResponse;
import com.stockify.project.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/save")
    public PaymentResponse save(@RequestBody PaymentCreateRequest request) {
        return paymentService.save(request);
    }
}

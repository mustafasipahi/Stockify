package com.project.envantra.controller;

import com.project.envantra.model.request.PaymentCancelRequest;
import com.project.envantra.model.request.PaymentCreateRequest;
import com.project.envantra.model.request.PaymentUpdateRequest;
import com.project.envantra.model.response.PaymentResponse;
import com.project.envantra.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/save")
    public PaymentResponse save(@RequestBody PaymentCreateRequest request) {
        return paymentService.save(request);
    }

    @PutMapping("/update")
    public PaymentResponse update(@RequestBody PaymentUpdateRequest request) {
        return paymentService.update(request);
    }

    @PutMapping("/cancel")
    public PaymentResponse cancel(@RequestBody PaymentCancelRequest request) {
        return paymentService.cancel(request);
    }

    public Page<PaymentResponse> search(@ModelAttribute PaymentSearchRequest request,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return paymentService.search(request, pageable);
    }
}

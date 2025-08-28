package com.stockify.project.service;

import com.stockify.project.converter.PaymentConverter;
import com.stockify.project.enums.BrokerStatus;
import com.stockify.project.exception.BrokerNotFoundException;
import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.entity.PaymentEntity;
import com.stockify.project.model.request.PaymentCreateRequest;
import com.stockify.project.model.response.PaymentResponse;
import com.stockify.project.repository.PaymentRepository;
import com.stockify.project.validator.PaymentCreateValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.stockify.project.util.TenantContext.getTenantId;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BrokerService brokerService;

    @Transactional
    public PaymentResponse save(PaymentCreateRequest request) {
        PaymentCreateValidator.validate(request);
        BrokerDto broker = getBroker(request.getBrokerId());
        Long tenantId = getTenantId();
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .brokerId(request.getBrokerId())
                .price(request.getPaymentPrice())
                .type(request.getPaymentType())
                .tenantId(tenantId)
                .build();
        paymentRepository.save(paymentEntity);
        updateBrokerDebt(request.getBrokerId(), request.getPaymentPrice(), tenantId);
        return PaymentConverter.toResponse(request, broker);
    }

    private BrokerDto getBroker(Long brokerId) {
        BrokerDto broker = brokerService.detail(brokerId);
        if (broker.getStatus() != BrokerStatus.ACTIVE) {
            throw new BrokerNotFoundException(brokerId);
        }
        return broker;
    }

    private void updateBrokerDebt(Long brokerId, BigDecimal price, Long tenantId) {
        brokerService.decreaseDebtPrice(brokerId, price, tenantId);
    }
}

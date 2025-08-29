package com.stockify.project.converter;

import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.entity.BrokerEntity;
import com.stockify.project.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import static com.stockify.project.util.TenantContext.getTenantId;

@Component
@AllArgsConstructor
public class BrokerConverter {

    private TransactionService transactionService;

    public BrokerDto toDto(BrokerEntity brokerEntity) {
        return BrokerDto.builder()
                .brokerId(brokerEntity.getId())
                .firstName(brokerEntity.getFirstName())
                .lastName(brokerEntity.getLastName())
                .discountRate(brokerEntity.getDiscountRate())
                .currentBalance(transactionService.getBrokerCurrentBalance(brokerEntity.getId(), getTenantId()))
                .status(brokerEntity.getStatus())
                .createdDate(brokerEntity.getCreatedDate())
                .lastModifiedDate(brokerEntity.getLastModifiedDate())
                .build();
    }

    public BrokerDto toIdDto(BrokerEntity brokerEntity) {
        return BrokerDto.builder()
                .brokerId(brokerEntity.getId())
                .build();
    }
}

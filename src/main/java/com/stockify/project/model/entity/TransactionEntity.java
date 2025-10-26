package com.stockify.project.model.entity;

import com.stockify.project.enums.PaymentType;
import com.stockify.project.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transaction", indexes = {
        @Index(name = "idx_transaction_broker_tenant_created", columnList = "brokerId,tenantId,createdDate"),
        @Index(name = "idx_transaction_tenant_created", columnList = "tenantId,createdDate")
})
@EntityListeners(AuditingEntityListener.class)
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long brokerId;

    @Column
    private Long salesId;

    @Column
    private Long paymentId;

    @NotNull
    @Column(nullable = false)
    private Long documentId;

    @NotNull
    @Column(nullable = false)
    private String documentNumber;

    @NotNull
    @Column(nullable = false)
    private BigDecimal price;

    @NotNull
    @Column(nullable = false)
    private BigDecimal balance;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @NotNull
    @Column(nullable = false)
    private Long tenantId;

    @CreatedDate
    private LocalDateTime createdDate;
}

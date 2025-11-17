package com.project.envantra.model.entity;

import com.project.envantra.enums.PaymentStatus;
import com.project.envantra.enums.PaymentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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
@Table(name = "payment", indexes = {
        @Index(name = "idx_payment_broker_created", columnList = "brokerId,createdDate"),
        @Index(name = "idx_payment_created", columnList = "createdDate"),
        @Index(name = "idx_payment_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long creatorUserId;

    @NotNull
    @Column(nullable = false)
    private Long brokerId;

    @Column
    private Long originalPaymentId;

    @NotNull
    @Column(nullable = false)
    private Long documentId;

    @NotNull
    @Column(nullable = false)
    private BigDecimal price;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType type;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column
    private String cancelReason;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
package com.stockify.project.model.entity;

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
@Table(name = "sales")
@EntityListeners(AuditingEntityListener.class)
public class SalesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long brokerId;

    @NotNull
    @Column(nullable = false)
    private String documentId;

    @NotNull
    @Column(nullable = false)
    private String documentNumber;

    @NotNull
    @Column(nullable = false)
    private BigDecimal subtotalPrice;

    @NotNull
    @Column(nullable = false)
    private BigDecimal discountRate;

    @NotNull
    @Column(nullable = false)
    private BigDecimal discountPrice;

    @NotNull
    @Column(nullable = false)
    private BigDecimal totalPrice;

    @NotNull
    @Column(nullable = false)
    private BigDecimal totalTaxPrice;

    @NotNull
    @Column(nullable = false)
    private BigDecimal totalPriceWithTax;

    @NotNull
    @Column(nullable = false)
    private Long tenantId;

    @CreatedDate
    private LocalDateTime createdDate;
}

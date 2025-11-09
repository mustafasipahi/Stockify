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
@Table(name = "sales_items", indexes = {
        @Index(name = "idx_sales_item_sales", columnList = "salesId"),
        @Index(name = "idx_sales_item_product", columnList = "productId")
})
@EntityListeners(AuditingEntityListener.class)
public class SalesItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long salesId;

    @NotNull
    @Column(nullable = false)
    private Long productId;

    @NotNull
    @Column(nullable = false)
    private Integer productCount;

    @NotNull
    @Column(nullable = false)
    private BigDecimal unitPrice;

    @NotNull
    @Column(nullable = false)
    private BigDecimal totalPrice;

    @NotNull
    @Column(nullable = false)
    private BigDecimal discountRate;

    @NotNull
    @Column(nullable = false)
    private BigDecimal discountPrice;

    @NotNull
    @Column(nullable = false)
    private BigDecimal priceAfterDiscount;

    @NotNull
    @Column(nullable = false)
    private BigDecimal taxRate;

    @NotNull
    @Column(nullable = false)
    private BigDecimal taxPrice;

    @NotNull
    @Column(nullable = false)
    private BigDecimal totalPriceWithTax;

    @CreatedDate
    private LocalDateTime createdDate;
}

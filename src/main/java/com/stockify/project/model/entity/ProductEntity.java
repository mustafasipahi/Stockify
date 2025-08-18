package com.stockify.project.model.entity;

import com.stockify.project.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String stockCode;

    @Column
    private String name;

    @Column
    private BigDecimal amount;

    @Column
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Column
    private Long createdAgentId;

    @Column
    private Long updatedAgentId;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}

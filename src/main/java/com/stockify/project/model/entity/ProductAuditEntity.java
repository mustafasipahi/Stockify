package com.stockify.project.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_audit")
public class ProductAuditEntity {

    @Id
    private Long id;

    @Column
    private BigDecimal price;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;
}

package com.stockify.project.model.entity;

import com.stockify.project.enums.InventoryStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
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
@Audited
@Table(name = "inventory")
@AuditTable(value = "inventory_audit")
@EntityListeners(AuditingEntityListener.class)
public class InventoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long productId;

    @NotNull
    @Column(nullable = false)
    private BigDecimal price;

    @NotNull
    @Column(nullable = false)
    private Integer productCount;

    @Column
    private Integer criticalProductCount;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InventoryStatus status;

    @CreatedDate
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}

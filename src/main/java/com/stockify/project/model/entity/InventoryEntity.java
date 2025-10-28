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
@Table(name = "inventory", indexes = {
        @Index(name = "idx_inventory_product_unique", columnList = "productId"),
        @Index(name = "idx_inventory_tenant_status", columnList = "tenantId,status")
})
@AuditTable(value = "inventory_audit")
@EntityListeners(AuditingEntityListener.class)
public class InventoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true)
    private Long productId;

    @NotNull
    @Column(nullable = false)
    private Long creatorUserId;

    @NotNull
    @Column(nullable = false)
    private BigDecimal price;

    @NotNull
    @Column(nullable = false)
    private Integer productCount;

    @Column
    private Integer criticalProductCount;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean active;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InventoryStatus status;

    @NotNull
    @Column(nullable = false)
    private Long tenantId;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}

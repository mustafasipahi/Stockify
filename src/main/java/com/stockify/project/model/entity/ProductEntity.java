package com.stockify.project.model.entity;

import com.stockify.project.enums.ProductStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Audited
@Table(name = "product", indexes = {
        @Index(name = "idx_product_category_tenant", columnList = "categoryId,tenantId"),
        @Index(name = "idx_product_name_tenant", columnList = "name,tenantId"),
        @Index(name = "idx_product_tenant_created", columnList = "tenantId,createdDate")
})
@AuditTable(value = "product_audit")
@EntityListeners(AuditingEntityListener.class)
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long categoryId;

    @NotNull
    @Column(nullable = false)
    private String inventoryCode;

    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @NotNull
    @Column(nullable = false)
    private Long tenantId;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}

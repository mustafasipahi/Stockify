package com.stockify.project.model.entity.tenant;

import com.stockify.project.enums.ProductStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
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
@Table(name = "product")
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
    private String stockCode;

    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @NotAudited
    @OneToOne(cascade = CascadeType.ALL)
    private CategoryEntity categoryEntity;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}

package com.stockify.project.model.entity;

import com.stockify.project.enums.BrokerStatus;
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
@Table(name = "broker", indexes = {
        @Index(name = "idx_broker_name_tenant", columnList = "firstName,lastName,tenantId"),
        @Index(name = "idx_broker_status_tenant_created", columnList = "status,tenantId,createdDate")
})
@EntityListeners(AuditingEntityListener.class)
public class BrokerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String firstName;

    @NotNull
    @Column(nullable = false)
    private String lastName;

    @Column
    private BigDecimal discountRate;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BrokerStatus status;

    @NotNull
    @Column(nullable = false)
    private Long tenantId;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}

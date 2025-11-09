package com.project.envantra.model.entity;

import com.project.envantra.enums.BrokerStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "broker", indexes = {
        @Index(name = "idx_broker_status_created", columnList = "status,createdDate")
})
@EntityListeners(AuditingEntityListener.class)
public class BrokerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long creatorUserId;

    @NotNull
    @Column(nullable = false)
    private Long brokerUserId;

    @Column
    private BigDecimal discountRate;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BrokerStatus status;

    @Column
    private DayOfWeek targetDayOfWeek;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}

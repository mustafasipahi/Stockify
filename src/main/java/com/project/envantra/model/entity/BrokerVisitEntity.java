package com.project.envantra.model.entity;

import com.project.envantra.enums.BrokerVisitStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
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
@Table(name = "broker_visit", indexes = {
        @Index(name = "idx_visit_broker_date", columnList = "brokerId,visitDate"),
        @Index(name = "idx_visit_user_date", columnList = "creatorUserId,visitDate"),
        @Index(name = "idx_visit_date", columnList = "visitDate")
})
@EntityListeners(AuditingEntityListener.class)
public class BrokerVisitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long creatorUserId;

    @NotNull
    @Column(nullable = false)
    private Long brokerId;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime visitDate;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BrokerVisitStatus status;

    @Column(length = 500)
    private String note;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}

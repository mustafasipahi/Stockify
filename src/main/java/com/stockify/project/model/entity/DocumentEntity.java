package com.stockify.project.model.entity;

import com.stockify.project.enums.DocumentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "documents", indexes = {
        @Index(name = "idx_document_broker_tenant_created", columnList = "brokerId,tenantId,createdDate"),
        @Index(name = "idx_document_tenant_created", columnList = "tenantId,createdDate")
})
@EntityListeners(AuditingEntityListener.class)
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String outId;

    @NotNull
    @Column(nullable = false)
    private Long brokerId;

    @NotNull
    @Column(nullable = false)
    private String originalFilename;

    @NotNull
    @Column(nullable = false)
    private String fileName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    @Column(nullable = false)
    private String bucket;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String fullPath;

    @NotNull
    @Column(nullable = false)
    private Long tenantId;

    @Column
    private String message;

    @CreatedDate
    private LocalDateTime createdDate;
}

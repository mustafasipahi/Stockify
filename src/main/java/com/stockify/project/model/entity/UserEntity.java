package com.stockify.project.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class UserEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String username;

    @NotNull
    @Column(nullable = false)
    private String password;

    @NotNull
    @Column(nullable = false)
    private String stokifySchemaName;

    @CreatedDate
    @Column(name = "created_date")
    private LocalDateTime createdDate;
}

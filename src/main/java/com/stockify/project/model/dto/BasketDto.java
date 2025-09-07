package com.stockify.project.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasketDto {

    private Long id;
    private Long brokerId;
    private Long productId;
    private Integer productCount;
    private Long tenantId;
    private LocalDateTime createdDate;
}

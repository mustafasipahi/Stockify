package com.project.envantra.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasketDto {

    private Long id;
    private Long creatorUserId;
    private Long brokerId;
    private Long productId;
    private Integer productCount;
    private LocalDateTime createdDate;
}

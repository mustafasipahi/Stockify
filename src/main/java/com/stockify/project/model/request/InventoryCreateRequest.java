package com.stockify.project.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryCreateRequest {

    private Long productId;
    private Long ownerUserId;
    private BigDecimal price;
    private Integer productCount;
    private Integer criticalProductCount;
}

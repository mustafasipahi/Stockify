package com.project.envantra.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryUpdateRequest {

    private Long inventoryId;
    private Boolean active;
    private BigDecimal price;
    private Integer productCount;
    private Integer criticalProductCount;
}

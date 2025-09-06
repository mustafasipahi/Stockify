package com.stockify.project.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.stockify.project.enums.InventoryStatus;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long inventoryId;
    private ProductDto product;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private Integer productCount;
    private Integer criticalProductCount;
    private boolean active;
    private InventoryStatus status;
    private Long createdDate;
    private Long lastModifiedDate;
}

package com.stockify.project.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.stockify.project.enums.InventoryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
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
    private InventoryStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}

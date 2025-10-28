package com.stockify.project.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.stockify.project.enums.ProductStatus;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long productId;
    private Long categoryId;
    private Long creatorUserId;
    private String categoryName;
    private BigDecimal taxRate;
    private String inventoryCode;
    private String name;
    private ProductStatus status;
    private Long createdDate;
    private Long lastModifiedDate;
}

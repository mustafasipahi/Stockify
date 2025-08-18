package com.stockify.project.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.stockify.project.enums.ProductStatus;
import com.stockify.project.model.dto.StockifyPageable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductSearchRequest extends StockifyPageable {

    private String searchText;
    private ProductStatus status;
}

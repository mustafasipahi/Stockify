package com.project.envantra.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.envantra.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductSearchRequest {

    private String productText;
    private ProductStatus status;
}

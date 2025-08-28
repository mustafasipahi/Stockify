package com.stockify.project.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryUpdateRequest {

    private Long categoryId;
    private String name;
    private Double taxRate;
}

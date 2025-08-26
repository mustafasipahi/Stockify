package com.stockify.project.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.stockify.project.model.entity.SalesEntity;
import com.stockify.project.model.entity.SalesItemEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesPrepareDto {

    private SalesEntity salesEntity;
    private List<SalesItemEntity> salesItems;
}

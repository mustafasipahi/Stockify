package com.stockify.project.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    private SalesDto sales;
    private List<SalesItemDto> salesItems;
    private BrokerDto broker;
    private CompanyInfoDto companyInfo;
}

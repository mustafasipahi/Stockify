package com.stockify.project.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesPrepareDto {

    private SalesDto sales;
    private List<SalesItemDto> salesItems;
    private BrokerDto broker;
    private CompanyInfoDto companyInfo;
}

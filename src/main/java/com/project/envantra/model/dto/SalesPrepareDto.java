package com.project.envantra.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.envantra.model.entity.UserEntity;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesPrepareDto {

    private UserEntity user;
    private SalesDto sales;
    private List<SalesItemDto> salesItems;
    private BrokerDto broker;
    private CompanyDto company;
}

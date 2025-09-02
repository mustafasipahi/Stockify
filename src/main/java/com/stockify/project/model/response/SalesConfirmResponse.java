package com.stockify.project.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.stockify.project.model.dto.ReceiptInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesConfirmResponse {

    private ReceiptInfoDto receiptInfo;
    private SalesResponse salesResponse;
}

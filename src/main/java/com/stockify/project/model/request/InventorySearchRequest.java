package com.stockify.project.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.stockify.project.enums.InventoryStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventorySearchRequest {

    private List<InventoryStatus> statusList;
}

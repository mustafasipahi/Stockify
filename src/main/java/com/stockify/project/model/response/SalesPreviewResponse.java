package com.stockify.project.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.stockify.project.model.dto.SalesItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesPreviewResponse {

    private SalesResponse salesResponse;
}

package com.project.envantra.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasketAddRequest {

    private Long brokerId;
    private Long productId;
    private Integer productCount;
}

package com.project.envantra.model.request;

import com.project.envantra.enums.BrokerVisitStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BrokerVisitRequest {

    @NotNull
    private Long brokerId;
    @NotNull
    private BrokerVisitStatus status;
    private String note;
}

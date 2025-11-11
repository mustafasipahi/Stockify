package com.project.envantra.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.envantra.enums.BrokerVisitStatus;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrokerVisitDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long creatorUserId;
    private Long brokerId;
    private Long visitDate;
    private BrokerVisitStatus status;
    private String note;
}

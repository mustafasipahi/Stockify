package com.project.envantra.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.envantra.enums.DocumentType;
import com.project.envantra.model.dto.BrokerDto;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentUploadRequest {

    private BrokerDto brokerDto;
    private DocumentType documentType;
}

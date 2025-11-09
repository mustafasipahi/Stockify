package com.stockify.project.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.stockify.project.enums.DocumentType;
import com.stockify.project.model.dto.BrokerDto;
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

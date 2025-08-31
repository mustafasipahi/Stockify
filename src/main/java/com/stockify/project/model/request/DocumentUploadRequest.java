package com.stockify.project.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.stockify.project.enums.DocumentType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentUploadRequest {

    private Long brokerId;
    private DocumentType documentType;
}

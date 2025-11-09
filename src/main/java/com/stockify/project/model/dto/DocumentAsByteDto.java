package com.stockify.project.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.stockify.project.model.entity.DocumentEntity;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentAsByteDto {

    private DocumentEntity document;
    private byte[] documentAsByte;
}

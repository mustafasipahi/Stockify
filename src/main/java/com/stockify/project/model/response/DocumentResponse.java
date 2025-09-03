package com.stockify.project.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentResponse {

    private String name;
    private String documentType;
    private String contentType;
    private Long uploadDate;
    private String downloadUrl;
}

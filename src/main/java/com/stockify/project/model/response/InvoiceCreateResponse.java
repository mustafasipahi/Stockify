package com.stockify.project.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceCreateResponse {

    @JsonProperty("httpStatusCode")
    private Integer httpStatusCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("ettn")
    private String ettn;

    @JsonProperty("xmldata")
    private String xmldata;

    @JsonProperty("value")
    private String value;
}

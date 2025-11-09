package com.stockify.project.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long companyId;
    private Long creatorUserId;
    private Long logoImageId;
    private String name;
    private String address;
    private String phoneNumber;
    private String invoiceUsername;
    private String invoicePassword;
}

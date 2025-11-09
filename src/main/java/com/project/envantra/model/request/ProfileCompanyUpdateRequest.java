package com.project.envantra.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileCompanyUpdateRequest {

    private String companyName;
    private String companyAddress;
    private String phoneNumber;
    private String invoiceUsername;
    private String invoicePassword;
}

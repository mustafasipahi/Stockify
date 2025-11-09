package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.COMPANY_INFO_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class CompanyNotFoundException extends EnvantraRuntimeException {

    public CompanyNotFoundException() {
        super(COMPANY_INFO_NOT_FOUND, NOT_FOUND, "Company Info Not Found!");
    }
}

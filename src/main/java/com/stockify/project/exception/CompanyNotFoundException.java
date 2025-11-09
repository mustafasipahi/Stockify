package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.COMPANY_INFO_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class CompanyNotFoundException extends StockifyRuntimeException {

    public CompanyNotFoundException() {
        super(COMPANY_INFO_NOT_FOUND, NOT_FOUND, "Company Info Not Found!");
    }
}

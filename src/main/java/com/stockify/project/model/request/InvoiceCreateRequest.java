package com.stockify.project.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceCreateRequest {

    @JsonProperty("MasterClientERPReference")
    private String masterClientERPReference;

    @JsonProperty("MasterClientERPCode")
    private String masterClientERPCode;

    @JsonProperty("InvoiceERPReference")
    private String invoiceERPReference;

    @JsonProperty("invoice")
    private InvoiceRequest invoice;

    @JsonProperty("invoiceLines")
    private List<InvoiceLineRequest> invoiceLines;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class InvoiceRequest {

        @JsonProperty("TCKN_VN")
        private String tcknVn;

        @JsonProperty("TaxOffice")
        private String taxOffice;

        @JsonProperty("Country")
        private String country;

        @JsonProperty("City")
        private String city;

        @JsonProperty("Town")
        private String town;

        @JsonProperty("BuildingName")
        private String buildingName;

        @JsonProperty("BuildingNumber")
        private String buildingNumber;

        @JsonProperty("DoorNumber")
        private String doorNumber;

        @JsonProperty("PostCode")
        private String postCode;

        @JsonProperty("StreetName")
        private String streetName;

        @JsonProperty("Email")
        private String email;

        @JsonProperty("Phone")
        private String phone;

        @JsonProperty("Fax")
        private String fax;

        @JsonProperty("WebAddress")
        private String webAddress;

        @JsonProperty("InvoiceDate")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime invoiceDate;

        @JsonProperty("Note1")
        private String note1;

        @JsonProperty("SubTotal")
        private BigDecimal subTotal;

        @JsonProperty("DiscountTotal")
        private BigDecimal discountTotal;

        @JsonProperty("TotalWithDiscount")
        private BigDecimal totalWithDiscount;

        @JsonProperty("VatAmount")
        private BigDecimal vatAmount;

        @JsonProperty("TotalWithVat")
        private BigDecimal totalWithVat;

        @JsonProperty("Title")
        private String title;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("Surname")
        private String surname;

        @JsonProperty("SourceId")
        private Integer sourceId;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class InvoiceLineRequest {

        @JsonProperty("ProductName")
        private String productName;

        @JsonProperty("Quantity")
        private BigDecimal quantity;

        @JsonProperty("Unit")
        private String unit;

        @JsonProperty("Price")
        private BigDecimal price;

        @JsonProperty("DiscountRate")
        private BigDecimal discountRate;

        @JsonProperty("DiscountAmount")
        private BigDecimal discountAmount;

        @JsonProperty("SubTotal")
        private BigDecimal subTotal;

        @JsonProperty("TotalWithDiscount")
        private BigDecimal totalWithDiscount;

        @JsonProperty("VatRate")
        private BigDecimal vatRate;

        @JsonProperty("VatAmount")
        private BigDecimal vatAmount;

        @JsonProperty("TotalWithVat")
        private BigDecimal totalWithVat;

        @JsonProperty("UnitCode")
        private String unitCode;

        @JsonProperty("ProductCode")
        private String productCode;
    }
}

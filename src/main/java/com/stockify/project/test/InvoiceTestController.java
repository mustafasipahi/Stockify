package com.stockify.project.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockify.project.model.dto.SalesPrepareDto;
import com.stockify.project.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoice/api/test")
@RequiredArgsConstructor
public class InvoiceTestController {

    private static final String TEST_TEXT = "{\n" +
            "  \"sales\" : {\n" +
            "    \"brokerId\" : 1,\n" +
            "    \"documentNumber\" : \"SL1000\",\n" +
            "    \"subtotalPrice\" : 290.0,\n" +
            "    \"discountRate\" : 6.0,\n" +
            "    \"discountPrice\" : 17.4,\n" +
            "    \"totalPrice\" : 272.6,\n" +
            "    \"totalTaxPrice\" : 27.26,\n" +
            "    \"totalPriceWithTax\" : 299.86\n" +
            "  },\n" +
            "  \"salesItems\" : [ {\n" +
            "    \"productId\" : 2,\n" +
            "    \"productName\" : \"test1\",\n" +
            "    \"productCount\" : 5,\n" +
            "    \"unitPrice\" : 15.0,\n" +
            "    \"totalPrice\" : 75.0,\n" +
            "    \"taxRate\" : 10.0,\n" +
            "    \"taxPrice\" : 7.5,\n" +
            "    \"totalPriceWithTax\" : 82.5\n" +
            "  }, {\n" +
            "    \"productId\" : 3,\n" +
            "    \"productName\" : \"test2\",\n" +
            "    \"productCount\" : 6,\n" +
            "    \"unitPrice\" : 16.0,\n" +
            "    \"totalPrice\" : 96.0,\n" +
            "    \"taxRate\" : 10.0,\n" +
            "    \"taxPrice\" : 9.6,\n" +
            "    \"totalPriceWithTax\" : 105.6\n" +
            "  }, {\n" +
            "    \"productId\" : 4,\n" +
            "    \"productName\" : \"test3\",\n" +
            "    \"productCount\" : 7,\n" +
            "    \"unitPrice\" : 17.0,\n" +
            "    \"totalPrice\" : 119.0,\n" +
            "    \"taxRate\" : 10.0,\n" +
            "    \"taxPrice\" : 11.9,\n" +
            "    \"totalPriceWithTax\" : 130.9\n" +
            "  } ],\n" +
            "  \"broker\" : {\n" +
            "    \"brokerId\" : 1,\n" +
            "    \"brokerUserId\" : 4,\n" +
            "    \"firstName\" : \"test1\",\n" +
            "    \"lastName\" : \"test2\",\n" +
            "    \"email\" : \"test1çtest2@gmail.com\",\n" +
            "    \"role\" : \"ROLE_BROKER\",\n" +
            "    \"tkn\" : \"test1\",\n" +
            "    \"vkn\" : \"test2\",\n" +
            "    \"currentBalance\" : 0,\n" +
            "    \"discountRate\" : 6.0,\n" +
            "    \"status\" : \"ACTIVE\",\n" +
            "    \"targetDayOfWeek\" : \"MONDAY\",\n" +
            "    \"createdDate\" : 1761809407967,\n" +
            "    \"lastModifiedDate\" : 1761809407967\n" +
            "  },\n" +
            "  \"companyInfo\" : {\n" +
            "    \"companyName\" : \"Gurme Şirketler Grubu Lt.Ş.\",\n" +
            "    \"companyAddress\" : \"Antalyada Bir Yerde Gülveren Tarafında\"\n" +
            "  }\n" +
            "}";

    private final InvoiceService invoiceService;

    @PostMapping("/create/invoice")
    public void createInvoice(@RequestParam String username, @RequestParam String password) throws JsonProcessingException {
        SalesPrepareDto prepareDto = new ObjectMapper().readValue(TEST_TEXT, SalesPrepareDto.class);
        invoiceService.createInvoice(prepareDto);
    }
}

package com.stockify.project.test;

import com.stockify.project.enums.PaymentType;
import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.dto.CompanyInfoDto;
import com.stockify.project.model.dto.PaymentDto;
import com.stockify.project.model.response.DocumentResponse;
import com.stockify.project.service.email.PaymentEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
//@RestController
//@RequestMapping("/payment/api/test")
@RequiredArgsConstructor
public class PaymentEmailTestController {

    private final PaymentEmailService paymentEmailService;

    @GetMapping("/send-email")
    public ResponseEntity<String> sendPaymentEmail(
            @RequestParam(required = false, defaultValue = "broker@example.com") String brokerEmail,
            @RequestParam(required = false, defaultValue = "company@example.com") String companyEmail
    ) {
        try {
            // Test PaymentDto oluştur
            BrokerDto broker = BrokerDto.builder()
                    .brokerId(1L)
                    .firstName("Ahmet")
                    .lastName("Yılmaz")
                    .email(brokerEmail)
                    .build();

            CompanyInfoDto companyInfo = CompanyInfoDto.builder()
                    .companyName("Test Şirketi")
                    .build();

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            PaymentDto paymentDto = PaymentDto.builder()
                    .broker(broker)
                    .companyInfo(companyInfo)
                    .documentNumber("PAY-" + timestamp)
                    .price(new BigDecimal("5000.00"))
                    .type(PaymentType.CASH)
                    .createdDate(LocalDateTime.now())
                    .build();

            // Mock DocumentResponse oluştur
            MultipartFile mockFile = new MultipartFile() {
                private final byte[] content = "Mock PDF Content for Payment Receipt".getBytes();

                @Override
                public String getName() {
                    return "receipt.pdf";
                }

                @Override
                public String getOriginalFilename() {
                    return "payment_receipt.pdf";
                }

                @Override
                public String getContentType() {
                    return "application/pdf";
                }

                @Override
                public boolean isEmpty() {
                    return content.length == 0;
                }

                @Override
                public long getSize() {
                    return content.length;
                }

                @Override
                public byte[] getBytes() {
                    return content;
                }

                @Override
                public InputStream getInputStream() {
                    return new ByteArrayInputStream(content);
                }

                @Override
                public void transferTo(File dest) throws IOException {
                    throw new UnsupportedOperationException("Transfer not supported in mock");
                }
            };

            DocumentResponse documentResponse = DocumentResponse.builder()
                    .documentId(1L)
                    .fileName("payment_receipt.pdf")
                    .file(mockFile)
                    .build();

            // Email gönder
            paymentEmailService.sendPaymentNotifications(paymentDto, documentResponse);

            String successMessage = String.format(
                    "✅ Email gönderildi!\n" +
                            "📧 Broker: %s\n" +
                            "🏢 Company: %s\n" +
                            "📄 Makbuz No: %s\n" +
                            "💰 Tutar: ₺%s",
                    brokerEmail,
                    companyEmail,
                    paymentDto.getDocumentNumber(),
                    paymentDto.getPrice()
            );

            return ResponseEntity.ok(successMessage);

        } catch (Exception e) {
            log.error("Email gönderme hatası", e);
            return ResponseEntity.internalServerError()
                    .body("❌ Hata: " + e.getMessage());
        }
    }
}
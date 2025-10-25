package com.stockify.project.test;

import com.stockify.project.enums.PaymentType;
import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.dto.PaymentDto;
import com.stockify.project.model.response.PaymentDocumentResponse;
import com.stockify.project.service.document.PaymentDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
//@RestController
//@RequestMapping("/payment/stream/api/test")
@RequiredArgsConstructor
public class PaymentDocumentTestController {

    private final PaymentDocumentService paymentDocumentService;

    private static final String OUTPUT_DIRECTORY = "generated-pdfs";

    @GetMapping("/generate-payment-pdf")
    public ResponseEntity<String> generateTestPaymentPDF() {
        try {
            // Test verilerini oluştur
            PaymentDto testData = createTestData();

            // PDF'i oluştur
            PaymentDocumentResponse response = paymentDocumentService.generatePDF(testData);

            // Çıktı dizinini oluştur
            Path outputDir = Paths.get(OUTPUT_DIRECTORY);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }

            // Dosya adını timestamp ile oluştur
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "payment_document_" + timestamp + ".pdf";
            Path outputPath = outputDir.resolve(filename);

            // PDF'i dosyaya yaz
            try (FileOutputStream fos = new FileOutputStream(outputPath.toFile())) {
                fos.write(response.getFile().getBytes());
                fos.flush();
            }

            String successMessage = String.format(
                    "✅ Makbuz başarıyla oluşturuldu!\n" +
                            "📁 Dosya yolu: %s\n" +
                            "📊 Dosya boyutu: %d bytes\n" +
                            "⏰ Oluşturma zamanı: %s",
                    outputPath.toAbsolutePath(),
                    response.getFile().getSize(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            );

            log.info("Makbuz oluşturuldu: {}", outputPath.toAbsolutePath());

            return ResponseEntity.ok(successMessage);

        } catch (Exception e) {
            log.error("Makbuz oluşturma hatası", e);
            return ResponseEntity.internalServerError()
                    .body("❌ Makbuz oluşturulurken hata oluştu: " + e.getMessage());
        }
    }

    private PaymentDto createTestData() {
        // Broker bilgileri
        BrokerDto broker = BrokerDto.builder()
                .firstName("Mehmet")
                .lastName("DEMIR")
                .build();

        // Payment bilgileri
        return PaymentDto.builder()
                .broker(broker)
                .documentNumber("TAH-2024-5501")
                .price(new BigDecimal("15000.00"))
                .type(PaymentType.CASH) // veya başka bir PaymentType
                .createdDate(LocalDateTime.now())
                .build();
    }
}

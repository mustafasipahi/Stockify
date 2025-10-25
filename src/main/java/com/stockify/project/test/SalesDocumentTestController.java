package com.stockify.project.test;

import com.stockify.project.model.dto.*;
import com.stockify.project.model.response.SalesDocumentResponse;
import com.stockify.project.service.document.SalesDocumentService;
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
import java.util.Arrays;
import java.util.List;

@Slf4j
//@RestController
//@RequestMapping("/sales/stream/api/test")
@RequiredArgsConstructor
public class SalesDocumentTestController {

    private final SalesDocumentService salesDocumentService;

    private static final String OUTPUT_DIRECTORY = "generated-pdfs";

    @GetMapping("/generate-sales-pdf")
    public ResponseEntity<String> generateTestSalesPDF() {
        try {
            // Test verilerini oluştur
            SalesPrepareDto testData = createTestData();

            // PDF'i oluştur
            SalesDocumentResponse response = salesDocumentService.generatePDF(testData);

            // Çıktı dizinini oluştur
            Path outputDir = Paths.get(OUTPUT_DIRECTORY);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }

            // Dosya adını timestamp ile oluştur
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "sales_document_" + timestamp + ".pdf";
            Path outputPath = outputDir.resolve(filename);

            // PDF'i dosyaya yaz
            try (FileOutputStream fos = new FileOutputStream(outputPath.toFile())) {
                fos.write(response.getFile().getBytes());
                fos.flush();
            }

            String successMessage = String.format(
                    """
                            ✅ PDF başarıyla oluşturuldu!
                            📁 Dosya yolu: %s
                            📊 Dosya boyutu: %d bytes
                            ⏰ Oluşturma zamanı: %s""",
                    outputPath.toAbsolutePath(),
                    response.getFile().getSize(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            );

            log.info("PDF oluşturuldu: {}", outputPath.toAbsolutePath());

            return ResponseEntity.ok(successMessage);

        } catch (Exception e) {
            log.error("PDF oluşturma hatası", e);
            return ResponseEntity.internalServerError()
                    .body("❌ PDF oluşturulurken hata oluştu: " + e.getMessage());
        }
    }

    private SalesPrepareDto createTestData() {
        SalesPrepareDto prepareDto = new SalesPrepareDto();

        // Şirket bilgileri
        CompanyInfoDto companyInfo = new CompanyInfoDto();
        companyInfo.setCompanyName("STOCKIFY YAZILIM A.Ş.");
        companyInfo.setCompanyAddress("Teknokent Mahallesi, Silikon Vadisi Cad. No:123/A\nAnkara/TÜRKİYE\nTel: +90 312 123 45 67");

        // Broker (Müşteri) bilgileri
        BrokerDto broker = new BrokerDto();
        broker.setFirstName("Ahmet");
        broker.setLastName("YILMAZ");
        broker.setCurrentBalance(new BigDecimal("2500.75"));

        // Satış bilgileri
        SalesDto sales = new SalesDto();
        sales.setDocumentNumber("SAT-2024-001234");
        sales.setSubtotalPrice(new BigDecimal("8500.00"));
        sales.setDiscountRate(new BigDecimal("5"));
        sales.setDiscountPrice(new BigDecimal("425.00"));
        sales.setTotalPrice(new BigDecimal("8075.00"));
        sales.setTotalTaxPrice(new BigDecimal("1453.50"));
        sales.setTotalPriceWithTax(new BigDecimal("9528.50"));

        // Satış kalemleri
        List<SalesItemDto> salesItems = Arrays.asList(
                createSalesItem("LENOVO THINKPAD E15", new BigDecimal("7500.00"), new BigDecimal("1"), new BigDecimal("18")),
                createSalesItem("LOGITECH MX MASTER 3", new BigDecimal("650.00"), new BigDecimal("1"), new BigDecimal("18")),
                createSalesItem("SAMSUNG 32GB USB BELLEK", new BigDecimal("350.00"), new BigDecimal("1"), new BigDecimal("18")),
                createSalesItem("SAMSUNG 32GB USB BELLEK", new BigDecimal("350.00"), new BigDecimal("1"), new BigDecimal("18")),
                createSalesItem("SAMSUNG 32GB USB BELLEK", new BigDecimal("350.00"), new BigDecimal("1"), new BigDecimal("18")),
                createSalesItem("SAMSUNG 32GB USB BELLEK", new BigDecimal("350.00"), new BigDecimal("1"), new BigDecimal("18")),
                createSalesItem("SAMSUNG 32GB USB BELLEK", new BigDecimal("350.00"), new BigDecimal("1"), new BigDecimal("18")),
                createSalesItem("SAMSUNG 32GB USB BELLEK", new BigDecimal("350.00"), new BigDecimal("1"), new BigDecimal("18")),
                createSalesItem("SAMSUNG 32GB USB BELLEK", new BigDecimal("350.00"), new BigDecimal("1"), new BigDecimal("18")),
                createSalesItem("SAMSUNG 32GB USB BELLEK", new BigDecimal("350.00"), new BigDecimal("1"), new BigDecimal("18")),
                createSalesItem("SAMSUNG 32GB USB BELLEK", new BigDecimal("350.00"), new BigDecimal("1"), new BigDecimal("18")),
                createSalesItem("SAMSUNG 32GB USB BELLEK", new BigDecimal("350.00"), new BigDecimal("1"), new BigDecimal("18")),
                createSalesItem("SAMSUNG 32GB USB BELLEK", new BigDecimal("350.00"), new BigDecimal("1"), new BigDecimal("18")),
                createSalesItem("SAMSUNG 32GB USB BELLEK", new BigDecimal("350.00"), new BigDecimal("1"), new BigDecimal("18"))
        );

        prepareDto.setCompanyInfo(companyInfo);
        prepareDto.setBroker(broker);
        prepareDto.setSales(sales);
        prepareDto.setSalesItems(salesItems);

        return prepareDto;
    }

    private SalesItemDto createSalesItem(String productName, BigDecimal unitPrice, BigDecimal count, BigDecimal taxRate) {
        SalesItemDto item = new SalesItemDto();
        item.setProductName(productName);
        item.setUnitPrice(unitPrice);
        item.setProductCount(count.intValue());
        item.setTaxRate(taxRate);

        // Toplam hesaplamaları
        BigDecimal total = unitPrice.multiply(count);
        BigDecimal tax = total.multiply(taxRate).divide(new BigDecimal("100"));
        BigDecimal totalWithTax = total.add(tax);

        item.setTotalPriceWithTax(totalWithTax);

        return item;
    }
}
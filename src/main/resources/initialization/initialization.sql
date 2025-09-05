INSERT INTO category (name, tax_rate, tenant_id, created_date)
SELECT *
FROM (SELECT 'Çerezler' as name, 8.00 as tax_rate, 1 as tenant_id, NOW() as created_date
      UNION ALL
      SELECT 'Kuruyemişler', 8.00, 1, NOW()
      UNION ALL
      SELECT 'Baharatlar', 8.00, 1, NOW()
      UNION ALL
      SELECT 'Şekerlemeler', 18.00, 1, NOW()
      UNION ALL
      SELECT 'Organik Ürünler', 8.00, 1, NOW()
      UNION ALL
      SELECT 'Hediyelik', 18.00, 1, NOW()
      UNION ALL
      SELECT 'Toplu Satış', 8.00, 1, NOW()) tmp
WHERE NOT EXISTS (SELECT 1 FROM category);

INSERT INTO product (category_id, inventory_code, name, status, tenant_id, created_date, last_modified_date)
SELECT *
FROM (SELECT 1                     as category_id,
             'CRZ001'              as inventory_code,
             'Tuzlu Fıstık (250g)' as name,
             'ACTIVE'              as status,
             1                     as tenant_id,
             NOW()                 as created_date,
             NOW()                 as last_modified_date
      UNION ALL
      SELECT 1, 'CRZ002', 'Çiğ Badem (500g)', 'ACTIVE', 1, NOW(), NOW()
      UNION ALL
      SELECT 1, 'CRZ003', 'Kavrulmuş Leblebi (200g)', 'ACTIVE', 1, NOW(), NOW()
      UNION ALL
      SELECT 1, 'CRZ004', 'Çedar Soslu Çıtır (150g)', 'ACTIVE', 1, NOW(), NOW()
      UNION ALL
      SELECT 2, 'KRY001', 'Premium Ceviz İçi (250g)', 'ACTIVE', 1, NOW(), NOW()
      UNION ALL
      SELECT 2, 'KRY002', 'Antep Fıstığı (100g)', 'ACTIVE', 1, NOW(), NOW()
      UNION ALL
      SELECT 2, 'KRY003', 'Kaju (200g)', 'ACTIVE', 1, NOW(), NOW()
      UNION ALL
      SELECT 2, 'KRY004', 'Çiğ Fındık (300g)', 'ACTIVE', 1, NOW(), NOW()
      UNION ALL
      SELECT 2, 'KRY005', 'Kuru Üzüm Sultani (500g)', 'ACTIVE', 1, NOW(), NOW()
      UNION ALL
      SELECT 2, 'KRY006', 'Hurma Medjoul (250g)', 'ACTIVE', 1, NOW(), NOW()
      UNION ALL
      SELECT 3, 'BHR001', 'Karabiber Tane (50g)', 'ACTIVE', 1, NOW(), NOW()
      UNION ALL
      SELECT 3, 'BHR002', 'Sumak (100g)', 'ACTIVE', 1, NOW(), NOW()
      UNION ALL
      SELECT 3, 'BHR003', 'Kırmızı Pul Biber (100g)', 'ACTIVE', 1, NOW(), NOW()
      UNION ALL
      SELECT 4, 'SKR001', 'Türk Lokumu Karışık (300g)', 'ACTIVE', 1, NOW(), NOW()
      UNION ALL
      SELECT 4, 'SKR002', 'Çikolatalı Fıstık (200g)', 'ACTIVE', 1, NOW(), NOW()
      UNION ALL
      SELECT 5, 'ORG001', 'Organik Badem (250g)', 'ACTIVE', 1, NOW(), NOW()
      UNION ALL
      SELECT 5, 'ORG002', 'Organik Ceviz (200g)', 'ACTIVE', 1, NOW(), NOW()
      UNION ALL
      SELECT 6, 'HDY001', 'Hediyelik Kuruyemiş Kutusu', 'ACTIVE', 1, NOW(), NOW()
      UNION ALL
      SELECT 7, 'TOP001', 'Fıstık Toplu (5kg)', 'ACTIVE', 1, NOW(), NOW()
      UNION ALL
      SELECT 7, 'TOP002', 'Badem Toplu (3kg)', 'ACTIVE', 1, NOW(), NOW()) tmp
WHERE NOT EXISTS (SELECT 1 FROM product);

INSERT INTO inventory (product_id, price, product_count, critical_product_count, status, tenant_id, created_date,
                       last_modified_date)
SELECT *
FROM (SELECT 1           as product_id,
             85.00       as price,
             120         as product_count,
             20          as critical_product_count,
             'AVAILABLE' as status,
             1           as tenant_id,
             NOW()       as created_date,
             NOW()       as last_modified_date
      UNION ALL
      SELECT 2,
             180.00,
             80,
             15,
             'AVAILABLE',
             1,
             NOW(),
             NOW()
      UNION ALL
      SELECT 3,
             45.00,
             200,
             40,
             'AVAILABLE',
             1,
             NOW(),
             NOW()
      UNION ALL
      SELECT 4,
             65.00,
             150,
             25,
             'AVAILABLE',
             1,
             NOW(),
             NOW()
      UNION ALL
      SELECT 5,
             320.00,
             60,
             10,
             'AVAILABLE',
             1,
             NOW(),
             NOW()
      UNION ALL
      SELECT 6,
             450.00,
             40,
             8,
             'AVAILABLE',
             1,
             NOW(),
             NOW()
      UNION ALL
      SELECT 7,
             280.00,
             70,
             12,
             'AVAILABLE',
             1,
             NOW(),
             NOW()
      UNION ALL
      SELECT 8,
             220.00,
             90,
             18,
             'AVAILABLE',
             1,
             NOW(),
             NOW()
      UNION ALL
      SELECT 9,
             75.00,
             100,
             20,
             'AVAILABLE',
             1,
             NOW(),
             NOW()
      UNION ALL
      SELECT 10,
             85.00,
             85,
             15,
             'AVAILABLE',
             1,
             NOW(),
             NOW()
      UNION ALL
      SELECT 11,
             25.00,
             300,
             50,
             'AVAILABLE',
             1,
             NOW(),
             NOW()
      UNION ALL
      SELECT 12,
             35.00,
             150,
             30,
             'AVAILABLE',
             1,
             NOW(),
             NOW()
      UNION ALL
      SELECT 13,
             40.00,
             180,
             35,
             'AVAILABLE',
             1,
             NOW(),
             NOW()
      UNION ALL
      SELECT 14,
             95.00,
             45,
             10,
             'AVAILABLE',
             1,
             NOW(),
             NOW()
      UNION ALL
      SELECT 15,
             125.00,
             60,
             12,
             'AVAILABLE',
             1,
             NOW(),
             NOW()
      UNION ALL
      SELECT 16,
             350.00,
             50,
             8,
             'AVAILABLE',
             1,
             NOW(),
             NOW()
      UNION ALL
      SELECT 17,
             280.00,
             35,
             6,
             'AVAILABLE',
             1,
             NOW(),
             NOW()
      UNION ALL
      SELECT 18,
             650.00,
             25,
             5,
             'AVAILABLE',
             1,
             NOW(),
             NOW()
      UNION ALL
      SELECT 19,
             1250.00,
             15,
             3,
             'AVAILABLE',
             1,
             NOW(),
             NOW()
      UNION ALL
      SELECT 20,
             850.00,
             20,
             4,
             'AVAILABLE',
             1,
             NOW(),
             NOW()) tmp
WHERE NOT EXISTS (SELECT 1 FROM inventory);

INSERT INTO broker (first_name, last_name, discount_rate, status, tenant_id, created_date, last_modified_date)
SELECT *
FROM (SELECT 'Mehmet'      as first_name,
             'Kuruyemişçi' as last_name,
             8.00          as discount_rate,
             'ACTIVE'      as status,
             1             as tenant_id,
             NOW()         as created_date,
             NOW()         as last_modified_date
      UNION ALL
      SELECT 'Fatma', 'Badem', 6.50, 'ACTIVE', 1, NOW(), NOW()
      UNION ALL
      SELECT 'Ali', 'Fıstıkoğlu', 7.25, 'ACTIVE', 1, NOW(), NOW()
      UNION ALL
      SELECT 'Ayşe', 'Cevizci', 5.75, 'ACTIVE', 1, NOW(), NOW()
      UNION ALL
      SELECT 'Hasan', 'Leblebici', 4.50, 'ACTIVE', 1, NOW(), NOW()
      UNION ALL
      SELECT 'Zeynep', 'Organik', 9.00, 'ACTIVE', 1, NOW(), NOW()) tmp
WHERE NOT EXISTS (SELECT 1 FROM broker);

INSERT INTO sales (broker_id, document_number, total_price_with_tax, subtotal_price, total_price, discount_price,
                   discount_rate, tenant_id, created_date, last_modified_date)
SELECT *
FROM (SELECT 1                     as broker_id,
             'SAT-2024-001'        as document_number,
             918.00                as total_price_with_tax,
             850.00                as subtotal_price,
             782.00                as total_price,
             68.00                 as discount_price,
             8.00                  as discount_rate,
             1                     as tenant_id,
             '2024-01-15 10:30:00' as created_date,
             '2024-01-15 10:30:00' as last_modified_date
      UNION ALL
      SELECT 2,
             'SAT-2024-002',
             1166.25,
             1250.00,
             1166.25,
             83.75,
             6.50,
             1,
             '2024-01-16 14:20:00',
             '2024-01-16 14:20:00'
      UNION ALL
      SELECT 3,
             'SAT-2024-003',
             1400.63,
             1510.00,
             1400.63,
             109.37,
             7.25,
             1,
             '2024-01-17 09:45:00',
             '2024-01-17 09:45:00'
      UNION ALL
      SELECT 4,
             'SAT-2024-004',
             756.50,
             800.00,
             754.00,
             46.00,
             5.75,
             1,
             '2024-01-18 16:15:00',
             '2024-01-18 16:15:00'
      UNION ALL
      SELECT 5,
             'SAT-2024-005',
             515.25,
             540.00,
             515.25,
             24.75,
             4.50,
             1,
             '2024-01-19 11:30:00',
             '2024-01-19 11:30:00') tmp
WHERE NOT EXISTS (SELECT 1 FROM sales);

INSERT INTO sales_items (sales_id, product_id, unit_price, total_price, total_price_with_tax, product_count, tax_rate,
                         tax_price, tenant_id, created_date)
SELECT *
FROM (SELECT 1                     as sales_id,
             1                     as product_id,
             85.00                 as unit_price,
             425.00                as total_price,
             459.00                as total_price_with_tax,
             5                     as product_count,
             8.00                  as tax_rate,
             34.00                 as tax_price,
             1                     as tenant_id,
             '2024-01-15 10:30:00' as created_date
      UNION ALL
      SELECT 1,
             2,
             180.00,
             360.00,
             388.80,
             2,
             8.00,
             28.80,
             1,
             '2024-01-15 10:30:00'
      UNION ALL
      SELECT 2,
             5,
             320.00,
             960.00,
             1036.80,
             3,
             8.00,
             76.80,
             1,
             '2024-01-16 14:20:00'
      UNION ALL
      SELECT 2,
             6,
             450.00,
             900.00,
             972.00,
             2,
             8.00,
             72.00,
             1,
             '2024-01-16 14:20:00'
      UNION ALL
      SELECT 3,
             7,
             280.00,
             1120.00,
             1209.60,
             4,
             8.00,
             89.60,
             1,
             '2024-01-17 09:45:00'
      UNION ALL
      SELECT 3,
             8,
             220.00,
             440.00,
             475.20,
             2,
             8.00,
             35.20,
             1,
             '2024-01-17 09:45:00'
      UNION ALL
      SELECT 3,
             10,
             85.00,
             85.00,
             91.80,
             1,
             8.00,
             6.80,
             1,
             '2024-01-17 09:45:00'
      UNION ALL
      SELECT 4,
             9,
             75.00,
             450.00,
             486.00,
             6,
             8.00,
             36.00,
             1,
             '2024-01-18 16:15:00'
      UNION ALL
      SELECT 4,
             3,
             45.00,
             180.00,
             194.40,
             4,
             8.00,
             14.40,
             1,
             '2024-01-18 16:15:00'
      UNION ALL
      SELECT 5,
             4,
             65.00,
             520.00,
             561.60,
             8,
             8.00,
             41.60,
             1,
             '2024-01-19 11:30:00'
      UNION ALL
      SELECT 5,
             12,
             35.00,
             70.00,
             75.60,
             2,
             8.00,
             5.60,
             1,
             '2024-01-19 11:30:00') tmp
WHERE NOT EXISTS (SELECT 1 FROM sales_items);

INSERT INTO payment (broker_id, document_number, price, type, tenant_id, created_date, last_modified_date)
SELECT *
FROM (SELECT 1                     as broker_id,
             'PAY-2024-001'        as document_number,
             400.00                as price,
             'CASH'                as type,
             1                     as tenant_id,
             '2024-01-20 10:00:00' as created_date,
             '2024-01-20 10:00:00' as last_modified_date
      UNION ALL
      SELECT 2, 'PAY-2024-002', 600.00, 'CREDIT_CARD', 1, '2024-01-21 14:30:00', '2024-01-21 14:30:00'
      UNION ALL
      SELECT 3, 'PAY-2024-003', 700.00, 'CASH', 1, '2024-01-22 09:15:00', '2024-01-22 09:15:00'
      UNION ALL
      SELECT 4, 'PAY-2024-004', 300.00, 'CREDIT_CARD', 1, '2024-01-23 16:45:00', '2024-01-23 16:45:00'
      UNION ALL
      SELECT 5, 'PAY-2024-005', 250.00, 'CASH', 1, '2024-01-24 11:20:00', '2024-01-24 11:20:00'
      UNION ALL
      SELECT 6, 'PAY-2024-006', 800.00, 'CREDIT_CARD', 1, '2024-01-25 13:10:00', '2024-01-25 13:10:00') tmp
WHERE NOT EXISTS (SELECT 1 FROM payment);

INSERT INTO transaction (broker_id, sales_id, payment_id, document_number, price, balance, type, tenant_id,
                         created_date)
SELECT *
FROM (
         -- SALE transactions
         SELECT 1                     as broker_id,
                1                     as sales_id,
                NULL                  as payment_id,
                'SAT-2024-001'        as document_number,
                782.00                as price,
                782.00                as balance,
                'SALE'                as type,
                1                     as tenant_id,
                '2024-01-15 10:30:00' as created_date
         UNION ALL
         SELECT 2,
                2,
                NULL,
                'SAT-2024-002',
                1166.25,
                1166.25,
                'SALE',
                1,
                '2024-01-16 14:20:00'
         UNION ALL
         SELECT 3,
                3,
                NULL,
                'SAT-2024-003',
                1400.63,
                1400.63,
                'SALE',
                1,
                '2024-01-17 09:45:00'
         UNION ALL
         SELECT 4,
                4,
                NULL,
                'SAT-2024-004',
                754.00,
                754.00,
                'SALE',
                1,
                '2024-01-18 16:15:00'
         UNION ALL
         SELECT 5,
                5,
                NULL,
                'SAT-2024-005',
                515.25,
                515.25,
                'SALE',
                1,
                '2024-01-19 11:30:00'
         UNION ALL
         -- PAYMENT transactions
         SELECT 1,
                NULL,
                1,
                'PAY-2024-001',
                400.00,
                382.00,
                'PAYMENT',
                1,
                '2024-01-20 10:00:00'
         UNION ALL
         SELECT 2,
                NULL,
                2,
                'PAY-2024-002',
                600.00,
                566.25,
                'PAYMENT',
                1,
                '2024-01-21 14:30:00'
         UNION ALL
         SELECT 3,
                NULL,
                3,
                'PAY-2024-003',
                700.00,
                700.63,
                'PAYMENT',
                1,
                '2024-01-22 09:15:00'
         UNION ALL
         SELECT 4,
                NULL,
                4,
                'PAY-2024-004',
                300.00,
                454.00,
                'PAYMENT',
                1,
                '2024-01-23 16:45:00'
         UNION ALL
         SELECT 5,
                NULL,
                5,
                'PAY-2024-005',
                250.00,
                265.25,
                'PAYMENT',
                1,
                '2024-01-24 11:20:00'
         UNION ALL
         SELECT 6,
                NULL,
                6,
                'PAY-2024-006',
                800.00,
                -800.00,
                'PAYMENT',
                1,
                '2024-01-25 13:10:00') tmp
WHERE NOT EXISTS (SELECT 1 FROM transaction);
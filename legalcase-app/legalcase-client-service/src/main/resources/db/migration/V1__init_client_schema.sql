CREATE TABLE clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone_number VARCHAR(20),
    address VARCHAR(500),
    tax_id VARCHAR(20),
    identity_number VARCHAR(20),
    notes VARCHAR(1000),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Veritabanı indekslerini oluştur
CREATE INDEX idx_client_name ON clients(name);
CREATE INDEX idx_client_surname ON clients(surname);
CREATE INDEX idx_client_active ON clients(is_active);

-- Örnek veriler
INSERT INTO clients (name, surname, email, phone_number, address, tax_id, identity_number, notes, is_active)
VALUES
('Ahmet', 'Yılmaz', 'ahmet.yilmaz@example.com', '5551234567', 'Atatürk Cad. No:123 İstanbul', '1234567890', '12345678901', 'Önemli müvekkil', true),
('Ayşe', 'Demir', 'ayse.demir@example.com', '5559876543', 'Cumhuriyet Sok. No:45 Ankara', '9876543210', '98765432109', 'Yeni müvekkil', true),
('Mehmet', 'Kaya', 'mehmet.kaya@example.com', '5553334444', 'İnönü Bulvarı No:67 İzmir', '5678901234', '56789012345', 'Ticari davalar', true),
('Fatma', 'Şahin', 'fatma.sahin@example.com', '5552223333', 'Mimar Sinan Cad. No:89 Bursa', '3456789012', '34567890123', 'Aile hukuku davaları', true),
('Ali', 'Öztürk', 'ali.ozturk@example.com', '5557778888', 'Gazi Cad. No:12 Antalya', '7890123456', '78901234567', 'Aktif olmayan müvekkil', false); 
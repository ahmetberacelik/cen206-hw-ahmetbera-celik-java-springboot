# LEGALCASE MİKROSERVİS DÖNÜŞÜM PROJESİ

## 1. PROJE GENEL BAKIŞ

### 1.1. Proje Tanımı
LegalCase uygulaması, hukuk bürolarının dava takibi ve müvekkil yönetimi için geliştirilmiş bir yazılım çözümüdür. Bu proje, mevcut monolitik yapıdaki uygulamayı mikroservis mimarisine dönüştürmeyi, modern DevOps pratiklerini entegre etmeyi ve kullanıcı deneyimini iyileştirmeyi amaçlamaktadır.

### 1.2. Proje Hedefleri
- Mevcut monolitik uygulamayı mikroservis mimarisine dönüştürmek
- Docker konteynerizasyonunu tamamlamak
- Keycloak ile güvenli kimlik doğrulama ve yetkilendirme sistemi kurmak
- Swing tabanlı kullanıcı arayüzü geliştirmek
- Monitoring ve logging altyapısı oluşturmak
- Azure üzerinde Linux sunuculara dağıtım gerçekleştirmek
- CI/CD pipeline entegrasyonu sağlamak

### 1.3. Paydaşlar
- Proje Geliştiricileri
- Hukuk Büroları (Son Kullanıcılar)
- Teknik Danışmanlar

## 2. TEKNOLOJİ YIĞINI

### 2.1. Backend
- **Programlama Dili**: Java 17
- **Framework**: Spring Boot 3.x
- **API**: REST
- **Veritabanı**: MySQL 8.0
- **ORM**: Hibernate/JPA
- **Kimlik Yönetimi**: Keycloak
- **API Dokümantasyonu**: OpenAPI/Swagger

### 2.2. Frontend
- **GUI**: Java Swing
- **UI Kütüphanesi**: FlatLaf (modern görünüm için)
- **HTTP İstemci**: RestTemplate veya OkHttp

### 2.3. DevOps & Altyapı
- **Konteynerizasyon**: Docker
- **Orkestrasyon**: Docker Compose
- **CI/CD**: GitHub Actions
- **Bulut Platformu**: Azure
- **Monitoring**: Prometheus + Grafana
- **Logging**: ELK Stack veya Loki
- **Kod Kalitesi**: SonarQube

### 2.4. Test
- **Birim Test**: JUnit 5
- **Entegrasyon Test**: Spring Boot Test
- **Performans Test**: JMeter
- **Kod Kapsama**: JaCoCo

## 3. MİKROSERVİS MİMARİSİ

### 3.1. Mikroservis Stratejisi
Mevcut monolitik uygulama, aşağıdaki iş alanlarına göre mikroservislere ayrılacaktır:

1. **Kullanıcı Servisi**: Kullanıcı yönetimi ve Keycloak entegrasyonu
2. **Müvekkil Servisi**: Müvekkil bilgilerinin yönetimi
3. **Dava Servisi**: Dava bilgilerinin yönetimi
4. **Doküman Servisi**: Dava dokümanlarının yönetimi
5. **Duruşma Servisi**: Duruşma takibi ve takvim yönetimi
6. **Bildirim Servisi**: Kullanıcılara e-posta ve sistem bildirimleri gönderimi
7. **API Gateway**: Tüm servislere tek giriş noktası

### 3.2. Servis İletişim Modeli
- **Senkron İletişim**: REST API (servisler arası direkt çağrılar)
- **Asenkron İletişim**: Kafka veya RabbitMQ (ileriki aşamalarda eklenebilir)

### 3.3. Veri Yönetimi Stratejisi
- Her servis kendi veritabanına sahip olacak (Database-per-Service)
- Veri tutarlılığı için Saga pattern kullanılabilir
- Veritabanı şema yönetimi için Flyway kullanılacak

## 4. UYGULAMA GELİŞTİRME PLANI

### 4.1. Faz 1: Altyapı ve Temel Özellikler (2-4 Hafta)
- Docker ve veritabanı entegrasyonu tamamlanması
- Keycloak kimlik doğrulama kurulumu
- Temel domain modellerinin oluşturulması
- JPA repository ve servis sınıflarının oluşturulması
- Temel REST API'lerin geliştirilmesi

### 4.2. Faz 2: Mikroservis Dönüşümü (4-6 Hafta)
- Ortak bileşenler için core kütüphanesi oluşturulması
- İlk mikroservisin (Dava Servisi) ayrılması ve bağımsız çalışır hale getirilmesi
- Servisler arası iletişim protokollerinin tanımlanması
- Diğer mikroservislerin kademeli olarak oluşturulması
- API Gateway yapılandırması

### 4.3. Faz 3: Swing GUI Geliştirmesi (4-6 Hafta)
- MVC mimarisi ile GUI tasarımı
- Backend API entegrasyonu
- Kullanıcı kimlik doğrulama ve yetkilendirme akışı
- Form validasyonları ve hata işleme
- Kullanıcı deneyimi iyileştirmeleri

### 4.4. Faz 4: Monitoring, Logging ve DevOps (2-4 Hafta)
- Prometheus ve Grafana kurulumu
- Centralized logging sistemi kurulumu
- CI/CD pipeline oluşturulması
- Kod kalitesi araçlarının entegrasyonu
- Otomatik test süreçlerinin yapılandırılması

### 4.5. Faz 5: Bulut Dağıtımı ve Optimizasyon (2-4 Hafta)
- Azure Linux sunucu kurulumu
- Konteyner dağıtımı için yapılandırma
- Güvenlik taraması ve düzeltmeleri
- Performans optimizasyonu
- Yük testleri ve ölçeklendirme ayarları

## 5. MİKROSERVİS DETAYLARI

### 5.1. Kullanıcı Servisi (User Service)
**Sorumluluklar:**
- Kullanıcı hesap yönetimi
- Rol ve izin yönetimi
- Kimlik doğrulama ve token işleme
- Keycloak entegrasyonu

**API Uç Noktaları:**
- `POST /api/v1/users`: Yeni kullanıcı oluşturma
- `GET /api/v1/users/{id}`: Kullanıcı bilgilerini getirme
- `PUT /api/v1/users/{id}`: Kullanıcı bilgilerini güncelleme
- `DELETE /api/v1/users/{id}`: Kullanıcı silme
- `POST /api/v1/auth/login`: Kullanıcı girişi
- `POST /api/v1/auth/logout`: Kullanıcı çıkışı
- `POST /api/v1/auth/refresh`: Token yenileme

### 5.2. Müvekkil Servisi (Client Service)
**Sorumluluklar:**
- Müvekkil bilgilerinin yönetimi
- Müvekkil iletişim kayıtları
- Müvekkil ilişkili dokümanlar

**API Uç Noktaları:**
- `POST /api/v1/clients`: Yeni müvekkil oluşturma
- `GET /api/v1/clients`: Tüm müvekkilleri listeleme
- `GET /api/v1/clients/{id}`: Müvekkil detaylarını getirme
- `PUT /api/v1/clients/{id}`: Müvekkil bilgilerini güncelleme
- `DELETE /api/v1/clients/{id}`: Müvekkil silme

### 5.3. Dava Servisi (Case Service)
**Sorumluluklar:**
- Dava kayıtları yönetimi
- Dava durumu takibi
- Dava ile ilgili işlemlerin kaydı

**API Uç Noktaları:**
- `POST /api/v1/cases`: Yeni dava oluşturma
- `GET /api/v1/cases`: Tüm davaları listeleme
- `GET /api/v1/cases/{id}`: Dava detaylarını getirme
- `PUT /api/v1/cases/{id}`: Dava bilgilerini güncelleme
- `DELETE /api/v1/cases/{id}`: Dava silme
- `GET /api/v1/cases/client/{clientId}`: Müvekkile ait davaları listeleme

### 5.4. Doküman Servisi (Document Service)
**Sorumluluklar:**
- Doküman yükleme ve indirme
- Doküman versiyonlama
- Doküman kategorilendirme

**API Uç Noktaları:**
- `POST /api/v1/documents`: Yeni doküman yükleme
- `GET /api/v1/documents`: Tüm dokümanları listeleme
- `GET /api/v1/documents/{id}`: Doküman detaylarını getirme
- `GET /api/v1/documents/{id}/download`: Doküman indirme
- `PUT /api/v1/documents/{id}`: Doküman bilgilerini güncelleme
- `DELETE /api/v1/documents/{id}`: Doküman silme
- `GET /api/v1/documents/case/{caseId}`: Davaya ait dokümanları listeleme

### 5.5. Duruşma Servisi (Hearing Service)
**Sorumluluklar:**
- Duruşma takvimi yönetimi
- Duruşma hatırlatıcıları
- Duruşma notları

**API Uç Noktaları:**
- `POST /api/v1/hearings`: Yeni duruşma oluşturma
- `GET /api/v1/hearings`: Tüm duruşmaları listeleme
- `GET /api/v1/hearings/{id}`: Duruşma detaylarını getirme
- `PUT /api/v1/hearings/{id}`: Duruşma bilgilerini güncelleme
- `DELETE /api/v1/hearings/{id}`: Duruşma silme
- `GET /api/v1/hearings/case/{caseId}`: Davaya ait duruşmaları listeleme
- `GET /api/v1/hearings/upcoming`: Yaklaşan duruşmaları listeleme

### 5.6. Bildirim Servisi (Notification Service)
**Sorumluluklar:**
- E-posta bildirimleri
- Sistem içi bildirimler
- Bildirim tercihleri yönetimi

**API Uç Noktaları:**
- `POST /api/v1/notifications`: Yeni bildirim gönderme
- `GET /api/v1/notifications`: Kullanıcının bildirimlerini listeleme
- `PUT /api/v1/notifications/{id}/read`: Bildirimi okundu olarak işaretleme
- `GET /api/v1/notifications/preferences`: Bildirim tercihlerini getirme
- `PUT /api/v1/notifications/preferences`: Bildirim tercihlerini güncelleme

### 5.7. API Gateway
**Sorumluluklar:**
- Tüm servislere tek giriş noktası sunmak
- Yetkilendirme kontrolü
- İstek yönlendirme
- Rate limiting
- Request/response dönüşümü

## 6. DOCKER VE KONTEYNER YAPISI

### 6.1. Docker Compose Yapılandırması
Docker Compose ile aşağıdaki servisler orkestrasyonu sağlanacaktır:

- **Veritabanı**: MySQL 8.0
- **Kimlik Yönetimi**: Keycloak
- **API Gateway**: Spring Cloud Gateway
- **Mikroservisler**: Her biri ayrı konteyner
- **Monitoring**: Prometheus ve Grafana
- **Logging**: ELK Stack veya Loki
- **Yönetim Araçları**: phpMyAdmin

### 6.2. Uygulama Konteynerizasyonu
Her mikroservis için çok aşamalı (multi-stage) Docker yapılandırması:

1. **Build aşaması**: Maven ile uygulamayı derler
2. **Çalışma aşaması**: JRE içeren minimal image ile uygulamayı çalıştırır

### 6.3. Veritabanı Konteynerizasyonu
- Veritabanı şemaları ve başlangıç verileri için init scriptleri
- Volume yapılandırması ile veri kalıcılığı
- Sağlık kontrolleri

### 6.4. Keycloak Konteynerizasyonu
- Realm yapılandırması içeren import dosyaları
- Client yapılandırmaları
- Rol ve kullanıcı tanımları

## 7. TEST STRATEJİSİ

### 7.1. Birim Testleri
- Her servisteki temel iş mantığı için birim testler
- Mock nesneler ile bağımlılıkların izolasyonu
- JUnit 5 ve Mockito kullanımı

### 7.2. Entegrasyon Testleri
- Servis-veritabanı entegrasyon testleri
- API entegrasyon testleri
- TestContainers ile gerçek veritabanı kullanımı

### 7.3. Uçtan Uca Testler
- Temel kullanıcı senaryoları için uçtan uca testler
- Selenium veya Playwright ile GUI testleri

### 7.4. Performans Testleri
- JMeter ile yük testleri
- Stres testleri
- Dayanıklılık testleri

### 7.5. Güvenlik Testleri
- OWASP ZAP ile güvenlik taraması
- Dependency check
- Token ve yetkilendirme testleri

## 8. CI/CD VE DEVOPS

### 8.1. GitHub Actions Workflow
- **Build**: Maven ile derleme ve test
- **Test**: JUnit testlerinin çalıştırılması
- **Statik Kod Analizi**: SonarQube entegrasyonu
- **Vulnerability Scan**: OWASP Dependency Check
- **Docker Image Build**: Her mikroservis için image oluşturma
- **Docker Image Push**: Container Registry'ye gönderme
- **Deployment**: Azure'a dağıtım

### 8.2. Ortam Yapılandırması
- **Dev**: Geliştirme ortamı
- **Test**: Test ortamı
- **Staging**: Ön üretim ortamı
- **Production**: Üretim ortamı

### 8.3. Deployment Stratejisi
- **Canary Deployment**: Yeni sürümün kademeli olarak devreye alınması
- **Blue-Green Deployment**: Sıfır kesinti süreli dağıtım
- **Rollback Mekanizması**: Sorun durumunda hızlı geri dönüş

## 9. MONITORING VE LOGGING

### 9.1. Prometheus ve Grafana
- **Metrik Toplama**: Servis performans metrikleri
- **Dashboard**: Özelleştirilmiş izleme dashboardları
- **Alerts**: Anormal durumlar için uyarı mekanizması

### 9.2. Centralized Logging
- **Log Formatı**: Yapılandırılmış JSON formatı
- **Log Toplama**: Loki veya ELK Stack
- **Log Analizi**: Kibana veya Grafana

### 9.3. Health Checks
- Her servis için health check endpoint'leri
- Servis bağımlılıkları için health check
- Kubernetes readiness/liveness probe entegrasyonu

## 10. GÜVENLİK YAPISI

### 10.1. Kimlik Doğrulama ve Yetkilendirme
- **Keycloak**: OAuth2/OpenID Connect protokolleri
- **JWT**: Stateless token yönetimi
- **RBAC**: Rol tabanlı erişim kontrolü

### 10.2. API Güvenliği
- **Rate Limiting**: Aşırı isteklere karşı koruma
- **Input Validation**: Girdilerin doğrulanması
- **CORS**: Cross-Origin Resource Sharing yapılandırması
- **HTTPS**: Tüm ortamlarda SSL/TLS kullanımı

### 10.3. Veri Güvenliği
- **Encryption at Rest**: Hassas verilerin şifrelenmesi
- **Encryption in Transit**: HTTPS/TLS kullanımı
- **Data Masking**: Hassas verilerin maskelenmesi

## 11. SWING GUI MİMARİSİ

### 11.1. MVC Yapısı
- **Model**: Veri modelleri ve iş mantığı
- **View**: Swing bileşenleri ve ekranlar
- **Controller**: Kullanıcı etkileşimi ve uygulama akışı

### 11.2. Ekran Tasarımları
- **Login/Register**: Kullanıcı girişi ve kayıt
- **Dashboard**: Ana gösterge paneli
- **Case Management**: Dava yönetimi ekranları
- **Client Management**: Müvekkil yönetimi ekranları
- **Document Management**: Doküman yönetimi ekranları
- **Hearing Calendar**: Duruşma takvimi

### 11.3. Backend Entegrasyonu
- **HTTP Client**: Backend API'leri ile iletişim
- **Authentication**: Token yönetimi ve yenileme
- **Offline Mode**: Çevrimdışı çalışma desteği (ileriki aşamalarda)

## 12. PROJE TAKİP VE YÖNETİM

### 12.1. Çıktı Yönetimi
- **Milestone**: Her faz için belirlenen kilometre taşları
- **Release**: Sürüm yönetimi ve sürüm notları
- **Documentation**: Teknik ve kullanıcı dokümantasyonu

### 12.2. Risk Yönetimi
- **Teknik Riskler**: Mimari ve teknoloji riskleri
- **Zaman Riskleri**: Takvim ve teslimat riskleri
- **Kaynak Riskleri**: İnsan kaynağı ve altyapı riskleri

### 12.3. Kalite Güvence
- **Code Review**: Kod inceleme süreci
- **Static Analysis**: Statik kod analizi
- **Test Coverage**: Test kapsama hedefleri
- **Performance Benchmarks**: Performans kriterleri

## 13. GELECEK GELİŞTİRMELER

### 13.1. Potansiyel Özellikler
- **Mobile App**: Mobil uygulama
- **AI/ML**: Otomatik belge sınıflandırma ve analiz
- **Reporting**: Gelişmiş raporlama özellikleri
- **Billing Integration**: Faturalandırma entegrasyonu

### 13.2. Teknoloji Yol Haritası
- **Reactive Programming**: Reactive streams entegrasyonu
- **GraphQL**: REST API alternatifi olarak GraphQL
- **Kubernetes**: Container orkestrasyonu için Kubernetes geçişi

## 14. KAYNAKLAR VE REFERANSLAR

- Spring Boot: https://spring.io/projects/spring-boot
- Microservices.io: https://microservices.io/
- Keycloak: https://www.keycloak.org/documentation
- Docker: https://docs.docker.com/
- Prometheus: https://prometheus.io/docs/
- GitHub Actions: https://docs.github.com/en/actions 
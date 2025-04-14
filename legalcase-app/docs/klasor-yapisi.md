# LEGALCASE MİKROSERVİS PROJESİ KLASÖR YAPISI

Bu dokümantasyon, LegalCase mikroservis projesinin klasör yapısını ve organizasyonunu detaylı olarak açıklamaktadır. Bu yapı, tüm mikroservislerde tutarlı bir kod organizasyonu sağlamak için kullanılmalıdır.

## 1. GENEL PROJE YAPISI

Mikroservis mimarisine geçiş yapıldığında, proje aşağıdaki ana bileşenlerden oluşacaktır:

```
legalcase/
├── legalcase-commons/               # Paylaşılan kütüphane
├── legalcase-gateway/               # API Gateway
├── legalcase-user-service/          # Kullanıcı Servisi
├── legalcase-client-service/        # Müvekkil Servisi
├── legalcase-case-service/          # Dava Servisi
├── legalcase-document-service/      # Doküman Servisi
├── legalcase-hearing-service/       # Duruşma Servisi
├── legalcase-notification-service/  # Bildirim Servisi
├── legalcase-ui/                    # Swing GUI Uygulaması
├── docker/                          # Docker dosyaları
├── docs/                            # Proje dokümantasyonu
└── infrastructure/                  # Altyapı kodları ve konfigürasyonları
```

## 2. MİKROSERVİS YAPISI

Her bir mikroservisin klasör yapısı aşağıdaki gibi olmalıdır. Bu örnek `legalcase-case-service` için gösterilmiştir:

```
legalcase-case-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── legalcase/
│   │   │           └── case/
│   │   │               ├── api/                 # REST API Controller
│   │   │               │   ├── controller/      # Controller sınıfları
│   │   │               │   ├── advice/          # Exception handler
│   │   │               │   └── request/         # İstek DTO'ları
│   │   │               │   └── response/        # Yanıt DTO'ları
│   │   │               ├── application/         # İş mantığı
│   │   │               │   ├── service/         # Servis sınıfları
│   │   │               │   ├── mapper/          # Entity-DTO dönüşümleri
│   │   │               │   └── exception/       # İş mantığı istisnaları
│   │   │               ├── domain/              # Domain modelleri
│   │   │               │   ├── entity/          # JPA Entity'leri
│   │   │               │   ├── repository/      # Repository sınıfları
│   │   │               │   ├── event/           # Domain event'leri
│   │   │               │   └── valueobject/     # Değer nesneleri
│   │   │               ├── infrastructure/      # Altyapı kodları
│   │   │               │   ├── config/          # Konfigürasyon
│   │   │               │   ├── client/          # Diğer servislere bağlantı
│   │   │               │   ├── security/        # Güvenlik yapılandırması
│   │   │               │   └── persistence/     # Veritabanı yapılandırması
│   │   │               └── CaseServiceApplication.java  # Ana uygulama sınıfı
│   │   └── resources/
│   │       ├── application.yml                  # Uygulama ayarları
│   │       ├── application-dev.yml              # Dev ortamı ayarları
│   │       ├── application-prod.yml             # Prod ortamı ayarları
│   │       ├── db/                              # DB migration dosyaları
│   │       │   └── migration/                   # Flyway migration
│   │       └── static/                          # Statik dosyalar (varsa)
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── legalcase/
│       │           └── case/
│       │               ├── api/                 # API testleri
│       │               ├── application/         # Servis testleri
│       │               ├── domain/              # Domain testleri
│       │               └── infrastructure/      # Altyapı testleri
│       └── resources/
│           ├── application-test.yml             # Test ayarları
│           └── test-data/                       # Test verileri
├── Dockerfile                                  # Docker image oluşturma
├── pom.xml                                     # Maven yapılandırması
└── README.md                                   # Servis dokümantasyonu
```

## 3. COMMONS KÜTÜPHANE YAPISI

Mikroservislerin paylaştığı ortak kodları içeren kütüphane:

```
legalcase-commons/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── legalcase/
│   │   │           └── commons/
│   │   │               ├── dto/                # Paylaşılan DTO'lar
│   │   │               ├── exception/          # Ortak exception'lar
│   │   │               ├── security/           # Güvenlik yardımcıları
│   │   │               ├── util/               # Yardımcı sınıflar
│   │   │               ├── validation/         # Doğrulama araçları
│   │   │               └── web/                # Web araçları
│   │   └── resources/
│   └── test/
├── pom.xml
└── README.md
```

## 4. API GATEWAY YAPISI

Tüm servislere merkezi giriş noktası sağlayan API Gateway:

```
legalcase-gateway/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── legalcase/
│   │   │           └── gateway/
│   │   │               ├── config/             # Yapılandırma
│   │   │               ├── filter/             # Filtreler
│   │   │               ├── security/           # Güvenlik yapılandırması
│   │   │               └── GatewayApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── Dockerfile
├── pom.xml
└── README.md
```

## 5. SWING GUI UYGULAMASI YAPISI

Masaüstü uygulaması için Swing GUI:

```
legalcase-ui/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── legalcase/
│   │   │           └── ui/
│   │   │               ├── application/        # Uygulama mantığı
│   │   │               │   ├── service/        # API iletişim servisleri
│   │   │               │   └── util/           # Yardımcı sınıflar
│   │   │               ├── model/              # Veri modelleri
│   │   │               ├── view/               # Ekranlar ve paneller
│   │   │               │   ├── auth/           # Kimlik doğrulama ekranları
│   │   │               │   ├── case/           # Dava ekranları
│   │   │               │   ├── client/         # Müvekkil ekranları
│   │   │               │   ├── document/       # Doküman ekranları
│   │   │               │   ├── hearing/        # Duruşma ekranları
│   │   │               │   ├── dashboard/      # Ana panel ekranları
│   │   │               │   └── common/         # Ortak bileşenler
│   │   │               ├── controller/         # Controller sınıfları
│   │   │               │   ├── auth/           # Kimlik doğrulama kontrolcüleri
│   │   │               │   ├── case/           # Dava kontrolcüleri
│   │   │               │   ├── client/         # Müvekkil kontrolcüleri
│   │   │               │   ├── document/       # Doküman kontrolcüleri
│   │   │               │   └── hearing/        # Duruşma kontrolcüleri
│   │   │               ├── config/             # Konfigürasyon
│   │   │               ├── security/           # Güvenlik yönetimi
│   │   │               └── LegalCaseUI.java    # Ana uygulama sınıfı
│   │   └── resources/
│   │       ├── config/                         # Yapılandırma dosyaları
│   │       ├── icons/                          # İkon dosyaları
│   │       ├── images/                         # Resim dosyaları
│   │       └── styles/                         # Stil dosyaları
│   └── test/
├── pom.xml
└── README.md
```

## 6. DOCKER YAPISI

Docker dosyaları ve konfigürasyonları:

```
docker/
├── docker-compose.yml                          # Temel docker-compose
├── docker-compose.dev.yml                      # Geliştirme ortamı için
├── docker-compose.prod.yml                     # Üretim ortamı için
├── mysql/                                      # MySQL konfigürasyonu
│   ├── init-keycloak-db.sql
│   └── my.cnf
├── keycloak/                                   # Keycloak konfigürasyonu
│   └── import/
│       └── legalcase-realm.json
├── prometheus/                                 # Prometheus konfigürasyonu
│   └── prometheus.yml
├── grafana/                                    # Grafana konfigürasyonu
│   ├── dashboards/
│   └── datasources/
└── loki/                                       # Loki konfigürasyonu
    └── loki-config.yaml
```

## 7. INFRASTRUCTURE YAPISI

Altyapı konfigürasyonları ve dağıtım scriptleri:

```
infrastructure/
├── azure/                                      # Azure deployment
│   ├── templates/                              # ARM şablonları
│   └── scripts/                                # Deployment scriptleri
├── kubernetes/                                 # Kubernetes konfigürasyonu
│   ├── deployments/                            # Deployment YAML'ları
│   ├── services/                               # Service YAML'ları
│   └── config-maps/                            # ConfigMap YAML'ları
├── scripts/                                    # Genel yardımcı scriptler
└── monitoring/                                 # Monitoring konfigürasyonu
```

## 8. DOCS YAPISI

Proje dokümantasyonu:

```
docs/
├── architecture/                               # Mimari dokümantasyonu
│   ├── diagrams/                               # UML ve mimari diyagramlar
│   └── decisions/                              # Mimari kararlar
├── api/                                        # API dokümantasyonu
├── setup/                                      # Kurulum kılavuzları
├── user-guides/                                # Kullanıcı kılavuzları
└── development/                                # Geliştirici kılavuzları
```

## 9. GIT YAPISI

Git ile ilgili konfigürasyonlar:

```
.github/
├── workflows/                                  # GitHub Actions workflow'ları
│   ├── build.yml
│   ├── test.yml
│   └── deploy.yml
├── ISSUE_TEMPLATE/                             # Issue şablonları
└── PULL_REQUEST_TEMPLATE.md                    # PR şablonu
```

## 10. KOD DÜZENLEME VE FORMATLAMA YAPISI

Kod düzenleme ve formatlama konfigürasyonları:

```
├── .editorconfig                               # Editor konfigürasyonu
├── .checkstyle.xml                             # Checkstyle konfigürasyonu
└── .spotless.xml                               # Spotless konfigürasyonu
```

Bu klasör yapısı, mikroservis mimarisine geçiş sürecinde tüm servislerde tutarlı bir kod organizasyonu oluşturmak için referans olarak kullanılmalıdır. Gerçek geliştirme sürecinde, projenin özel ihtiyaçlarına göre bazı ayarlamalar yapılabilir. 
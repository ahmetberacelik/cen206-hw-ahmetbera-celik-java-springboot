# Veritabanı Şema Yönetimi

## Genel Bakış

Bu projede veritabanı şema yönetimi Flyway ile gerçekleştirilmektedir. Bu yaklaşım, veritabanı değişikliklerinin sürüm kontrolü altında tutulmasını ve farklı ortamlarda (geliştirme, test, üretim) tutarlı bir şekilde uygulanmasını sağlar.

## Flyway Konfigürasyonu

Tüm servisler için Flyway yapılandırması etkinleştirilmiştir:

```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
```

## Hibernate Yapılandırması

Tüm servislerde Hibernate'in otomatik şema oluşturma ve güncelleme özelliği devre dışı bırakılmıştır:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

Bu ayar, Hibernate'in mevcut şemayı doğrulamasını sağlar, ancak otomatik olarak şemayı değiştirmesini önler. Tüm şema değişiklikleri Flyway migrasyonları aracılığıyla yapılmalıdır.

## Migrasyon Dosyaları

Migrasyon dosyaları şu konumda bulunur:
```
src/main/resources/db/migration/
```

Her migrasyon dosyası, Flyway'in adlandırma kuralına uygun şekilde adlandırılmalıdır:
```
V{version}__{description}.sql
```

Örneğin:
- `V1__create_users_table.sql`
- `V2__add_indexes_to_users.sql`

## Migrasyon İşlemi

Uygulama başlatıldığında, Flyway şu adımları izler:

1. `flyway_schema_history` tablosunu kontrol eder (yoksa oluşturur)
2. Uygulanmamış migrasyonları tespit eder
3. Migrasyonları versiyon numarasına göre sıralı bir şekilde uygular
4. Her başarılı migrasyonu `flyway_schema_history` tablosuna kaydeder

## Yeni Migrasyon Ekleme

Yeni bir veritabanı değişikliği gerektiren bir özellik geliştirirken:

1. Yeni bir migrasyon dosyası oluşturun (`V{next_version}__{description}.sql`)
2. Değişikliklerinizi bu dosyada SQL olarak tanımlayın
3. Uygulamayı başlatın, Flyway migrasyonu otomatik olarak uygulayacaktır

## Önemli Notlar

- **Üretim ortamında ddl-auto: update kullanılmamalıdır.** Bu tehlikeli bir yapılandırmadır ve veri kaybına yol açabilir.
- Mevcut migrasyon dosyaları asla değiştirilmemelidir; bunun yerine yeni migrasyonlar eklemelisiniz.
- Migrasyon dosyalarınızı her zaman test ortamında çalıştırdıktan sonra üretim ortamına taşıyın.
- Büyük migrasyonları uygulama saatleri dışında planlamayı düşünün.

## Sorun Giderme

Eğer migrasyon sırasında bir hata alırsanız:

1. Hata mesajını dikkatlice okuyun
2. Geliştirme ortamında sorunu giderin
3. Eğer migrasyon zaten uygulandıysa ve düzeltilmesi gerekiyorsa, bir düzeltme migrasyonu ekleyin; mevcut migrasyonu değiştirmeyin. 
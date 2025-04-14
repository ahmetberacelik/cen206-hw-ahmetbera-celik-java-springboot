# LegalCase UML Diyagramları

Bu dizin, LegalCase mikroservis projesi için UML diyagramlarını içermektedir. Diyagramlar, PlantUML formatında hazırlanmıştır ve kolayca görselleştirilebilir.

## Diyagram Listesi

1. **mikroservis-mimarisi.puml**: Sistemin genel mikroservis mimarisini gösteren bileşen diyagramı
2. **case-service-classes.puml**: Dava Servisi için detaylı sınıf diyagramı
3. **kimlik-dogrulama-sequence.puml**: Kimlik doğrulama ve yetkilendirme akışını gösteren sıralama diyagramı
4. **dava-olusturma-activity.puml**: Yeni dava oluşturma sürecini gösteren aktivite diyagramı
5. **veritabani-er.puml**: Veritabanı şemalarını gösteren ER diyagramı
6. **deployment-diagram.puml**: Azure üzerindeki dağıtım mimarisini gösteren dağıtım diyagramı
7. **swing-ui-class.puml**: Swing GUI uygulaması için sınıf diyagramı

## PlantUML Diyagramlarını Görselleştirme

Bu PlantUML dosyalarını görselleştirmek için aşağıdaki yöntemleri kullanabilirsiniz:

### 1. Online PlantUML Servisi

PlantUML dosyalarını [PlantUML Online Server](http://www.plantuml.com/plantuml/uml/) üzerinde görselleştirmek için:

1. PlantUML dosyasının içeriğini kopyalayın
2. http://www.plantuml.com/plantuml/uml/ adresine gidin
3. İçeriği metin alanına yapıştırın
4. Sonuç, otomatik olarak gösterilecektir

### 2. IDE Eklentileri

Popüler IDE'ler için PlantUML eklentileri mevcuttur:

- **IntelliJ IDEA / WebStorm**: "PlantUML integration" eklentisi
- **Visual Studio Code**: "PlantUML" eklentisi
- **Eclipse**: "PlantUML Encoder" eklentisi

### 3. Yerel Kurulum

PlantUML'i yerel olarak çalıştırmak için:

1. Java JRE yükleyin (en az sürüm 8)
2. [PlantUML JAR dosyasını](https://plantuml.com/download) indirin
3. Aşağıdaki komutu çalıştırın:
   
   ```bash
   java -jar plantuml.jar dosya_adı.puml
   ```

4. Aynı dizinde PNG/SVG çıktısı oluşacaktır

### 4. Dökümanlarda Kullanım

Bu diyagramların PNG veya SVG çıktılarını diğer proje dökümantasyonlarında kullanabilirsiniz. Diyagramları güncellemek istediğinizde, PlantUML dosyasını düzenleyip yeniden render edin.

## Diyagramları Güncelleme

Proje gereksinimleri değiştikçe bu diyagramlar da güncellenmelidir. Bir diyagramı güncellemek için:

1. İlgili PlantUML dosyasını açın
2. Gerekli değişiklikleri yapın
3. Yeni bir görsele dönüştürün
4. Gerekirse ilgili dökümantasyonu güncelleyin

## Diyagram Standartları

Bu diyagramlar aşağıdaki standartları izler:
- UML 2.5+ notasyonu
- Tutarlı renk şeması ve stillemesi
- Açık ve anlaşılır notlar

---

PlantUML hakkında daha fazla bilgi için [resmi PlantUML web sitesini](https://plantuml.com/) ziyaret edin. 
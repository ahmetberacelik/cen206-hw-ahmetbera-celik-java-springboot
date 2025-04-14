# UML Diyagram Görselleri

Bu klasör, PlantUML dosyalarından oluşturulan UML diyagramlarının PNG/SVG görsellerini içermektedir. 

## Oluşturma Süreci

Bu görseller, PlantUML CLI kullanılarak oluşturulmuştur:

```bash
java -jar ../plantuml.jar ../*.puml
```

## Görsel Listesi

1. mikroservis-mimarisi.png - Sistemin genel mikroservis mimarisini gösteren bileşen diyagramı
2. case-service-classes.png - Dava Servisi için detaylı sınıf diyagramı
3. kimlik-dogrulama-sequence.png - Kimlik doğrulama ve yetkilendirme akışını gösteren sıralama diyagramı
4. dava-olusturma-activity.png - Yeni dava oluşturma sürecini gösteren aktivite diyagramı
5. veritabani-er.png - Veritabanı şemalarını gösteren ER diyagramı
6. deployment-diagram.png - Azure üzerindeki dağıtım mimarisini gösteren dağıtım diyagramı
7. swing-ui-class.png - Swing GUI uygulaması için sınıf diyagramı

## Kullanım

Bu görseller, proje dokümantasyonunda kullanılabilir. Markdown belgelerinde aşağıdaki gibi referans verilebilir:

```markdown
![Mikroservis Mimarisi](./uml-diyagramlari/images/mikroservis-mimarisi.png)
``` 
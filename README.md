# High-Concurrency Ticketing System (Yüksek Trafikli Biletleme Sistemi)

Bu proje, yüksek eşzamanlılık (high concurrency) altında çalışabilen ölçeklenebilir bir bilet satış/rezervasyon sisteminin geliştirilmesini konu almaktadır. Proje, aşamalı olarak (step-by-step) inşa edilmektedir.

## Aşamalar

- **Aşama 1 (Mevcut):** Temel REST API ve PostgreSQL Veritabanı İskeleti. Eşzamanlılık kontrolü olmadan doğrudan veritabanı kayıt işlemleri.
- **Aşama 2 (Gelecek):** Concurrency Lock Kontrolleri (Optimistic/Pessimistic Locking, Redis Distributed Lock vb.).
- **Aşama 3 (Gelecek):** Kuyruk ve Mesajlaşma Altyapısı (Kafka/RabbitMQ) ile asenkron bilet işleme.
- **Aşama 4 (Gelecek):** Dağıtık Mimariler, Docker, Performans Testleri (JMeter/Gatling).

---

## Aşama 1: Temel REST API ve Veri Tabanı

Bu ilk aşamada, sistemin en temel bileşenleri ve veritabanı ilişkileri kurulmuştur. Herhangi bir Redis, Kafka veya Docker bağımlılığı yoktur. Gelen istekler doğrudan ilişkisel veritabanına kaydedilir.

### Kullanılan Teknolojiler

- **Java 25**
- **Spring Boot 3.3.3**
- **Spring Data JPA**
- **PostgreSQL**
- **Lombok**

### Veri Modeli ve İlişkiler

- **User (Kullanıcı):** Bilet satın alacak kullanıcıları temsil eder. Bir kullanıcının birden fazla bileti olabilir (`One-to-Many`).
- **Event (Etkinlik):** Bilet satışı yapılacak etkinlikleri (örneğin konser, maç) temsil eder. Kapasite (`availableCapacity`) bilgisi barındırır.
- **Ticket (Bilet):** Satın alınan her bir bileti temsil eder. `User` ve `Event` ile `@ManyToOne` ilişkisine sahiptir.

---

## Kurulum ve Çalıştırma

### 1. Veritabanı Hazırlığı
PostgreSQL sunucunuzda `ticketing` adında bir veritabanı oluşturun:
```sql
CREATE DATABASE ticketing;
```

`src/main/resources/application.yml` dosyasındaki veritabanı kullanıcı adı ve şifresini kendi ortamınıza göre güncelleyin:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ticketing
    username: <kullanici_adiniz>
    password: <sifreniz>
```

### 2. Uygulamayı Başlatma
Projeyi favori IDE'nizde (IntelliJ IDEA, VS Code vb.) içe aktarıp `TicketingApplication` sınıfını çalıştırabilirsiniz.

Veya terminalden:
```bash
mvn spring-boot:run
```

Proje ilk ayağa kalktığında `DataInitializer` sınıfı otomatik olarak veritabanında test verisi oluşturur:
- **Test Kullanıcısı:** `johndoe` (ID: 1)
- **Test Etkinliği:** `Rock Concert 2026` (ID: 1, Başlangıç Kapasitesi: 100)

---

## API Uç Noktaları (Endpoints)

### 1. Etkinlikleri Listeleme (GET `/api/events`)
Sistemdeki tüm etkinlikleri ve kalan bilet kapasitelerini döner.

```bash
curl -X GET http://localhost:8080/api/events
```

### 2. Bilet Satın Alma (POST `/api/tickets`)
Belirli bir kullanıcı için belirli bir etkinliğe bilet satın alır. İşlem başarılı olursa etkinlik kapasitesi 1 azaltılır.

**İstek (Request Body):**
```json
{
  "eventId": 1,
  "userId": 1
}
```

**cURL Örneği:**
```bash
curl -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{"eventId": 1, "userId": 1}'
```

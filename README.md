# ⚡ High-Concurrency Ticketing System (Yüksek Eşzamanlılıklı Bubilet Biletleme Platformu)

![Architecture](https://img.shields.io/badge/Architecture-Event--Driven-brightgreen)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.3-green)
![Redis](https://img.shields.io/badge/Redis-Capacity%20Gatekeeper-red)
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-Async%20Queue-black)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![Docker](https://img.shields.io/badge/Docker-Containers-cyan)

Bu proje, binlerce eşzamanlı kullanıcının aynı anda bilet almaya çalıştığı (flash sale / yüksek trafik) senaryolarda veritabanı kilitlenmelerini ve çökmelerini engellemek amacıyla tasarlanmış **ultra hızlı, asenkron ve ölçeklenebilir bilet satış platformudur**.

Ön yüz mimarisi, Türkiye'nin lider biletleme platformlarından **Bubilet** arayüzü referans alınarak modern, dinamik ve etkileşimli bir kullanıcı deneyimi sunacak şekilde geliştirilmiştir.

---

## 🏗️ Mimari ve Yüksek Eşzamanlılık Tasarımı (High Concurrency Flow)

İlişkisel veritabanlarına (RDBMS) binlerce anlık istek doğrudan gönderildiğinde veritabanı kilitlenmeleri (lock contention) ve `Deadlock` hataları meydana gelir. Bu projedeki 3 aşamalı koruma mimarisi şu şekildedir:

```text
                               ┌───────────────────────────────────┐
                               │     Bubilet Web Interface (UI)    │
                               └─────────────────┬─────────────────┘
                                                 │
                                                 ▼ (1. Bilet Talebi - POST /api/tickets)
                               ┌───────────────────────────────────┐
                               │     Spring Boot REST Controller   │
                               └─────────────────┬─────────────────┘
                                                 │
                                                 ▼
                       ┌──────────────────────────────────────────────────┐
                       │  2. REDIS GATEKEEPER (Atomic DECR)               │
                       │  - Kapasite kontrolü bellek içinde 1ms'de yapılır│
                       │  - Stok bittiyse: Fail-Fast (Anında Red)          │
                       └─────────────────────────┬────────────────────────┘
                                                 │ (Stok Varsa: Onaylandı)
                                                 ▼
                       ┌──────────────────────────────────────────────────┐
                       │  3. APACHE KAFKA (Topic: ticket-bookings)        │
                       │  - Talebi asenkron kuyruğa aktarır               │
                       │  - DB yazma yükünü sönümler ve sıralar           │
                       └─────────────────────────┬────────────────────────┘
                                                 │
                                                 ▼
                       ┌──────────────────────────────────────────────────┐
                       │  4. KAFKA LISTENER & PERSISTENCE WORKER          │
                       │  - Mesajları sırayla tüketir                     │
                       │  - PostgreSQL veritabanına bilet kaydını atar    │
                       └─────────────────────────┬────────────────────────┘
                                                 │
                                                 ▼
                               ┌───────────────────────────────────┐
                               │   PostgreSQL Relational DB        │
                               └───────────────────────────────────┘
```

### ⚡ Öne Çıkan Performans Özellikleri

1. **Redis Fail-Fast Gatekeeper:** Bilet talebi henüz PostgreSQL'e ulaşmadan Redis atomik `DECR` sayacı ile kontrol edilir. Stok tükendiğinde veritabanına sıfır yük bindirilerek milisaniyeler içinde kullanıcıya "Bilet Tükendi" cevabı verilir.
2. **Apache Kafka Event Streaming:** Kapasite onayından geçen bilet talepleri Kafka kuyruğuna aktarılır. Böylece veritabanı anlık anormalliklerden (traffic spikes) etkilenmeden kendi işleme hızında biletleri kaydeder.
3. **Transactional Integrity:** Veritabanı işlemleri `@Transactional` yapısıyla tam tutarlılık garantisi sağlar.

---

## 🎟️ Ön Yüz Özellikleri (Bubilet UI)

- **Gelişmiş Kullanıcı Yönetimi & Kimlik Doğrulama:**
  - `Giriş Yap` ve `Kayıt Ol` pop-up modalları.
  - Oturum kontrolü (`localStorage` entegrasyonu).
  - Kullanıcıya özel bilet satın alım geçmişi.
- **Bubilet Etkinlik Detay Modalı (`#eventDetailModal`):**
  - Etkinlik posteri, mekan haritası icon'u, tarih/saat detayları, açıklama metinleri ve açılır/kapanır **Etkinlik Kuralları** akordeonu.
- **İnteraktif Sahne & Mesafe Bazlı Fiyatlandırma Haritası (`#seatingViewModal`):**
  - **Işıklı Sahne Göstergesi:** Visual Glow `🎤 SAHNE / STAGE`.
  - **3 Farklı Bölge ve Dinamik Fiyatlandırma:**
    - 🌟 **Sahne Önü VIP:** En yakın mesafe (1.8x Taban Fiyat).
    - 🟣 **Kategori 1:** Orta saha / ayakta (1.2x Taban Fiyat).
    - 🔵 **Kategori 2:** Tribün / genel giriş (0.8x Taban Fiyat).
  - **4x10 Dinamik Koltuk Matrisi:** Koltuk tıklandığında anlık fiyat güncellenir ve bilet işlemine dahil edilir.
- **Dijital Bilet Koçanı & Barkod:**
  - Profil menüsünden erişilebilen **Önceden Alınmış Biletlerim** sekmesinde PNR kodu, koltuk numarası ve taranabilir görsel barkodlar.

---

## 🛠️ Teknolojik Yanıtlar (Tech Stack)

- **Backend:** Java 17/21/25, Spring Boot 3.3.3, Spring Data JPA, Spring Kafka, Spring Data Redis
- **Önbellek & Mesaj Kuyruğu:** Redis, Apache Kafka, Zookeeper
- **Veritabanı:** PostgreSQL
- **Konteynerizasyon:** Docker, Docker Compose
- **Frontend:** Glassmorphic HTML5, CSS3, JavaScript (ES6+), FontAwesome, Canvas Confetti

---

## 🚀 Kurulum ve Çalıştırma Rehberi

### 1. Ön Gereksinimler
- Git
- Docker & Docker Desktop
- Java 17 veya üzeri
- Maven

### 2. Konteyner Servislerini Başlatma (Docker)
Proje dizininde PostgreSQL, Redis, Kafka ve Zookeeper servislerini tek komutla ayağa kaldırın:

```bash
docker compose up -d
```

Çalışan servisleri doğrulamak için:
```bash
docker compose ps
```

### 3. Spring Boot Uygulamasını Çalıştırma

Terminalden Maven kullanarak:
```bash
mvn spring-boot:run
```
veya favori IDE'nizden (IntelliJ IDEA / VS Code) `TicketingApplication.java` dosyasını çalıştırabilirsiniz.

### 4. Tarayıcıda Test Etme
Uygulama ayağa kalktığında otomatik olarak `DataInitializer` ile örnek etkinlikler (*Duman & Manga Konseri*, *Fazıl Say*, *Cem Yılmaz*, *Armin van Buuren*, *Galatasaray - Fenerbahçe*) yüklenir.

Tarayıcınızda aşağıdaki adrese gidin:
👉 **`http://localhost:8080`**

---

## 🔌 REST API Uç Noktaları (Endpoints)

### Kimlik Doğrulama (Auth)
- `POST /api/auth/register` - Yeni kullanıcı kaydı.
- `POST /api/auth/login` - Kullanıcı girişi.

### Etkinlikler (Events)
- `GET /api/events` - Tüm aktif etkinlikleri ve kalan bilet kapasitelerini listeler.
- `GET /api/events/{id}` - Belirli bir etkinliğin detayını getirir.

### Bilet Satın Alma & Sorgulama (Tickets)
- `POST /api/tickets` - Bilet satın alma talebi (Redis DECR + Kafka işleme).
  ```json
  {
    "eventId": 1,
    "userId": 1,
    "seatNumber": "VIP-A-3"
  }
  ```
- `GET /api/tickets/user/{userId}` - Kullanıcının satın aldığı tüm biletleri döner.

---

## 📜 Lisans
Bu proje eğitim ve yüksek performanslı mimari gösterimi amacıyla geliştirilmiştir. MIT Lisansı altındadır.

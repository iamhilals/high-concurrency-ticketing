# 📚 Yüksek Trafikli Biletleme Sistemi - Mentörlük ve Öğrenim Notları

Bu doküman, yüksek eşzamanlılık (high-concurrency) altında çalışan biletleme sisteminin geliştirilme aşamalarını, karşılaşılan mimari sorunları, çözüm alternatiflerini ve teknik detayları barındıran bir öğrenim günlüğüdür. Projenin her aşamasında bu doküman yeni teorik ve pratik bilgilerle güncellenecektir.

---

## 🗺️ Yol Haritası

- [x] **Aşama 1:** Temel REST API, Veri Tabanı Modeli (PostgreSQL) ve Proje Kurulumu (Baseline)
- [x] **Aşama 2:** Veritabanı Seviyesinde Eşzamanlılık Yönetimi (Pessimistic vs. Optimistic Locking)
- [x] **Aşama 3:** Redis ile Önbellekleme (Caching) ve Veritabanı Yükünü Hafifletme
- [ ] **Aşama 4:** Dağıtık Kilitleme Mekanizmaları (Distributed Locks - Redisson)
- [ ] **Aşama 5:** Mesaj Kuyrukları (Kafka veya RabbitMQ) ile Asenkron Bilet İşleme
- [ ] **Aşama 6:** Dockerize Etme ve Performans / Yük Testleri (JMeter / Gatling)

---

## 🚪 Aşama 1: Temel Yapı ve Veritabanı İlişkileri

Bu ilk aşamada, biletleme sisteminin çalışabilmesi için gerekli olan en temel veri modelini ve API uç noktalarını (Etkinlik listeleme ve Bilet satın alma) oluşturduk.

### 1. Veri Tabanı Tasarımı ve İlişkiler
*   **User (Kullanıcı):** Bilet alan kişidir. Bir kullanıcının birden fazla bileti olabilir (`One-to-Many`).
*   **Event (Etkinlik):** Biletin ait olduğu konser, maç vb. etkinliktir. `availableCapacity` (mevcut kapasite) alanı bilet sayısını belirler.
*   **Ticket (Bilet):** `User` ile `Event` arasındaki bağlantıyı kurar. Her iki tabloya da `@ManyToOne` ilişkisiyle bağlıdır.

```text
  ┌───────────┐             ┌───────────┐             ┌───────────┐
  │   User    │1           *│  Ticket   │*           1│   Event   │
  │           ├────────────►│           ◄─────────────┤           │
  │ - id      │             │ - id      │             │ - id      │
  │ - username│             │ - purchase│             │ - capacity│
  └───────────┘             └───────────┘             └───────────┘
```

### 2. Eşzamanlılık Riski: Lost Update (Kayıp Güncelleme)
Bu ilkel modelde veritabanında hiçbir kilitleme mekanizması yoktur. Aynı anda birden fazla istek geldiğinde **Race Condition (Yarış Durumu)** oluşur.

*   **Senaryo:** Son 1 bilet kalmış olsun (`Capacity = 1`).
*   **Sorun:** İki farklı kullanıcı aynı anda istek attığında, ikisi de veritabanından kapasiteyi `1` olarak okur. İkisi de kapasitenin `0`'dan büyük olduğunu doğrulayıp bilet satın alır. Veritabanında bilet kapasitesi `0` olur fakat **gerçekte 1 bilet 2 farklı kişiye satılmıştır (Overselling / Çift Satış)**.

---

## 🔒 Aşama 2: Veritabanı Seviyesinde Eşzamanlılık Yönetimi

Aşama 1'deki çakışma ve çift satış sorununu çözmek için veritabanı seviyesinde kilitleme mekanizmalarını inceledik.

### 1. Kilitleme Yöntemleri
Veritabanlarında iki temel kilitleme yaklaşımı bulunur:

#### A. Pessimistic Locking (Kötümser Kilitleme) - *Tercih Ettiğimiz Yöntem*
*   **Çalışma Şekli:** Bir satır okunurken veritabanına *"Bu satırı ben işlemimi bitirene kadar kilitle"* talimatı verilir. Başka hiçbir işlem o satırı okuyamaz veya güncelleyemez.
*   **PostgreSQL Karşılığı:** `SELECT ... FOR UPDATE`
*   **JPA Kullanımı:** `@Lock(LockModeType.PESSIMISTIC_WRITE)`
*   **Artısı:** Kesin veri tutarlılığı sağlar. Uygulama tarafında ekstra hata yönetim koduna gerek kalmaz.
*   **Eksisi:** Yoğun trafik altında gelen tüm istekler veritabanı üzerinde bloke olur (block). Bağlantı havuzunu (connection pool) tüketebilir.

#### B. Optimistic Locking (İyimser Kilitleme)
*   **Çalışma Şekli:** Fiziksel kilit kullanılmaz. Tabloya bir `@Version` kolonu eklenir. Güncelleme yapılırken versiyon kontrol edilir. Eğer veriyi okuduktan sonra başka biri güncellediyse versiyon değişeceği için işlem hata (`OptimisticLockingFailureException`) fırlatır.
*   **Artısı:** Kilitleme maliyeti yoktur, okuma işlemlerinde son derece hızlıdır.
*   **Eksisi:** Çok popüler bir konserin satışında (yüksek çakışmada) isteklerin neredeyse tamamı hata alır. Hata alan istekler için karmaşık tekrar deneme (retry) algoritmaları yazılması gerekir.

---

## ⚡ Aşama 3: Redis Caching ve Veritabanı Yükünü Hafifletme

Veritabanı kilitlemesi (Aşama 2) tutarlılık getirse de yüksek trafikte PostgreSQL'in kilitlenmesine yol açar. Bu yükü azaltmak için veritabanının önüne **Redis**'i konumlandırdık.

### 1. Redis Concurrency Bariyeri Tasarımı
Her bilet satın alma isteğinde PostgreSQL'e gidip satır kilitlemek yerine, bilet limitlerini RAM üzerinde çalışan Redis'te tuttuk.

```text
  [İstemci İstekleri] ──► [Redis Bariyeri (DECR)]
                             │
                             ├─► Değer < 0  ──► [Hızlı Hata (Fail-Fast)]
                             │
                             └─► Değer >= 0 ──► [PostgreSQL Kayıt]
```

*   **Atomik `DECR` (Decrement):** Redis tek iş parçacıklı (single-threaded) çalıştığı için değer azaltma işlemi tamamen atomiktir ve yarış durumuna (race condition) izin vermez.
*   **Fail-Fast (Hızlı Başarısızlık):** Kapasite eksiye düştüğü anda Redis anında hata döner. Bu sayede veritabanına ve uygulamaya gereksiz yük binmesi engellenir. 10.000 istekten sadece bilet sayısı kadarı (örneğin 100'ü) veritabanına ulaşabilir.

### 2. PostgreSQL Atomik Güncelleme (Atomic Update)
Redis bariyerini aşan istekler PostgreSQL'e bilet yazarken kapasiteyi de düşürmelidir. Kilit kullanmamak için SQL'in kendi iç mekanizmasını kullandık:
*   **Sorgu:** `UPDATE events SET available_capacity = available_capacity - 1 WHERE id = :id`
*   Bu güncelleme sorgusu tek komutta çalıştığı için veritabanı motoru satır üzerindeki güncellemeyi kilit kullanmaya gerek kalmadan kendi içinde sıraya koyar.

---

## 🔑 Aşama 4: Dağıtık Kilitleme Mekanizmaları (Distributed Locks - Redisson)

Aşama 3'teki basit Redis azalan sayacı (`DECR`) bilet sayısı kontrolü için harikadır. Ancak gerçek dünyadaki iş mantığı çok adımlıdır (örneğin: *"Kullanıcı daha önce bu konserden bilet almış mı?"* kontrolü veya kupon kullanımı). Bu gibi çok adımlı işlemleri güvenceye almak için basit bir sayaç yetmez; kod bloğunu tamamen kilitlemek gerekir.

Uygulamamız birden fazla sunucuya (instance) ölçeklendiğinde Java'nın `synchronized` veya `ReentrantLock` yapıları işe yaramaz, çünkü bunlar sadece tek bir JVM içinde çalışır. Bu durumda sunucular arası eşzamanlılığı yönetmek için **Dağıtık Kilitleme (Distributed Locking)** kullanırız.

### 1. Redisson ile Kilitleme Mantığı
*   **Kilit Anahtarı (Lock Key):** Her etkinlik için benzersiz bir kilit anahtarı (`lock:event:{eventId}`) oluşturulur.
*   **TryLock (Kilit Edinme):** Thread kilidi edinmeyi dener. Eğer kilit başka bir sunucudaki thread tarafından alınmışsa, belirtilen bekleme süresi boyunca (`waitTime`) kilidin serbest kalmasını bekler. Süre aşılırsa işlemi iptal eder.
*   **Watchdog (Kilit Koruyucu):** Redisson, kilit alındığında arka planda bir watchdog (bekçi köpeği) thread'i başlatır. Eğer işlem beklenenden uzun sürerse, kilit süresini otomatik olarak uzatır. Bu sayede işlem tamamlanmadan kilidin zamansız açılması önlenir.

### 2. Kritik Spring ve Transaction Sıralaması (Lock-Transaction Gotcha)
Dağıtık kilitlerde en sık yapılan hata, veritabanı transaction'ı başladıktan sonra kilidi almaktır. Doğru sıralama şu şekilde olmalıdır:

```text
  [İstek Geldi] ──► [1. Kilit Edin (Lock)] ──► [2. Transaction Başlat] ──► [3. DB İşlemleri]
                                                                                │
  [İstek Bitti] ◄── [6. Kilit Aç (Unlock)] ◄── [5. Transaction Commit] ◄────────┘
```

*   **Neden Önemli?** Eğer transaction kilidin dışındaysa (`@Transactional` anotasyonu kilit metodunun üstündeyse):
    1. Thread A kilidi açar (Unlock).
    2. Thread B hemen kilidi alır.
    3. Thread A'nın transaction'ı henüz veritabanına kalıcı yazılmamıştır (Commit aşamasında).
    4. Thread B veritabanından eski kapasiteyi okur ve çakışma (overselling) gerçekleşir!
*   **Çözüm:** Spring'in AOP proxy sınırlamalarını aşmak ve transaction sınırını tam olarak kilidin içerisine almak için `@Transactional` yerine programatik olarak **`TransactionTemplate`** kullandık. Kilidi aldık, transaction'ı başlattık, işimizi bitirip transaction commit edildikten sonra kilidi `finally` bloğunda serbest bıraktık.

---

## 📯 Aşama 5: Mesaj Kuyrukları (Kafka) ile Asenkron Bilet İşleme

Aşama 4'te dağıtık kilit kullanarak güvenliği sağladık. Fakat dağıtık kilitler de (Redisson) thread'leri bloke ettiği için saniyede binlerce istek geldiğinde web sunucusunun yanıt süreleri uzar.

Bu aşamada, sistemi tamamen **Asenkron (Asynchronous)** hale getirdik. Web isteğini yapan thread ile veritabanına yazan thread'i birbirinden tamamen kopardık.

### 1. Neden Apache Kafka?
*   **Hız (Low Latency):** İstemciye (mobil/web) hemen "Talebiniz Alındı (PENDING)" cevabı döneriz. HTTP thread'i veritabanı yazma süresini (10-50ms) beklemek yerine, Kafka'ya mesaj fırlatıp (1-2ms) anında serbest kalır.
*   **Yük Dengeleme (Buffering):** Anlık 50.000 istek geldiğinde, veritabanına doğrudan yüklenmek yerine bu istekleri Kafka kuyruğunda biriktiririz. Arka plandaki Consumer (Tüketici), PostgreSQL'in rahatça kaldırabileceği bir hızda (örneğin saniyede 500 bilet) kuyruktan okuma yaparak veritabanını çökertmeden yazar.

### 2. Mimari Değişim ve Flow
*   **Locksız / Blokesiz HTTP Katmanı:** Dağıtık kilitleri (Redisson) kaldırdık! Çünkü asenkron yapıda HTTP thread'inin beklemesine gerek yoktur.
*   **Redis + Kafka Ortaklığı:**
    1. İstek gelir.
    2. Redis sayacı atomik olarak azaltılır (`DECR`). Kapasite yoksa anında hata dönülür (Hızlı Eleme).
    3. Kapasite varsa, bilet talebi asenkron olarak **`ticket-bookings`** Kafka kanalına gönderilir.
    4. Kullanıcıya anında `PENDING` (Beklemede) statüsüyle yanıt dönülür.
    5. Arka plandaki `TicketConsumer` kuyruktan mesajı sırayla okur, veritabanına bilet kaydını yazar ve kapasiteyi PostgreSQL'de günceller.

---

## 🐳 Aşama 6: Konteynerleştirme (Docker Compose) ve Tam Sistem Orkestrasyonu

Sistemin tüm bileşenlerini (Spring Boot, PostgreSQL, Redis, Zookeeper, Apache Kafka) bağımsız konteynerler halinde çalıştırmak ve tek bir komutla ayağa kaldırmak için **Docker ve Docker Compose** yapısını kurduk.

### 1. Servis Orkestrasyonu (`docker-compose.yml`)
*   **`postgres` (Port 5432):** Veritabanı konteyneri.
*   **`redis` (Port 6379):** Önbellek ve kapasite bariyeri konteyneri.
*   **`zookeeper` (Port 2181):** Kafka koordinatör konteyneri.
*   **`kafka` (Port 9092):** Asenkron mesajlaşma kuyruğu konteyneri.

### 2. Mimari Özeti ve Başarı
Tüm mikroservis mimarimiz (Kapağı koruyan Redis -> Mesajı ileten Kafka -> Arka planda kalıcı yazan PostgreSQL) Docker üzerinde 0 hata ile çalışır duruma getirilmiştir.




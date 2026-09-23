package com.ticketing.config;

import com.ticketing.entity.Event;
import com.ticketing.entity.User;
import com.ticketing.repository.EventRepository;
import com.ticketing.repository.TicketRepository;
import com.ticketing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final StringRedisTemplate redisTemplate;

    public DataInitializer(UserRepository userRepository, EventRepository eventRepository, TicketRepository ticketRepository, StringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Test kullanıcısını oluştur
        if (userRepository.count() == 0) {
            User testUser = User.builder()
                    .username("johndoe")
                    .email("john.doe@example.com")
                    .password("password123")
                    .build();
            userRepository.save(testUser);
            System.out.println("Initialized test user: " + testUser.getUsername() + " (ID: " + testUser.getId() + ")");
        }

        // 2. 40 adet gerçekçi Bubilet etkinliğini veritabanına ekle
        if (eventRepository.count() < 25) {
            ticketRepository.deleteAll();
            eventRepository.deleteAll();

            List<Event> events = new ArrayList<>();

            // ==================== MÜZİK & KONSER (10 Etkinlik) ====================
            events.add(Event.builder()
                    .title("Duman & Manga - Rock Festivali 2026")
                    .description("Türk rock müziğinin dev isimleri Duman ve Manga unutulmaz bir açık hava konseri için sahnede!")
                    .dateTime(LocalDateTime.of(2026, 10, 15, 21, 0))
                    .price(new BigDecimal("450.00"))
                    .availableCapacity(100)
                    .category("Müzik & Konser")
                    .venue("KüçükÇiftlik Park, Harbiye / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Fazıl Say - Klasik Müzik & Piyano Gecesi")
                    .description("Dünyaca ünlü piyanist ve besteci Fazıl Say'ın canlı piyano performansı ve senfoni ziyafeti.")
                    .dateTime(LocalDateTime.of(2026, 11, 12, 20, 30))
                    .price(new BigDecimal("750.00"))
                    .availableCapacity(60)
                    .category("Müzik & Konser")
                    .venue("Zorlu PSM Turkcell Sahnesi, Beşiktaş / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1520523839897-bd0b52f945a0?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Teoman - Koyu Antoloji Konseri")
                    .description("Teoman en sevilen klasiklerini senfonik düzenlemeler eşliğinde açık havada seslendiriyor.")
                    .dateTime(LocalDateTime.of(2026, 10, 8, 21, 0))
                    .price(new BigDecimal("600.00"))
                    .availableCapacity(80)
                    .category("Müzik & Konser")
                    .venue("Harbiye Cemil Topuzlu Açıkhava / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Yüzyüzeyken Konuşuruz - Son Gemi Tour")
                    .description("Bağımsız alternatif rock grubu Yüzyüzeyken Konuşuruz hayranlarıyla buluşuyor.")
                    .dateTime(LocalDateTime.of(2026, 10, 22, 20, 30))
                    .price(new BigDecimal("380.00"))
                    .availableCapacity(90)
                    .category("Müzik & Konser")
                    .venue("OFT Sahne, Kadıköy / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1498038432885-c6f3f1b912ee?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Sıla - Şarkı Söylemek Lazım Konser Serisi")
                    .description("Güçlü sesi ve unutulmaz besteleriyle Sıla, başkentte müzikseverlere müzik ziyafeti sunuyor.")
                    .dateTime(LocalDateTime.of(2026, 11, 5, 21, 0))
                    .price(new BigDecimal("550.00"))
                    .availableCapacity(70)
                    .category("Müzik & Konser")
                    .venue("ATO Congresium, Ankara")
                    .imageUrl("https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Sertab Erener - Senfonik 2026")
                    .description("Eurovision şampiyonumuz Sertab Erener dev senfoni orkestrası eşliğinde sahnede!")
                    .dateTime(LocalDateTime.of(2026, 10, 18, 20, 30))
                    .price(new BigDecimal("480.00"))
                    .availableCapacity(85)
                    .category("Müzik & Konser")
                    .venue("Kültürpark Açıkhava Tiyatrosu, İzmir")
                    .imageUrl("https://images.unsplash.com/photo-1465847899084-d164df4dedc6?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Melike Şahin - Gülbeyaz Gazinosu")
                    .description("Neo-arabesk ve pop müziğin kraliçesi Melike Şahin muazzam gazino konsepti ile Harbiye'de.")
                    .dateTime(LocalDateTime.of(2026, 11, 15, 21, 0))
                    .price(new BigDecimal("520.00"))
                    .availableCapacity(75)
                    .category("Müzik & Konser")
                    .venue("Harbiye Cemil Topuzlu Açıkhava / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1501386761578-eac5c94b800a?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Zeynep Bastık - Akustik Gece")
                    .description("Sevilen hit parçaları ve canlı akustik performanslarıyla Zeynep Bastık sahnede.")
                    .dateTime(LocalDateTime.of(2026, 10, 30, 21, 30))
                    .price(new BigDecimal("420.00"))
                    .availableCapacity(95)
                    .category("Müzik & Konser")
                    .venue("Olimpos Açıkhava Tiyatrosu, Antalya")
                    .imageUrl("https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Adamlar - Canlı Rock Şovu")
                    .description("Enerjik sahne şovları ve eğlenceli şarkı sözleriyle Adamlar grubu konseri!")
                    .dateTime(LocalDateTime.of(2026, 11, 2, 20, 0))
                    .price(new BigDecimal("350.00"))
                    .availableCapacity(100)
                    .category("Müzik & Konser")
                    .venue("IF Performance Hall, Ankara")
                    .imageUrl("https://images.unsplash.com/photo-1524368535928-5b5e00ddc76b?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Mor ve Ötesi - Büyük Stadyum Konseri")
                    .description("Mor ve Ötesi stadyumu sallayacak dev prodüksiyonlu tarihi konseriyle unutulmaz bir gece yaşatacak.")
                    .dateTime(LocalDateTime.of(2026, 11, 28, 21, 0))
                    .price(new BigDecimal("700.00"))
                    .availableCapacity(50)
                    .category("Müzik & Konser")
                    .venue("Tüpraş Stadyumu, Beşiktaş / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1429962714451-bb934ecdc436?auto=format&fit=crop&w=800&q=80")
                    .build());


            // ==================== FESTİVAL & DJ (10 Etkinlik) ====================
            events.add(Event.builder()
                    .title("Armin van Buuren - Electronic Neon Night")
                    .description("Dünyanın 1 numaralı DJ'lerinden Armin van Buuren ile lazer ve görsel şovlarla dolu muazzam bir gece.")
                    .dateTime(LocalDateTime.of(2026, 11, 20, 22, 30))
                    .price(new BigDecimal("650.00"))
                    .availableCapacity(50)
                    .category("Festival & DJ")
                    .venue("Volkswagen Arena, Maslak / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Solomun - Open Air Techno Fest")
                    .description("Diynamic müzik şirketinin kurucusu efsane DJ Solomun ile doğa içinde 12 saatlik maraton set!")
                    .dateTime(LocalDateTime.of(2026, 10, 10, 18, 0))
                    .price(new BigDecimal("720.00"))
                    .availableCapacity(60)
                    .category("Festival & DJ")
                    .venue("Life Park, Sarıyer / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1506157786151-b8491531f063?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Chill-Out Festival Istanbul 2026")
                    .description("Deniz kıyısında elektronik müzik, tasarım pazarları ve eşsiz lezzetlerle dolu 2 günlük festival.")
                    .dateTime(LocalDateTime.of(2026, 10, 17, 14, 0))
                    .price(new BigDecimal("580.00"))
                    .availableCapacity(120)
                    .category("Festival & DJ")
                    .venue("Kilyos Milyon Beach / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6c3?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Boris Brejcha - High-Tech Minimal Night")
                    .description("Joker maskesi ve özgün High-Tech Minimal tarzıyla Boris Brejcha sahnede!")
                    .dateTime(LocalDateTime.of(2026, 11, 14, 23, 0))
                    .price(new BigDecimal("690.00"))
                    .availableCapacity(45)
                    .category("Festival & DJ")
                    .venue("Karaköy Warehouse, Beyoğlu / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1574391884720-bbc3740c59d1?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Tomorrowland Winter Teaser Party")
                    .description("Tomorrowland atmosferini kar manzarası ve elektronik ritimlerle deneyimleyin.")
                    .dateTime(LocalDateTime.of(2026, 12, 5, 21, 0))
                    .price(new BigDecimal("800.00"))
                    .availableCapacity(40)
                    .category("Festival & DJ")
                    .venue("Uludağ Snow Park, Bursa")
                    .imageUrl("https://images.unsplash.com/photo-1492684223066-81342ee5ff30?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Keinemusik - Rampa Istanbul Night")
                    .description("&ME, Rampa ve Adam Port efsane Keinemusik setiyle boğaz kıyısını şenlendiriyor.")
                    .dateTime(LocalDateTime.of(2026, 10, 25, 22, 0))
                    .price(new BigDecimal("850.00"))
                    .availableCapacity(35)
                    .category("Festival & DJ")
                    .venue("Ortaköy Port, Beşiktaş / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Tale of Us - Afterlife Realm")
                    .description("Görsel dijital sanatlar ve Melodic Techno müziğinin birleştiği sinematik Afterlife deneyimi.")
                    .dateTime(LocalDateTime.of(2026, 11, 21, 22, 30))
                    .price(new BigDecimal("900.00"))
                    .availableCapacity(30)
                    .category("Festival & DJ")
                    .venue("KüçükÇiftlik Park, Harbiye / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Charlotte de Witte - Acid Techno Invasion")
                    .description("Yüksek tempolu asit tekno kraliçesi Charlotte de Witte İzmir'de sahnede!")
                    .dateTime(LocalDateTime.of(2026, 11, 7, 23, 0))
                    .price(new BigDecimal("620.00"))
                    .availableCapacity(65)
                    .category("Festival & DJ")
                    .venue("Kültürpark Hall 1, İzmir")
                    .imageUrl("https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Anjunadeep Open Air 2026")
                    .description("Deep house ve melodik elektronik müziğin dünyaca tanınan plak şirketi Anjunadeep sahili ısıtıyor.")
                    .dateTime(LocalDateTime.of(2026, 10, 11, 16, 0))
                    .price(new BigDecimal("500.00"))
                    .availableCapacity(80)
                    .category("Festival & DJ")
                    .venue("Çeşme Beach Club, İzmir")
                    .imageUrl("https://images.unsplash.com/photo-1506157786151-b8491531f063?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Black Coffee Live - Afro House Special")
                    .description("Grammy ödüllü efsanevi prodüktör ve DJ Black Coffee rüzgarı Antalya'da esiyor.")
                    .dateTime(LocalDateTime.of(2026, 10, 28, 22, 0))
                    .price(new BigDecimal("750.00"))
                    .availableCapacity(50)
                    .category("Festival & DJ")
                    .venue("Lara Beach Arena, Antalya")
                    .imageUrl("https://images.unsplash.com/photo-1574391884720-bbc3740c59d1?auto=format&fit=crop&w=800&q=80")
                    .build());


            // ==================== SPOR (10 Etkinlik) ====================
            events.add(Event.builder()
                    .title("Galatasaray - Fenerbahçe Süper Derbi")
                    .description("Trendyol Süper Lig şampiyonluk yolundaki dev kitle derbisi!")
                    .dateTime(LocalDateTime.of(2026, 9, 28, 19, 0))
                    .price(new BigDecimal("1250.00"))
                    .availableCapacity(30)
                    .category("Spor")
                    .venue("RAMS Park Stadyumu, Seyrantepe / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Beşiktaş - Trabzonspor Lig Mücadelesi")
                    .description("Süper Lig'in iki köklü çınarının heyecan dolu 90 dakikalık karşılaşması.")
                    .dateTime(LocalDateTime.of(2026, 10, 18, 19, 0))
                    .price(new BigDecimal("950.00"))
                    .availableCapacity(40)
                    .category("Spor")
                    .venue("Tüpraş Stadyumu, Beşiktaş / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1522778119026-d647f0596c20?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Anadolu Efes - Real Madrid EuroLeague")
                    .description("Turkish Airlines EuroLeague devinde Anadolu Efes, Real Madrid'i ağırlıyor!")
                    .dateTime(LocalDateTime.of(2026, 10, 29, 20, 30))
                    .price(new BigDecimal("450.00"))
                    .availableCapacity(85)
                    .category("Spor")
                    .venue("Sinan Erdem Spor Salonu, Bakırköy / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1546519638-68e109498ffc?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Turkish Grand Prix Formula 1 2026")
                    .description("Dünyanın en hızlı pilotları efsane 8. virajı dönmek için Intercity İstanbul Park'ta!")
                    .dateTime(LocalDateTime.of(2026, 11, 15, 15, 0))
                    .price(new BigDecimal("2200.00"))
                    .availableCapacity(25)
                    .category("Spor")
                    .venue("Intercity İstanbul Park, Tuzla / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Filenin Sultanları - Dünya Voleybol Şampiyonası")
                    .description("A Milli Kadın Voleybol Takımımızın kritik grup maçı heyecanı sahnede!")
                    .dateTime(LocalDateTime.of(2026, 10, 12, 18, 0))
                    .price(new BigDecimal("350.00"))
                    .availableCapacity(90)
                    .category("Spor")
                    .venue("TVF Burhan Felek Salonu, Üsküdar / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1612872087720-bb876e2e67d1?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Türkiye - Hırvatistan A Milli Maç")
                    .description("2028 Avrupa Şampiyonası Elemeleri grup aşaması heyecanı!")
                    .dateTime(LocalDateTime.of(2026, 11, 10, 21, 45))
                    .price(new BigDecimal("400.00"))
                    .availableCapacity(75)
                    .category("Spor")
                    .venue("Eskişehir Yeni Stadyumu, Eskişehir")
                    .imageUrl("https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Red Bull Car Park Drift World Finals")
                    .description("Dünyanın en yetenekli drift pilotlarının duman altı performansları ve nefes kesen mücadelesi.")
                    .dateTime(LocalDateTime.of(2026, 10, 24, 14, 0))
                    .price(new BigDecimal("300.00"))
                    .availableCapacity(100)
                    .category("Spor")
                    .venue("İzmir Ülkü Yarış Pisti, İzmir")
                    .imageUrl("https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Istanbul Marathon 2026 Halk Koşusu")
                    .description("Kıtalararası tek maratonda Asya'dan Avrupa'ya koşma heyecanına ortak olun!")
                    .dateTime(LocalDateTime.of(2026, 11, 1, 9, 0))
                    .price(new BigDecimal("150.00"))
                    .availableCapacity(200)
                    .category("Spor")
                    .venue("15 Temmuz Şehitler Köprüsü / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1452626038306-9aae5e071dd3?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("ATP Tennis Istanbul Open 2026")
                    .description("Dünya sıralamasındaki raketlerin toprak korttaki şampiyonluk mücadelesi.")
                    .dateTime(LocalDateTime.of(2026, 10, 19, 11, 0))
                    .price(new BigDecimal("500.00"))
                    .availableCapacity(60)
                    .category("Spor")
                    .venue("TED Spor Kulübü, Tarabya / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1622279457486-62dcc4a431d6?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("World Kickboxing Grand Prix 2026")
                    .description("Ağır sıklet Dünya şampiyonluk unvan maçı ve profesyonel dövüş gecesi.")
                    .dateTime(LocalDateTime.of(2026, 12, 12, 20, 0))
                    .price(new BigDecimal("380.00"))
                    .availableCapacity(70)
                    .category("Spor")
                    .venue("Ankara Arena Spor Salonu, Ankara")
                    .imageUrl("https://images.unsplash.com/photo-1517649763962-0c623266010b?auto=format&fit=crop&w=800&q=80")
                    .build());


            // ==================== KOMEDİ & TİYATRO (10 Etkinlik) ====================
            events.add(Event.builder()
                    .title("Cem Yılmaz - CMXXIV Stand-Up")
                    .description("Cem Yılmaz'ın kapalı gişe oynayan yepyeni stand-up gösterisi CMXXIV canlı sahnede!")
                    .dateTime(LocalDateTime.of(2026, 10, 24, 21, 0))
                    .price(new BigDecimal("850.00"))
                    .availableCapacity(40)
                    .category("Komedi & Tiyatro")
                    .venue("Bostancı Gösteri Merkezi, Kadıköy / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1585699324551-f6c309eedeca?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Güldür Güldür Show Canlı Çekim")
                    .description("Ekranların sevilen mizah ekibi Güldür Güldür Show kahkaha dolu canlı çekimiyle BKM'de.")
                    .dateTime(LocalDateTime.of(2026, 10, 16, 20, 0))
                    .price(new BigDecimal("400.00"))
                    .availableCapacity(80)
                    .category("Komedi & Tiyatro")
                    .venue("BKM Tiyatro, Beşiktaş / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Tolgshow - Tolga Çevik İmprow")
                    .description("Arkadaşım karakteriyle Tolga Çevik ve Minik'in doğaçlama komedi şovu!")
                    .dateTime(LocalDateTime.of(2026, 11, 3, 20, 30))
                    .price(new BigDecimal("600.00"))
                    .availableCapacity(65)
                    .category("Komedi & Tiyatro")
                    .venue("Zorlu PSM Drama Sahnesi, Beşiktaş / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1507676184212-d03ab07a01bf?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Bir Baba Hamlet - Şevket Çoruh")
                    .description("Şevket Çoruh ve Günay Karacaoğlu'ndan Shakespeare'i altüst eden ödüllü komedi tiyatrosu.")
                    .dateTime(LocalDateTime.of(2026, 10, 21, 20, 30))
                    .price(new BigDecimal("320.00"))
                    .availableCapacity(90)
                    .category("Komedi & Tiyatro")
                    .venue("Kadıköy Halk Eğitim Merkezi, İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1460723237483-7a6dc9d0b212?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Amadeus - Selçuk Yöntem & Tansu Biçer")
                    .description("Mozart ile Salieri arasındaki efsanevi rekabeti konu alan kapalı gişe tiyatro şaheseri.")
                    .dateTime(LocalDateTime.of(2026, 11, 19, 20, 30))
                    .price(new BigDecimal("680.00"))
                    .availableCapacity(55)
                    .category("Komedi & Tiyatro")
                    .venue("AKM Türk Telekom Opera Salonu, Taksim / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1503095396549-807759245b35?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Zengin Mutfak - Şener Şen Efsanesi")
                    .description("Türk tiyatrosunun usta ismi Şener Şen, Lütfü Usta rolüyle DasDas sahnelerinde büyülüyor.")
                    .dateTime(LocalDateTime.of(2026, 11, 25, 20, 30))
                    .price(new BigDecimal("750.00"))
                    .availableCapacity(40)
                    .category("Komedi & Tiyatro")
                    .venue("DasDas Ataşehir, İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Yasemin Sakallıoğlu - Doğru Koca Nasıl Seçilir?")
                    .description("Sosyal medyanın sevilen ismi Yasemin Sakallıoğlu'ndan kahkaha dolu tek kişilik stand-up şov.")
                    .dateTime(LocalDateTime.of(2026, 10, 14, 20, 30))
                    .price(new BigDecimal("450.00"))
                    .availableCapacity(70)
                    .category("Komedi & Tiyatro")
                    .venue("MEB Şura Salonu, Ankara")
                    .imageUrl("https://images.unsplash.com/photo-1585699324551-f6c309eedeca?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Kutsal Motor Canlı Pod-Show")
                    .description("Sinema, mizah ve popüler kültür gıybetlerinin konuşulduğu canlı sahne yayını!")
                    .dateTime(LocalDateTime.of(2026, 10, 27, 20, 30))
                    .price(new BigDecimal("280.00"))
                    .availableCapacity(100)
                    .category("Komedi & Tiyatro")
                    .venue("Profilo Kültür Merkezi, Mecidiyeköy / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Doğu Demirkol Stand-Up 2026")
                    .description("Abudabi'den Cannes'a kadar uzanan özgün mizahıyla Doğu Demirkol sahnede!")
                    .dateTime(LocalDateTime.of(2026, 11, 8, 20, 30))
                    .price(new BigDecimal("390.00"))
                    .availableCapacity(80)
                    .category("Komedi & Tiyatro")
                    .venue("Bursa Merinos AKKM, Bursa")
                    .imageUrl("https://images.unsplash.com/photo-1507676184212-d03ab07a01bf?auto=format&fit=crop&w=800&q=80")
                    .build());

            events.add(Event.builder()
                    .title("Mesut Süre - Rabarba Canlı Yayın & Gösteri")
                    .description("Radyoların en neşeli programı Rabarba, Mesut Süre sunumu ve konuklarıyla sahnede!")
                    .dateTime(LocalDateTime.of(2026, 11, 16, 20, 30))
                    .price(new BigDecimal("350.00"))
                    .availableCapacity(85)
                    .category("Komedi & Tiyatro")
                    .venue("İzmir Nazım Hikmet Kültür Merkezi, İzmir")
                    .imageUrl("https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=800&q=80")
                    .build());

            eventRepository.saveAll(events);
            System.out.println("Successfully seeded 40 authentic Bubilet-style events across 4 categories!");
        }

        // 3. Veritabanındaki tüm 40 etkinliğin kapasitelerini Redis'e senkronize et
        for (Event event : eventRepository.findAll()) {
            String redisKey = "event:" + event.getId() + ":capacity";
            redisTemplate.opsForValue().set(redisKey, String.valueOf(event.getAvailableCapacity()));
            System.out.println("Synced event capacity to Redis: " + redisKey + " = " + event.getAvailableCapacity());
        }
    }
}

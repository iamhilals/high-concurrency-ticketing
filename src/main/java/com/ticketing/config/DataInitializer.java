package com.ticketing.config;

import com.ticketing.entity.Event;
import com.ticketing.entity.User;
import com.ticketing.repository.EventRepository;
import com.ticketing.repository.TicketRepository;
import com.ticketing.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
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

    private List<Event> buildCatalogEvents() {
        LocalDate today = LocalDate.now();
        List<Event> events = new ArrayList<>();

        // ==================== MÜZİK & KONSER (10 Etkinlik — Her biri özgün görsel & güncel 1 yıllık takvim) ====================
        events.add(Event.builder()
                .title("Duman & Manga - Rock Festivali")
                .description("Türk rock müziğinin dev isimleri Duman ve Manga unutulmaz bir açık hava konseri için sahnede!")
                .dateTime(today.plusDays(3).atTime(21, 0))
                .price(new BigDecimal("450.00"))
                .availableCapacity(100)
                .category("Müzik & Konser")
                .venue("KüçükÇiftlik Park, Harbiye / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1498038432885-c6f3f1b912ee?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Fazıl Say - Klasik Müzik & Piyano Gecesi")
                .description("Dünyaca ünlü piyanist ve besteci Fazıl Say'ın canlı piyano performansı ve senfoni ziyafeti.")
                .dateTime(today.plusDays(9).atTime(20, 30))
                .price(new BigDecimal("750.00"))
                .availableCapacity(60)
                .category("Müzik & Konser")
                .venue("Zorlu PSM Turkcell Sahnesi, Beşiktaş / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1520523839897-bd0b52f945a0?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Teoman - Koyu Antoloji Konseri")
                .description("Teoman en sevilen klasiklerini senfonik düzenlemeler eşliğinde açık havada seslendiriyor.")
                .dateTime(today.plusDays(16).atTime(21, 0))
                .price(new BigDecimal("600.00"))
                .availableCapacity(80)
                .category("Müzik & Konser")
                .venue("Harbiye Cemil Topuzlu Açıkhava / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Yüzyüzeyken Konuşuruz - Son Gemi Tour")
                .description("Bağımsız alternatif rock grubu Yüzyüzeyken Konuşuruz hayranlarıyla buluşuyor.")
                .dateTime(today.plusDays(24).atTime(20, 30))
                .price(new BigDecimal("380.00"))
                .availableCapacity(90)
                .category("Müzik & Konser")
                .venue("OFT Sahne, Kadıköy / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1510915361894-db8b60106cb1?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Sıla - Şarkı Söylemek Lazım Konser Serisi")
                .description("Güçlü sesi ve unutulmaz besteleriyle Sıla, başkentte müzikseverlere müzik ziyafeti sunuyor.")
                .dateTime(today.plusDays(38).atTime(21, 0))
                .price(new BigDecimal("550.00"))
                .availableCapacity(70)
                .category("Müzik & Konser")
                .venue("ATO Congresium, Ankara")
                .imageUrl("https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Sertab Erener - Senfonik Gece")
                .description("Eurovision şampiyonumuz Sertab Erener dev senfoni orkestrası eşliğinde sahnede!")
                .dateTime(today.plusDays(52).atTime(20, 30))
                .price(new BigDecimal("480.00"))
                .availableCapacity(85)
                .category("Müzik & Konser")
                .venue("Kültürpark Açıkhava Tiyatrosu, İzmir")
                .imageUrl("https://images.unsplash.com/photo-1465847899084-d164df4dedc6?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Melike Şahin - Gülbeyaz Gazinosu")
                .description("Neo-arabesk ve pop müziğin kraliçesi Melike Şahin muazzam gazino konsepti ile Harbiye'de.")
                .dateTime(today.plusDays(71).atTime(21, 0))
                .price(new BigDecimal("520.00"))
                .availableCapacity(75)
                .category("Müzik & Konser")
                .venue("Harbiye Cemil Topuzlu Açıkhava / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1501386761578-eac5c94b800a?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Zeynep Bastık - Akustik Gece")
                .description("Sevilen hit parçaları ve canlı akustik performanslarıyla Zeynep Bastık sahnede.")
                .dateTime(today.plusDays(95).atTime(21, 30))
                .price(new BigDecimal("420.00"))
                .availableCapacity(95)
                .category("Müzik & Konser")
                .venue("Olimpos Açıkhava Tiyatrosu, Antalya")
                .imageUrl("https://images.unsplash.com/photo-1511379938547-c1f69419868d?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Adamlar - Canlı Rock Şovu")
                .description("Enerjik sahne şovları ve eğlenceli şarkı sözleriyle Adamlar grubu konseri!")
                .dateTime(today.plusDays(124).atTime(20, 0))
                .price(new BigDecimal("350.00"))
                .availableCapacity(100)
                .category("Müzik & Konser")
                .venue("IF Performance Hall, Ankara")
                .imageUrl("https://images.unsplash.com/photo-1524368535928-5b5e00ddc76b?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Mor ve Ötesi - Büyük Stadyum Konseri")
                .description("Mor ve Ötesi stadyumu sallayacak dev prodüksiyonlu tarihi konseriyle unutulmaz bir gece yaşatacak.")
                .dateTime(today.plusDays(162).atTime(21, 0))
                .price(new BigDecimal("700.00"))
                .availableCapacity(50)
                .category("Müzik & Konser")
                .venue("Tüpraş Stadyumu, Beşiktaş / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1429962714451-bb934ecdc436?auto=format&fit=crop&w=900&q=80")
                .build());

        // ==================== FESTİVAL & DJ (10 Etkinlik) ====================
        events.add(Event.builder()
                .title("Armin van Buuren - Electronic Night")
                .description("Dünyanın 1 numaralı DJ'lerinden Armin van Buuren ile lazer ve görsel şovlarla dolu muazzam bir gece.")
                .dateTime(today.plusDays(6).atTime(22, 30))
                .price(new BigDecimal("650.00"))
                .availableCapacity(50)
                .category("Festival & DJ")
                .venue("Volkswagen Arena, Maslak / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Solomun - Open Air Techno Fest")
                .description("Diynamic müzik şirketinin kurucusu efsane DJ Solomun ile doğa içinde 12 saatlik maraton set!")
                .dateTime(today.plusDays(28).atTime(18, 0))
                .price(new BigDecimal("720.00"))
                .availableCapacity(60)
                .category("Festival & DJ")
                .venue("Life Park, Sarıyer / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1506157786151-b8491531f063?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Chill-Out Festival Istanbul")
                .description("Deniz kıyısında elektronik müzik, tasarım pazarları ve eşsiz lezzetlerle dolu 2 günlük festival.")
                .dateTime(today.plusDays(64).atTime(14, 0))
                .price(new BigDecimal("580.00"))
                .availableCapacity(120)
                .category("Festival & DJ")
                .venue("Kilyos Milyon Beach / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6c3?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Boris Brejcha - High-Tech Minimal Night")
                .description("Joker maskesi ve özgün High-Tech Minimal tarzıyla Boris Brejcha sahnede!")
                .dateTime(today.plusDays(108).atTime(23, 0))
                .price(new BigDecimal("690.00"))
                .availableCapacity(45)
                .category("Festival & DJ")
                .venue("Karaköy Warehouse, Beyoğlu / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1574391884720-bbc3740c59d1?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Tomorrowland Winter Teaser Party")
                .description("Tomorrowland atmosferini kar manzarası ve elektronik ritimlerle deneyimleyin.")
                .dateTime(today.plusDays(44).atTime(21, 0))
                .price(new BigDecimal("800.00"))
                .availableCapacity(40)
                .category("Festival & DJ")
                .venue("Uludağ Snow Park, Bursa")
                .imageUrl("https://images.unsplash.com/photo-1492684223066-81342ee5ff30?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Keinemusik - Rampa Istanbul Night")
                .description("&ME, Rampa ve Adam Port efsane Keinemusik setiyle boğaz kıyısını şenlendiriyor.")
                .dateTime(today.plusDays(142).atTime(22, 0))
                .price(new BigDecimal("850.00"))
                .availableCapacity(35)
                .category("Festival & DJ")
                .venue("Ortaköy Port, Beşiktaş / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Tale of Us - Afterlife Realm")
                .description("Görsel dijital sanatlar ve Melodic Techno müziğinin birleştiği sinematik Afterlife deneyimi.")
                .dateTime(today.plusDays(188).atTime(22, 30))
                .price(new BigDecimal("900.00"))
                .availableCapacity(30)
                .category("Festival & DJ")
                .venue("KüçükÇiftlik Park, Harbiye / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1540039155733-5bb30b53aa14?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Charlotte de Witte - Acid Techno Invasion")
                .description("Yüksek tempolu asit tekno kraliçesi Charlotte de Witte İzmir'de sahnede!")
                .dateTime(today.plusDays(224).atTime(23, 0))
                .price(new BigDecimal("620.00"))
                .availableCapacity(65)
                .category("Festival & DJ")
                .venue("Kültürpark Hall 1, İzmir")
                .imageUrl("https://images.unsplash.com/photo-1571266028243-3716f02d2d2e?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Anjunadeep Open Air")
                .description("Deep house ve melodik elektronik müziğin dünyaca tanınan plak şirketi Anjunadeep sahili ısıtıyor.")
                .dateTime(today.plusDays(260).atTime(16, 0))
                .price(new BigDecimal("500.00"))
                .availableCapacity(80)
                .category("Festival & DJ")
                .venue("Çeşme Beach Club, İzmir")
                .imageUrl("https://images.unsplash.com/photo-1459749411175-04bf5292ceea?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Black Coffee Live - Afro House Special")
                .description("Grammy ödüllü efsanevi prodüktör ve DJ Black Coffee rüzgarı Antalya'da esiyor.")
                .dateTime(today.plusDays(302).atTime(22, 0))
                .price(new BigDecimal("750.00"))
                .availableCapacity(50)
                .category("Festival & DJ")
                .venue("Lara Beach Arena, Antalya")
                .imageUrl("https://images.unsplash.com/photo-1598387993441-a364f854c3e1?auto=format&fit=crop&w=900&q=80")
                .build());

        // ==================== SPOR (10 Etkinlik) ====================
        events.add(Event.builder()
                .title("Galatasaray - Fenerbahçe Süper Derbi")
                .description("Trendyol Süper Lig şampiyonluk yolundaki dev kitle derbisi!")
                .dateTime(today.plusDays(5).atTime(19, 0))
                .price(new BigDecimal("1250.00"))
                .availableCapacity(30)
                .category("Spor")
                .venue("RAMS Park Stadyumu, Seyrantepe / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Beşiktaş - Trabzonspor Lig Mücadelesi")
                .description("Süper Lig'in iki köklü çınarının heyecan dolu 90 dakikalık karşılaşması.")
                .dateTime(today.plusDays(19).atTime(19, 0))
                .price(new BigDecimal("950.00"))
                .availableCapacity(40)
                .category("Spor")
                .venue("Tüpraş Stadyumu, Beşiktaş / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1522778119026-d647f0596c20?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Anadolu Efes - Real Madrid EuroLeague")
                .description("Turkish Airlines EuroLeague devinde Anadolu Efes, Real Madrid'i ağırlıyor!")
                .dateTime(today.plusDays(34).atTime(20, 30))
                .price(new BigDecimal("450.00"))
                .availableCapacity(85)
                .category("Spor")
                .venue("Sinan Erdem Spor Salonu, Bakırköy / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1546519638-68e109498ffc?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Turkish Grand Prix Formula 1")
                .description("Dünyanın en hızlı pilotları efsane 8. virajı dönmek için Intercity İstanbul Park'ta!")
                .dateTime(today.plusDays(82).atTime(15, 0))
                .price(new BigDecimal("2200.00"))
                .availableCapacity(25)
                .category("Spor")
                .venue("Intercity İstanbul Park, Tuzla / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Filenin Sultanları - Dünya Voleybol Şampiyonası")
                .description("A Milli Kadın Voleybol Takımımızın kritik grup maçı heyecanı sahnede!")
                .dateTime(today.plusDays(116).atTime(18, 0))
                .price(new BigDecimal("350.00"))
                .availableCapacity(90)
                .category("Spor")
                .venue("TVF Burhan Felek Salonu, Üsküdar / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1612872087720-bb876e2e67d1?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Türkiye - Hırvatistan A Milli Maç")
                .description("Avrupa Şampiyonası Elemeleri grup aşaması heyecanı!")
                .dateTime(today.plusDays(154).atTime(21, 45))
                .price(new BigDecimal("400.00"))
                .availableCapacity(75)
                .category("Spor")
                .venue("Eskişehir Yeni Stadyumu, Eskişehir")
                .imageUrl("https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Red Bull Car Park Drift World Finals")
                .description("Dünyanın en yetenekli drift pilotlarının duman altı performansları ve nefes kesen mücadelesi.")
                .dateTime(today.plusDays(204).atTime(14, 0))
                .price(new BigDecimal("300.00"))
                .availableCapacity(100)
                .category("Spor")
                .venue("İzmir Ülkü Yarış Pisti, İzmir")
                .imageUrl("https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Istanbul Marathon Halk Koşusu")
                .description("Kıtalararası tek maratonda Asya'dan Avrupa'ya koşma heyecanına ortak olun!")
                .dateTime(today.plusDays(248).atTime(9, 0))
                .price(new BigDecimal("150.00"))
                .availableCapacity(200)
                .category("Spor")
                .venue("15 Temmuz Şehitler Köprüsü / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1452626038306-9aae5e071dd3?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("ATP Tennis Istanbul Open")
                .description("Dünya sıralamasındaki raketlerin toprak korttaki şampiyonluk mücadelesi.")
                .dateTime(today.plusDays(285).atTime(11, 0))
                .price(new BigDecimal("500.00"))
                .availableCapacity(60)
                .category("Spor")
                .venue("TED Spor Kulübü, Tarabya / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1622279457486-62dcc4a431d6?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("World Kickboxing Grand Prix")
                .description("Ağır sıklet Dünya şampiyonluk unvan maçı ve profesyonel dövüş gecesi.")
                .dateTime(today.plusDays(328).atTime(20, 0))
                .price(new BigDecimal("380.00"))
                .availableCapacity(70)
                .category("Spor")
                .venue("Ankara Arena Spor Salonu, Ankara")
                .imageUrl("https://images.unsplash.com/photo-1517649763962-0c623266010b?auto=format&fit=crop&w=900&q=80")
                .build());

        // ==================== KOMEDİ & TİYATRO (10 Etkinlik) ====================
        events.add(Event.builder()
                .title("Cem Yılmaz - CMXXIV Stand-Up")
                .description("Cem Yılmaz'ın kapalı gişe oynayan yepyeni stand-up gösterisi CMXXIV canlı sahnede!")
                .dateTime(today.plusDays(7).atTime(21, 0))
                .price(new BigDecimal("850.00"))
                .availableCapacity(40)
                .category("Komedi & Tiyatro")
                .venue("Bostancı Gösteri Merkezi, Kadıköy / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1585699324551-f6c309eedeca?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Güldür Güldür Show Canlı Çekim")
                .description("Ekranların sevilen mizah ekibi Güldür Güldür Show kahkaha dolu canlı çekimiyle BKM'de.")
                .dateTime(today.plusDays(22).atTime(20, 0))
                .price(new BigDecimal("400.00"))
                .availableCapacity(80)
                .category("Komedi & Tiyatro")
                .venue("BKM Tiyatro, Beşiktaş / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1517457373958-b7bdd4587205?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Tolgshow - Tolga Çevik İmprow")
                .description("Arkadaşım karakteriyle Tolga Çevik ve Minik'in doğaçlama komedi şovu!")
                .dateTime(today.plusDays(47).atTime(20, 30))
                .price(new BigDecimal("600.00"))
                .availableCapacity(65)
                .category("Komedi & Tiyatro")
                .venue("Zorlu PSM Drama Sahnesi, Beşiktaş / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1507676184212-d03ab07a01bf?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Bir Baba Hamlet - Şevket Çoruh")
                .description("Şevket Çoruh ve Günay Karacaoğlu'ndan Shakespeare'i altüst eden ödüllü komedi tiyatrosu.")
                .dateTime(today.plusDays(76).atTime(20, 30))
                .price(new BigDecimal("320.00"))
                .availableCapacity(90)
                .category("Komedi & Tiyatro")
                .venue("Kadıköy Halk Eğitim Merkezi, İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1460723237483-7a6dc9d0b212?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Amadeus - Selçuk Yöntem & Tansu Biçer")
                .description("Mozart ile Salieri arasındaki efsanevi rekabeti konu alan kapalı gişe tiyatro şaheseri.")
                .dateTime(today.plusDays(132).atTime(20, 30))
                .price(new BigDecimal("680.00"))
                .availableCapacity(55)
                .category("Komedi & Tiyatro")
                .venue("AKM Türk Telekom Opera Salonu, Taksim / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1503095396549-807759245b35?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Zengin Mutfak - Şener Şen Efsanesi")
                .description("Türk tiyatrosunun usta ismi Şener Şen, Lütfü Usta rolüyle DasDas sahnelerinde büyülüyor.")
                .dateTime(today.plusDays(175).atTime(20, 30))
                .price(new BigDecimal("750.00"))
                .availableCapacity(40)
                .category("Komedi & Tiyatro")
                .venue("DasDas Ataşehir, İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1518834107812-67b0b7c58434?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Yasemin Sakallıoğlu - Doğru Koca Nasıl Seçilir?")
                .description("Sosyal medyanın sevilen ismi Yasemin Sakallıoğlu'ndan kahkaha dolu tek kişilik stand-up şov.")
                .dateTime(today.plusDays(214).atTime(20, 30))
                .price(new BigDecimal("450.00"))
                .availableCapacity(70)
                .category("Komedi & Tiyatro")
                .venue("MEB Şura Salonu, Ankara")
                .imageUrl("https://images.unsplash.com/photo-1527224857830-43a7acc85260?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Kutsal Motor Canlı Pod-Show")
                .description("Sinema, mizah ve popüler kültür gıybetlerinin konuşulduğu canlı sahne yayını!")
                .dateTime(today.plusDays(254).atTime(20, 30))
                .price(new BigDecimal("280.00"))
                .availableCapacity(100)
                .category("Komedi & Tiyatro")
                .venue("Profilo Kültür Merkezi, Mecidiyeköy / İstanbul")
                .imageUrl("https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Doğu Demirkol Stand-Up")
                .description("Abudabi'den Cannes'a kadar uzanan özgün mizahıyla Doğu Demirkol sahnede!")
                .dateTime(today.plusDays(296).atTime(20, 30))
                .price(new BigDecimal("390.00"))
                .availableCapacity(80)
                .category("Komedi & Tiyatro")
                .venue("Bursa Merinos AKKM, Bursa")
                .imageUrl("https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&w=900&q=80")
                .build());

        events.add(Event.builder()
                .title("Mesut Süre - Rabarba Canlı Yayın & Gösteri")
                .description("Radyoların en neşeli programı Rabarba, Mesut Süre sunumu ve konuklarıyla sahnede!")
                .dateTime(today.plusDays(340).atTime(20, 30))
                .price(new BigDecimal("350.00"))
                .availableCapacity(85)
                .category("Komedi & Tiyatro")
                .venue("İzmir Nazım Hikmet Kültür Merkezi, İzmir")
                .imageUrl("https://images.unsplash.com/photo-1590602847861-f357a9332bbc?auto=format&fit=crop&w=900&q=80")
                .build());

        return events;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Test kullanıcısını oluştur (varsa ve şifresi null kalmışsa düzelt)
        userRepository.findByUsername("johndoe").ifPresentOrElse(existingUser -> {
            if (existingUser.getPassword() == null || existingUser.getPassword().isBlank()) {
                existingUser.setPassword("password123");
                userRepository.save(existingUser);
            }
        }, () -> {
            User testUser = User.builder()
                    .username("johndoe")
                    .email("john.doe@example.com")
                    .password("password123")
                    .build();
            userRepository.save(testUser);
            System.out.println("Initialized test user: " + testUser.getUsername() + " (ID: " + testUser.getId() + ")");
        });

        List<Event> catalog = buildCatalogEvents();

        // 2. Veritabanında etkinlik yoksa 40 etkinliği ekle; varsa görsellerini ve geçmiş tarihlerini güncel tut
        if (eventRepository.count() < 25) {
            ticketRepository.deleteAll();
            eventRepository.deleteAll();
            eventRepository.saveAll(catalog);
            System.out.println("Successfully seeded 40 events with unique images and rolling 12-month dates!");
        } else {
            List<Event> existing = eventRepository.findAll();
            Map<String, Event> catalogByPrefix = new LinkedHashMap<>();
            for (Event c : catalog) {
                String prefix = c.getTitle().split(" - ")[0].replace(" 2026", "").trim().toLowerCase();
                catalogByPrefix.put(prefix, c);
            }

            LocalDateTime now = LocalDateTime.now();
            boolean anyUpdated = false;
            int fallbackIdx = 0;

            for (Event ev : existing) {
                String evPrefix = ev.getTitle() != null ? ev.getTitle().split(" - ")[0].replace(" 2026", "").trim().toLowerCase() : "";
                Event template = catalogByPrefix.get(evPrefix);
                if (template == null && fallbackIdx < catalog.size()) {
                    template = catalog.get(fallbackIdx);
                }
                fallbackIdx++;

                if (template != null) {
                    boolean imageChanged = !template.getImageUrl().equals(ev.getImageUrl());
                    if (imageChanged) {
                        ev.setImageUrl(template.getImageUrl());
                        anyUpdated = true;
                    }
                    // İlk yenilemede veya geçmişte kalan etkinliklerde tarihleri önümüzdeki 1 yıla yayarak güncel tut
                    if (imageChanged || ev.getDateTime() == null || ev.getDateTime().isBefore(now.plusDays(1))) {
                        ev.setDateTime(template.getDateTime());
                        anyUpdated = true;
                    }
                    // Başlıktaki eski yıl etiketlerini temizle
                    if (ev.getTitle() != null && ev.getTitle().contains(" 2026")) {
                        ev.setTitle(template.getTitle());
                        anyUpdated = true;
                    }
                }
            }

            if (anyUpdated) {
                eventRepository.saveAll(existing);
                System.out.println("Refreshed existing events with unique images and up-to-date rolling 12-month dates!");
            }
        }

        // 3. Veritabanındaki tüm etkinliklerin kapasitelerini Redis'e senkronize et
        for (Event event : eventRepository.findAll()) {
            String redisKey = "event:" + event.getId() + ":capacity";
            redisTemplate.opsForValue().set(redisKey, String.valueOf(event.getAvailableCapacity()));
        }
    }
}

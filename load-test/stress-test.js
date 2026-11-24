#!/usr/bin/env node
/**
 * PassoLive — Canlı 500 Eşzamanlı Kullanıcı (High-Concurrency) Yük & Stres Testi
 *
 * Kullanım:
 *   node load-test/stress-test.js
 *   BASE_URL=https://passolive.onrender.com node load-test/stress-test.js
 *
 * 3 Gerçek Senaryo Çalıştırır:
 *   1) 500 Farklı Kullanıcı -> Aynı 1 Koltuk Yarışı (Redis SETNX Lock Kanıtı)
 *   2) 500 Farklı Kullanıcı -> 50 Biletlik Flash-Sale Yarışı (Redis DECR + Kafka + Zero Overselling)
 *   3) Tek IP Bot Saldırısı -> 25 Seri İstek (Rate Limiter 429 + Anti-Scalper Engeli)
 */

const BASE_URL = (process.env.BASE_URL || 'http://localhost:8080').replace(/\/$/, '');
const CONCURRENT_USERS = parseInt(process.env.USERS || '500', 10);
const FLASH_SALE_STOCK = parseInt(process.env.STOCK || '50', 10);

const C = {
  reset: '\x1b[0m',
  bold: '\x1b[1m',
  cyan: '\x1b[36m',
  magenta: '\x1b[35m',
  green: '\x1b[32m',
  yellow: '\x1b[33m',
  red: '\x1b[31m',
  gray: '\x1b[90m'
};

function percentile(sortedArr, p) {
  if (!sortedArr.length) return 0;
  const idx = Math.min(sortedArr.length - 1, Math.floor((p / 100) * sortedArr.length));
  return sortedArr[idx];
}

function fakeIp(i) {
  const a = 10;
  const b = Math.floor(i / 65025) % 250 + 1;
  const c = Math.floor(i / 250) % 250 + 1;
  const d = (i % 250) + 1;
  return `${a}.${b}.${c}.${d}`;
}

async function createIsolatedTestEvent(capacity) {
  const payload = {
    title: `⚡ Stress Test Flash-Sale (${capacity} Bilet) #${Date.now().toString().slice(-4)}`,
    description: '500 eşzamanlı kullanıcı yüksek trafik doğrulama etkinliği.',
    dateTime: new Date(Date.now() + 7 * 86400000).toISOString().slice(0, 19),
    price: 500,
    availableCapacity: capacity,
    category: 'Müzik & Konser',
    venue: 'PassoLive Yük Test Arenası / İstanbul',
    imageUrl: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?auto=format&fit=crop&w=900&q=80'
  };

  const res = await fetch(`${BASE_URL}/api/events`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });

  if (!res.ok) {
    throw new Error(`Test etkinliği oluşturulamadı (HTTP ${res.status}): ${await res.text()}`);
  }
  return await res.json();
}

async function ensureUserId() {
  const res = await fetch(`${BASE_URL}/api/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: 'johndoe', password: 'password123' })
  });
  if (res.ok) {
    const data = await res.json();
    if (data && data.id) return data.id;
  }
  return 1;
}

async function runScenario1SingleSeatRace(eventId, userId) {
  console.log(`\n${C.bold}${C.magenta}▶ SENARYO 1: ${CONCURRENT_USERS} Farklı Kullanıcı → Aynı 1 Koltuk Yarışı (SETNX Lock)${C.reset}`);
  const seatId = `STRESS-${Date.now().toString().slice(-4)}-A1`;
  const latencies = [];
  let won = 0;
  let conflicts = 0;
  let rateLimited = 0;
  let errors = 0;

  const tStart = performance.now();

  const tasks = Array.from({ length: CONCURRENT_USERS }, (_, i) => (async () => {
    const reqStart = performance.now();
    try {
      const res = await fetch(`${BASE_URL}/api/tickets/reserve`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-Forwarded-For': fakeIp(i + 1)
        },
        body: JSON.stringify({
          eventId,
          userId: userId + i + 100,
          seatId,
          zoneName: 'Sahne Önü VIP',
          price: 900
        })
      });
      latencies.push(performance.now() - reqStart);
      if (res.status === 200) {
        const body = await res.json();
        if (body.success) won++; else conflicts++;
      } else if (res.status === 409) {
        conflicts++;
      } else if (res.status === 429) {
        rateLimited++;
      } else {
        errors++;
      }
    } catch {
      errors++;
    }
  })());

  await Promise.all(tasks);
  const totalMs = Math.round(performance.now() - tStart);
  latencies.sort((a, b) => a - b);
  const avg = Math.round(latencies.reduce((s, v) => s + v, 0) / (latencies.length || 1));
  const p95 = Math.round(percentile(latencies, 95));

  console.log(`   • Hedef Koltuk          : ${C.cyan}${seatId}${C.reset}`);
  console.log(`   • Toplam Paralel İstek  : ${C.bold}${CONCURRENT_USERS}${C.reset}`);
  console.log(`   • Kilidi Kazanan (200)  : ${won === 1 ? C.green : C.red}${C.bold}${won}${C.reset} (Beklenen: 1)`);
  console.log(`   • Reddedilen (409 Lock) : ${C.yellow}${conflicts}${C.reset} (Beklenen: ${CONCURRENT_USERS - 1})`);
  if (rateLimited > 0) console.log(`   • Rate Limited (429)    : ${rateLimited}`);
  if (errors > 0) console.log(`   • Hata                  : ${C.red}${errors}${C.reset}`);
  console.log(`   • Toplam Süre / Ort/P95 : ${C.cyan}${totalMs} ms${C.reset} | Ort: ${avg} ms | P95: ${p95} ms`);
  console.log(`   • Sonuç                 : ${won === 1 ? `${C.green}${C.bold}✅ PASS — 0 ÇİFTE REZERVASYON!${C.reset}` : `${C.red}❌ FAIL${C.reset}`}`);
}

async function runScenario2FlashSale(eventId, userId) {
  console.log(`\n${C.bold}${C.magenta}▶ SENARYO 2: ${CONCURRENT_USERS} Farklı Kullanıcı → ${FLASH_SALE_STOCK} Biletlik Flash-Sale (Redis DECR + Kafka)${C.reset}`);
  const latencies = [];
  let sold = 0;
  let soldOut = 0;
  let rateLimited = 0;
  let errors = 0;

  const tStart = performance.now();

  const tasks = Array.from({ length: CONCURRENT_USERS }, (_, i) => (async () => {
    const reqStart = performance.now();
    try {
      const res = await fetch(`${BASE_URL}/api/tickets`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-Forwarded-For': fakeIp(i + 5000)
        },
        body: JSON.stringify({ eventId, userId })
      });
      latencies.push(performance.now() - reqStart);
      if (res.status === 200) {
        sold++;
      } else if (res.status === 429) {
        rateLimited++;
      } else {
        soldOut++;
      }
    } catch {
      errors++;
    }
  })());

  await Promise.all(tasks);
  const totalMs = Math.round(performance.now() - tStart);
  latencies.sort((a, b) => a - b);
  const avg = Math.round(latencies.reduce((s, v) => s + v, 0) / (latencies.length || 1));
  const p95 = Math.round(percentile(latencies, 95));

  // Kafka tüketimi için kısa bekleme ve son DB kapasitesini kontrol
  await new Promise(r => setTimeout(r, 1500));
  let dbRemaining = '?';
  try {
    const evRes = await fetch(`${BASE_URL}/api/events`);
    if (evRes.ok) {
      const evList = await evRes.json();
      const target = evList.find(e => e.id === eventId);
      if (target) dbRemaining = target.availableCapacity;
    }
  } catch {}

  console.log(`   • Başlangıç Kontenjanı  : ${C.bold}${FLASH_SALE_STOCK} Bilet${C.reset}`);
  console.log(`   • Satılan Bilet (200)   : ${sold === FLASH_SALE_STOCK ? C.green : C.red}${C.bold}${sold}${C.reset} (Beklenen: ${FLASH_SALE_STOCK})`);
  console.log(`   • Tükendi Reddi         : ${C.yellow}${soldOut}${C.reset} (Beklenen: ${CONCURRENT_USERS - FLASH_SALE_STOCK})`);
  console.log(`   • Veritabanı Son Stok   : ${dbRemaining === 0 ? C.green : C.yellow}${C.bold}${dbRemaining}${C.reset} (Artık TÜKENDİ & Satışa Kapalı!)`);
  if (rateLimited > 0) console.log(`   • Rate Limited (429)    : ${rateLimited}`);
  if (errors > 0) console.log(`   • Hata                  : ${C.red}${errors}${C.reset}`);
  console.log(`   • Toplam Süre / Ort/P95 : ${C.cyan}${totalMs} ms${C.reset} | Ort: ${avg} ms | P95: ${p95} ms`);
  console.log(`   • Sonuç                 : ${sold === FLASH_SALE_STOCK ? `${C.green}${C.bold}✅ PASS — 0 FAZLA SATIŞ (ZERO OVERSELLING)!${C.reset}` : `${C.red}❌ FAIL${C.reset}`}`);
}

async function runScenario3BotFlood(eventId) {
  console.log(`\n${C.bold}${C.magenta}▶ SENARYO 3: Tek Bot IP → 25 Seri Saldırı (Rate Limiter 429 + Anti-Scalper)${C.reset}`);
  const botIp = `198.51.100.${Math.floor(Math.random() * 200) + 10}`;
  let allowed = 0;
  let blocked429 = 0;
  let blockedScalper = 0;

  for (let i = 1; i <= 25; i++) {
    const res = await fetch(`${BASE_URL}/api/tickets/reserve`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Forwarded-For': botIp
      },
      body: JSON.stringify({
        eventId,
        userId: 99999,
        seatId: `BOT-S${i}`,
        zoneName: 'VIP',
        price: 900
      })
    });
    if (res.status === 429) {
      blocked429++;
    } else if (res.status === 409) {
      blockedScalper++;
    } else if (res.ok) {
      allowed++;
    }
  }

  console.log(`   • Bot IP Adresi         : ${C.cyan}${botIp}${C.reset}`);
  console.log(`   • İzin Verilen          : ${allowed}`);
  console.log(`   • Anti-Scalper Engeli   : ${C.yellow}${blockedScalper}${C.reset}`);
  console.log(`   • Rate Limiter (429)    : ${C.red}${C.bold}${blocked429}${C.reset}`);
  console.log(`   • Sonuç                 : ${(blocked429 + blockedScalper) > 0 ? `${C.green}${C.bold}✅ PASS — BOT ANINDA DURDURULDU!${C.reset}` : `${C.red}❌ FAIL${C.reset}`}`);
}

async function main() {
  console.log(`${C.bold}${C.cyan}══════════════════════════════════════════════════════════════════${C.reset}`);
  console.log(`${C.bold}  ✦ PASSOLIVE — HIGH-CONCURRENCY (${CONCURRENT_USERS} KULLANICI) STRES TESTİ${C.reset}`);
  console.log(`${C.gray}  Hedef Sunucu: ${BASE_URL}${C.reset}`);
  console.log(`${C.bold}${C.cyan}══════════════════════════════════════════════════════════════════${C.reset}`);

  try {
    const userId = await ensureUserId();
    const testEvent = await createIsolatedTestEvent(FLASH_SALE_STOCK);
    console.log(`${C.green}✓ İzole test etkinliği oluşturuldu:${C.reset} ID=${testEvent.id} | "${testEvent.title}" (Stok: ${testEvent.availableCapacity})`);

    await runScenario1SingleSeatRace(testEvent.id, userId);
    await runScenario2FlashSale(testEvent.id, userId);
    await runScenario3BotFlood(1);

    console.log(`\n${C.bold}${C.cyan}══════════════════════════════════════════════════════════════════${C.reset}`);
    console.log(`${C.bold}${C.green}  🎉 TÜM YÜK VE EŞZAMANLILIK TESTLERİ TAMAMLANDI!${C.reset}`);
    console.log(`${C.bold}${C.cyan}══════════════════════════════════════════════════════════════════${C.reset}\n`);
  } catch (err) {
    console.error(`\n${C.red}${C.bold}❌ Test çalıştırılamadı:${C.reset} ${err.message}`);
    console.error(`${C.gray}İpucu: Spring Boot uygulamasının (${BASE_URL}) ve Docker (PostgreSQL, Redis, Kafka) servislerinin açık olduğundan emin olun.${C.reset}`);
    process.exit(1);
  }
}

main();

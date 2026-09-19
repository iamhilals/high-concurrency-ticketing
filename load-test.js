import http from 'k6/http';
import { check, sleep } from 'k6';

/**
 * k6 High-Concurrency Load Testing Script for PassoLive
 * Simulates high burst traffic to test Redis Seat Locking, Rate Limiting (HTTP 429), and low latency.
 * Run with: k6 run load-test.js
 */
export const options = {
    stages: [
        { duration: '5s', target: 50 },   // Ramp-up to 50 virtual users
        { duration: '10s', target: 200 }, // High load: 200 VUs firing concurrent reservations
        { duration: '5s', target: 0 },    // Ramp-down
    ],
    thresholds: {
        http_req_duration: ['p(95)<100'], // 95% of requests should respond under 100ms
    },
};

export default function () {
    const url = 'http://localhost:8080/api/tickets/reserve';
    const payload = JSON.stringify({
        eventId: 1,
        userId: Math.floor(Math.random() * 100) + 1,
        seatId: `VIP-ROW-A-SEAT-${Math.floor(Math.random() * 30) + 1}`,
        zoneName: 'Sahne Önü VIP',
        price: 1200.0
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(url, payload, params);

    check(res, {
        'status is 200 (reserved), 409 (locked), or 429 (rate limited)': (r) => r.status === 200 || r.status === 409 || r.status === 429,
    });

    sleep(0.1);
}

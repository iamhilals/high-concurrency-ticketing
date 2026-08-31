package com.ticketing.service;

import com.ticketing.entity.Event;
import com.ticketing.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final StringRedisTemplate redisTemplate;

    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        // Redis'teki anlık canlı kapasite değerlerini sorgulayıp nesnelere yansıtıyoruz
        for (Event event : events) {
            String redisKey = "event:" + event.getId() + ":capacity";
            String liveCapacity = redisTemplate.opsForValue().get(redisKey);
            if (liveCapacity != null) {
                try {
                    event.setAvailableCapacity(Integer.parseInt(liveCapacity));
                } catch (NumberFormatException ignored) {}
            }
        }
        return events;
    }
}

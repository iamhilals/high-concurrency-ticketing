package com.ticketing.repository;

import com.ticketing.entity.Event;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Veritabanı seviyesinde ilgili Etkinlik (Event) satırını kilitler (PESSIMISTIC_WRITE).
     * SQL karşılığı: SELECT ... FROM events WHERE id = ? FOR UPDATE
     * Bu işlem tamamlanana (transaction commit/rollback olana) kadar başka hiçbir transaction bu satırı güncelleyemez veya kilitleyemez.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Event e where e.id = :id")
    Optional<Event> findByIdWithPessimisticLock(@Param("id") Long id);

    /**
     * Etkinliğin kapasitesini doğrudan UPDATE sorgusu ile veritabanı seviyesinde atomik olarak 1 azaltır.
     * Bu sayede "SELECT ... FOR UPDATE" kilidine gerek kalmadan veri tutarlılığı korunur.
     */
    @org.springframework.data.jpa.repository.Modifying
    @Query("update Event e set e.availableCapacity = e.availableCapacity - 1 where e.id = :id")
    int decrementCapacity(@Param("id") Long id);
}

package com.oliveyoung.inventory.repository;

import com.oliveyoung.inventory.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByIdempotencyKey(String idempotencyKey);

    Optional<Reservation> findByReservationId(String reservationId);

    List<Reservation> findByStatusAndExpiresAtBefore(String status, LocalDateTime time);
}

package com.biblioteca.api.repository;

import com.biblioteca.api.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findFirstByUserIdAndStatus(Long userId, String status);
    Optional<Reservation> findFirstByBookIdAndStatus(Long bookId, String status);
}

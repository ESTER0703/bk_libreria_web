package com.biblioteca.api.service;

import com.biblioteca.api.dto.ReservationRequest;
import com.biblioteca.api.model.Book;
import com.biblioteca.api.model.Reservation;
import com.biblioteca.api.model.User;
import com.biblioteca.api.repository.BookRepository;
import com.biblioteca.api.repository.ReservationRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;

    public ReservationService(ReservationRepository reservationRepository, BookRepository bookRepository) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<Reservation> getReservations() {
        return reservationRepository.findAll();
    }

    @Transactional
    public Reservation createReservation(ReservationRequest request, HttpServletRequest httpRequest) {
        User authUser = (User) httpRequest.getAttribute("authUser");
        if (authUser == null || authUser.getId() == null) {
            throw new IllegalArgumentException("Usuario no autenticado");
        }

        Book book = bookRepository.findById(request.getBookId()).orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));

        Optional<Reservation> existingUserReservation = reservationRepository.findFirstByUserIdAndStatus(authUser.getId(), "active");
        if (existingUserReservation.isPresent()) {
            throw new IllegalArgumentException("Ya tienes una reserva activa");
        }

        Optional<Reservation> existingBookReservation = reservationRepository.findFirstByBookIdAndStatus(book.getId(), "active");
        if (existingBookReservation.isPresent()) {
            throw new IllegalArgumentException("El libro ya está reservado");
        }

        Reservation reservation = new Reservation();
        reservation.setBookId(book.getId());
        reservation.setUserId(authUser.getId());
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusHours(24));
        reservation.setStatus("active");

        book.setStatus("Reservado");
        bookRepository.save(book);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation cancelReservation(Long id, HttpServletRequest httpRequest) {
        User authUser = (User) httpRequest.getAttribute("authUser");
        if (authUser == null || authUser.getId() == null) {
            throw new IllegalArgumentException("Usuario no autenticado");
        }

        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        if (!authUser.getId().equals(reservation.getUserId())) {
            throw new IllegalArgumentException("No autorizado");
        }

        reservation.setStatus("canceled");
        reservation.setCanceledAt(LocalDateTime.now());

        bookRepository.findById(reservation.getBookId()).ifPresent(book -> {
            book.setStatus("Disponible");
            bookRepository.save(book);
        });

        return reservationRepository.save(reservation);
    }
}

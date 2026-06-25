package com.biblioteca.api.controller;

import com.biblioteca.api.dto.ReservationRequest;
import com.biblioteca.api.model.Reservation;
import com.biblioteca.api.service.ReservationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<Reservation> getReservations() {
        return reservationService.getReservations();
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationRequest request, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(reservationService.createReservation(request, httpRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Reservation> cancelReservation(@PathVariable Long id, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(reservationService.cancelReservation(id, httpRequest));
    }
}

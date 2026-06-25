package com.biblioteca.api.service;

import com.biblioteca.api.dto.ReservationRequest;
import com.biblioteca.api.model.Book;
import com.biblioteca.api.model.Reservation;
import com.biblioteca.api.model.User;
import com.biblioteca.api.repository.BookRepository;
import com.biblioteca.api.repository.ReservationRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void createReservationMarksBookAsReserved() throws Exception {
        Book book = new Book();
        setField(book, "id", 10L);
        book.setStatus("Disponible");

        User user = new User();
        setField(user, "id", 3L);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authUser", user);

        ReservationRequest reservationRequest = new ReservationRequest();
        reservationRequest.setBookId(10L);

        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(reservationRepository.findFirstByUserIdAndStatus(3L, "active")).thenReturn(Optional.empty());
        when(reservationRepository.findFirstByBookIdAndStatus(10L, "active")).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reservation reservation = reservationService.createReservation(reservationRequest, request);

        assertNotNull(reservation);
        assertEquals("active", reservation.getStatus());
        assertEquals(3L, reservation.getUserId());
        assertEquals("Reservado", book.getStatus());
        verify(reservationRepository).save(any(Reservation.class));
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}

package com.biblioteca.api.service;

import com.biblioteca.api.dto.LoanRequest;
import com.biblioteca.api.model.Book;
import com.biblioteca.api.model.Loan;
import com.biblioteca.api.repository.BookRepository;
import com.biblioteca.api.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceFlowTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private LoanService loanService;

    @Test
    void alumnoTomaYDevuelveLibroYBibliotecarioLoVuelveDisponible() {
        Book book = new Book();
        setField(book, "id", 10L);
        book.setStatus("Disponible");

        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(loanRepository.findById(1L)).thenReturn(Optional.of(new Loan()));

        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setBookId(10L);
        loanRequest.setUserId(3L);
        loanRequest.setLoanDate("2026-06-25");
        loanRequest.setReturnDateExpected("2026-06-30");
        loanRequest.setStatus("Activo");
        loanRequest.setObservations("Préstamo de prueba");

        Loan created = loanService.createLoan(loanRequest);
        assertNotNull(created);
        assertEquals("Activo", created.getStatus());
        assertEquals("Prestado", book.getStatus());

        Loan existingLoan = new Loan();
        existingLoan.setBookId(10L);
        setField(existingLoan, "id", 1L);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(existingLoan));

        Loan returned = loanService.returnLoan(1L, null);
        assertNotNull(returned);
        assertEquals("Devuelto", returned.getStatus());
        assertEquals("Disponible", book.getStatus());

        verify(bookRepository, atLeastOnce()).save(any(Book.class));
        verify(loanRepository, atLeastOnce()).save(any(Loan.class));
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

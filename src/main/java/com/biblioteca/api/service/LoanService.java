package com.biblioteca.api.service;

import com.biblioteca.api.dto.LoanRequest;
import com.biblioteca.api.model.Book;
import com.biblioteca.api.model.Loan;
import com.biblioteca.api.repository.BookRepository;
import com.biblioteca.api.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<Loan> getLoans() {
        return loanRepository.findAll();
    }

    @Transactional
    public Loan createLoan(LoanRequest request) {
        Book book = bookRepository.findById(request.getBookId()).orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
        Loan loan = new Loan();
        loan.setBookId(request.getBookId());
        loan.setUserId(request.getUserId());
        loan.setLoanDate(request.getLoanDate());
        loan.setReturnDateExpected(request.getReturnDateExpected());
        loan.setStatus(request.getStatus() != null ? request.getStatus() : "Activo");
        loan.setObservations(request.getObservations());
        loan.setReturnStatus("Pendiente");

        book.setStatus("Prestado");
        bookRepository.save(book);
        return loanRepository.save(loan);
    }

    @Transactional
    public Loan returnLoan(Long id, LoanRequest request) {
        Loan loan = loanRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado"));
        loan.setStatus("Devuelto");
        loan.setReturnedAt(request != null && request.getLoanDate() != null ? request.getLoanDate() : java.time.LocalDate.now().toString());
        loan.setReturnStatus("En tiempo");

        Book book = bookRepository.findById(loan.getBookId()).orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
        book.setStatus("Disponible");
        bookRepository.save(book);
        return loanRepository.save(loan);
    }
}

package com.biblioteca.api.controller;

import com.biblioteca.api.dto.LoanRequest;
import com.biblioteca.api.model.Book;
import com.biblioteca.api.model.Loan;
import com.biblioteca.api.repository.BookRepository;
import com.biblioteca.api.repository.LoanRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = "*")
public class LoanController {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    public LoanController(LoanRepository loanRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public List<Loan> getLoans() {
        return loanRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Loan> createLoan(@RequestBody LoanRequest request) {
        Book book = bookRepository.findById(request.getBookId()).orElseThrow();
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
        return ResponseEntity.ok(loanRepository.save(loan));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<Loan> returnLoan(@PathVariable Long id, @RequestBody(required = false) LoanRequest request) {
        Loan loan = loanRepository.findById(id).orElseThrow();
        loan.setStatus("Devuelto");
        loan.setReturnedAt(request != null && request.getLoanDate() != null ? request.getLoanDate() : java.time.LocalDate.now().toString());
        loan.setReturnStatus("En tiempo");

        Book book = bookRepository.findById(loan.getBookId()).orElseThrow();
        book.setStatus("Disponible");
        bookRepository.save(book);

        return ResponseEntity.ok(loanRepository.save(loan));
    }
}

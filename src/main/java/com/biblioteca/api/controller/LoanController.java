package com.biblioteca.api.controller;

import com.biblioteca.api.dto.LoanRequest;
import com.biblioteca.api.model.Loan;
import com.biblioteca.api.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = "*")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public List<Loan> getLoans() {
        return loanService.getLoans();
    }

    @PostMapping
    public ResponseEntity<Loan> createLoan(@RequestBody LoanRequest request) {
        return ResponseEntity.ok(loanService.createLoan(request));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<Loan> returnLoan(@PathVariable Long id, @RequestBody(required = false) LoanRequest request) {
        return ResponseEntity.ok(loanService.returnLoan(id, request));
    }
}

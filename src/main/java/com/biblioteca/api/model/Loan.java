package com.biblioteca.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "loan_date")
    private String loanDate;

    @Column(name = "return_date_expected")
    private String returnDateExpected;

    private String status;

    private String observations;

    @Column(name = "returned_at")
    private String returnedAt;

    @Column(name = "return_status")
    private String returnStatus;

    public Loan() {
    }

    public Long getId() {
        return id;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(String loanDate) {
        this.loanDate = loanDate;
    }

    public String getReturnDateExpected() {
        return returnDateExpected;
    }

    public void setReturnDateExpected(String returnDateExpected) {
        this.returnDateExpected = returnDateExpected;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getReturnedAt() {
        return returnedAt;
    }

    public void setReturnedAt(String returnedAt) {
        this.returnedAt = returnedAt;
    }

    public String getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(String returnStatus) {
        this.returnStatus = returnStatus;
    }
}

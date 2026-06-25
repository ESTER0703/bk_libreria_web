package com.biblioteca.api.dto;

public class LoanRequest {
    private Long bookId;
    private Long userId;
    private String loanDate;
    private String returnDateExpected;
    private String status;
    private String observations;

    public LoanRequest() {
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
}

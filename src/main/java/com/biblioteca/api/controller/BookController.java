package com.biblioteca.api.controller;

import com.biblioteca.api.dto.BookRequest;
import com.biblioteca.api.model.Book;
import com.biblioteca.api.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<Book> getBooks(HttpServletRequest httpRequest) {
        return bookService.getBooks(httpRequest);
    }

    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam(required = false) String q, HttpServletRequest httpRequest) {
        return bookService.searchBooks(q, httpRequest);
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@ModelAttribute BookRequest request, HttpServletRequest httpRequest) throws IOException {
        return ResponseEntity.ok(bookService.createBook(request, httpRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @ModelAttribute BookRequest request, HttpServletRequest httpRequest) throws IOException {
        return ResponseEntity.ok(bookService.updateBook(id, request, httpRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}

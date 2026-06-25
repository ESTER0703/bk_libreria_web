package com.biblioteca.api.controller;

import com.biblioteca.api.dto.BookRequest;
import com.biblioteca.api.model.Book;
import com.biblioteca.api.repository.BookRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam(required = false) String q) {
        if (q == null || q.isBlank()) {
            return bookRepository.findAll();
        }
        return bookRepository.searchByQuery(q);
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@ModelAttribute BookRequest request) throws IOException {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setEditorial(request.getEditorial());
        book.setCategory(request.getCategory());
        book.setIsbn(request.getIsbn());
        book.setStatus(request.getStatus() != null ? request.getStatus() : "Disponible");
        book.setDateEntry(request.getDateEntry());
        book.setDescription(request.getDescription());

        MultipartFile imageFile = request.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path uploadDir = Paths.get("uploads/books");
            Files.createDirectories(uploadDir);
            Path target = uploadDir.resolve(fileName);
            imageFile.transferTo(target);
            book.setImageUrl("/uploads/books/" + fileName);
        }

        return ResponseEntity.ok(bookRepository.save(book));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @ModelAttribute BookRequest request) throws IOException {
        Book book = bookRepository.findById(id).orElseThrow();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setEditorial(request.getEditorial());
        book.setCategory(request.getCategory());
        book.setIsbn(request.getIsbn());
        book.setStatus(request.getStatus() != null ? request.getStatus() : book.getStatus());
        book.setDateEntry(request.getDateEntry());
        book.setDescription(request.getDescription());

        MultipartFile imageFile = request.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path uploadDir = Paths.get("uploads/books");
            Files.createDirectories(uploadDir);
            Path target = uploadDir.resolve(fileName);
            imageFile.transferTo(target);
            book.setImageUrl("/uploads/books/" + fileName);
        }

        return ResponseEntity.ok(bookRepository.save(book));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

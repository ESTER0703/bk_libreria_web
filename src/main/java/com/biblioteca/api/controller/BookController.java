package com.biblioteca.api.controller;

import com.biblioteca.api.dto.BookRequest;
import com.biblioteca.api.model.Book;
import com.biblioteca.api.repository.BookRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    public ResponseEntity<Book> createBook(@ModelAttribute BookRequest request, HttpServletRequest httpRequest) throws IOException {
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
            String fileName = buildSafeFileName(imageFile);
            Path uploadDir = Paths.get("uploads", "books").toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);
            Path target = uploadDir.resolve(fileName);
            imageFile.transferTo(target);
            book.setImageUrl(buildImageUrl(httpRequest, fileName));
        }

        return ResponseEntity.ok(bookRepository.save(book));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @ModelAttribute BookRequest request, HttpServletRequest httpRequest) throws IOException {
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
            String fileName = buildSafeFileName(imageFile);
            Path uploadDir = Paths.get("uploads", "books").toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);
            Path target = uploadDir.resolve(fileName);
            imageFile.transferTo(target);
            book.setImageUrl(buildImageUrl(httpRequest, fileName));
        }

        return ResponseEntity.ok(bookRepository.save(book));
    }

    private String buildSafeFileName(MultipartFile imageFile) {
        String originalName = imageFile.getOriginalFilename();
        String name = (originalName == null || originalName.isBlank()) ? "image" : originalName;
        String fileName = Paths.get(name).getFileName().toString();
        int lastDot = fileName.lastIndexOf('.');
        String baseName = lastDot >= 0 ? fileName.substring(0, lastDot) : fileName;
        String extension = lastDot >= 0 ? fileName.substring(lastDot) : ".jpg";
        String sanitizedBaseName = baseName.replaceAll("[^a-zA-Z0-9._-]", "_").replaceAll("_+", "_").replaceAll("^_|_$", "");
        if (sanitizedBaseName.isBlank()) {
            sanitizedBaseName = "image";
        }
        return UUID.randomUUID() + "_" + sanitizedBaseName + extension;
    }

    private String buildImageUrl(HttpServletRequest httpRequest, String fileName) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .pathSegment("uploads", "books", fileName)
                .build()
                .encode()
                .toUriString();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

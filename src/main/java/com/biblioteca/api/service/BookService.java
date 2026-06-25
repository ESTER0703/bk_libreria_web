package com.biblioteca.api.service;

import com.biblioteca.api.dto.BookRequest;
import com.biblioteca.api.model.Book;
import com.biblioteca.api.repository.BookRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<Book> getBooks(HttpServletRequest httpRequest) {
        List<Book> books = bookRepository.findAll();
        books.forEach(book -> normalizeBookImageUrl(book, httpRequest));
        return books;
    }

    @Transactional(readOnly = true)
    public List<Book> searchBooks(String query, HttpServletRequest httpRequest) {
        List<Book> books = (query == null || query.isBlank()) ? bookRepository.findAll() : bookRepository.searchByQuery(query);
        books.forEach(book -> normalizeBookImageUrl(book, httpRequest));
        return books;
    }

    @Transactional
    public Book createBook(BookRequest request, HttpServletRequest httpRequest) throws IOException {
        Book book = new Book();
        applyBookRequest(book, request);
        MultipartFile imageFile = request.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = buildSafeFileName(imageFile);
            Path uploadDir = Paths.get("uploads", "books").toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);
            Path target = uploadDir.resolve(fileName);
            imageFile.transferTo(target);
            book.setImageUrl(buildImageUrl(httpRequest, fileName));
        }
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Long id, BookRequest request, HttpServletRequest httpRequest) throws IOException {
        Book book = bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
        applyBookRequest(book, request);
        MultipartFile imageFile = request.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = buildSafeFileName(imageFile);
            Path uploadDir = Paths.get("uploads", "books").toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);
            Path target = uploadDir.resolve(fileName);
            imageFile.transferTo(target);
            book.setImageUrl(buildImageUrl(httpRequest, fileName));
        }
        return bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    private void applyBookRequest(Book book, BookRequest request) {
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setEditorial(request.getEditorial());
        book.setCategory(request.getCategory());
        book.setIsbn(request.getIsbn());
        book.setStatus(request.getStatus() != null ? request.getStatus() : "Disponible");
        book.setDateEntry(request.getDateEntry());
        book.setDescription(request.getDescription());
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

    private void normalizeBookImageUrl(Book book, HttpServletRequest httpRequest) {
        if (book == null || book.getImageUrl() == null || book.getImageUrl().isBlank()) {
            return;
        }

        String imageUrl = book.getImageUrl();
        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            return;
        }

        String pathValue = imageUrl.startsWith("/") ? imageUrl.substring(1) : imageUrl;
        String[] segments = pathValue.split("/");
        StringBuilder normalizedPath = new StringBuilder();
        for (String segment : segments) {
            if (segment.isBlank()) continue;
            if (normalizedPath.length() > 0) {
                normalizedPath.append('/');
            }
            normalizedPath.append(URLEncoder.encode(segment, StandardCharsets.UTF_8));
        }

        book.setImageUrl(buildBaseUrl(httpRequest) + "/" + normalizedPath);
    }

    private String buildBaseUrl(HttpServletRequest httpRequest) {
        String scheme = httpRequest.getHeader("X-Forwarded-Proto");
        if (scheme == null || scheme.isBlank()) {
            scheme = httpRequest.getScheme();
        }

        String host = httpRequest.getHeader("X-Forwarded-Host");
        if (host == null || host.isBlank()) {
            host = httpRequest.getHeader("Host");
        }
        if (host == null || host.isBlank()) {
            host = httpRequest.getServerName();
            int port = httpRequest.getServerPort();
            if (port != 80 && port != 443) {
                host = host + ":" + port;
            }
        }

        return scheme + "://" + host;
    }

    private String buildImageUrl(HttpServletRequest httpRequest, String fileName) {
        return buildBaseUrl(httpRequest) + "/uploads/books/" + URLEncoder.encode(fileName, StandardCharsets.UTF_8);
    }
}

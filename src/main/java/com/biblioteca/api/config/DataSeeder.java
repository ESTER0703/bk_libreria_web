package com.biblioteca.api.config;

import com.biblioteca.api.model.Book;
import com.biblioteca.api.model.Loan;
import com.biblioteca.api.model.User;
import com.biblioteca.api.repository.BookRepository;
import com.biblioteca.api.repository.LoanRepository;
import com.biblioteca.api.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class DataSeeder implements ApplicationRunner {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;

    public DataSeeder(BookRepository bookRepository, LoanRepository loanRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Path uploadDir = Paths.get("uploads", "books");
        Files.createDirectories(uploadDir);

        if (bookRepository.count() == 0) {
            Book book1 = new Book();
            book1.setTitle("Fundamentos de Java");
            book1.setAuthor("Ana Pérez");
            book1.setEditorial("Editorial UTN");
            book1.setCategory("Programación");
            book1.setIsbn("978-1111111111");
            book1.setStatus("Disponible");
            book1.setDateEntry("2026-06-01");
            book1.setDescription("Guía práctica para introducirse en el lenguaje Java.");

            Book book2 = new Book();
            book2.setTitle("Diseño de Interfaces");
            book2.setAuthor("Luis Gómez");
            book2.setEditorial("Técnica Editores");
            book2.setCategory("Diseño");
            book2.setIsbn("978-2222222222");
            book2.setStatus("Disponible");
            book2.setDateEntry("2026-06-02");
            book2.setDescription("Conceptos clave para construir experiencias digitales claras.");

            Book book3 = new Book();
            book3.setTitle("Bases de Datos Modernas");
            book3.setAuthor("Carla Ruiz");
            book3.setEditorial("Data Books");
            book3.setCategory("Tecnologías");
            book3.setIsbn("978-3333333333");
            book3.setStatus("Prestado");
            book3.setDateEntry("2026-06-03");
            book3.setDescription("Métodos actuales para modelar y consultar información.");

            bookRepository.saveAll(List.of(book1, book2, book3));
        }

        if (loanRepository.count() == 0) {
            List<User> users = userRepository.findAll();
            List<Book> books = bookRepository.findAll();
            if (!users.isEmpty() && !books.isEmpty()) {
                Book borrowedBook = books.stream().filter(b -> "Prestado".equalsIgnoreCase(b.getStatus())).findFirst().orElse(books.get(0));
                User user = users.get(0);

                Loan loan = new Loan();
                loan.setBookId(borrowedBook.getId());
                loan.setUserId(user.getId());
                loan.setLoanDate("2026-06-20");
                loan.setReturnDateExpected("2026-06-27");
                loan.setStatus("Activo");
                loan.setObservations("Préstamo de ejemplo cargado al iniciar el sistema");
                loan.setReturnStatus("Pendiente");
                loanRepository.save(loan);
            }
        }
    }
}

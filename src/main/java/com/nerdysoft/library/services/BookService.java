package com.nerdysoft.library.services;

import com.nerdysoft.library.entities.Book;
import com.nerdysoft.library.repositories.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepo;

    public BookService(BookRepository bookRepo) {
        this.bookRepo = bookRepo;
    }

    @Transactional
    public Book addBook(Book book) {
        Optional<Book> existing = bookRepo.findByTitleAndAuthor(book.getTitle(), book.getAuthor());
        if (existing.isPresent()) {
            Book exBook = existing.get();
            exBook.setAmount(exBook.getAmount() + 1);
            return bookRepo.save(exBook);
        }
        return bookRepo.save(book);
    }

    public void deleteBook(Long id) {
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Книга не знайдена"));
        if (!book.getBorrowers().isEmpty()) {
            throw new IllegalStateException("Неможливо видалити книгу: є позичені примірники");
        }
        bookRepo.delete(book);
    }

}
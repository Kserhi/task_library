package com.nerdysoft.library.services;

import com.nerdysoft.library.entities.Book;
import com.nerdysoft.library.dto.BorrowedBookCount;
import com.nerdysoft.library.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepo;

    @Transactional
    public Book addBook(Book book) {
        Optional<Book> existing = bookRepo.findByTitleAndAuthor(book.getTitle(), book.getAuthor());
        if (existing.isPresent()) {
            Book ex = existing.get();
            ex.setAmount(ex.getAmount() + 1);
            return bookRepo.save(ex);
        }
        return bookRepo.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepo.findById(id).orElseThrow(() -> new RuntimeException("Книга не знайдена"));
    }

    @Transactional
    public Book updateBook(Long id, Book updated) {
        Book ex = bookRepo.findById(id).orElseThrow(() -> new RuntimeException("Книга не знайдена"));
        ex.setTitle(updated.getTitle());
        ex.setAuthor(updated.getAuthor());
        ex.setAmount(updated.getAmount());
        return bookRepo.save(ex);
    }

    public void deleteBook(Long id) {
        Book ex = bookRepo.findById(id).orElseThrow(() -> new RuntimeException("Книга не знайдена"));
        if (!ex.getBorrowers().isEmpty()) {
            throw new IllegalStateException("Неможливо видалити книгу: вона позичена");
        }
        bookRepo.delete(ex);
    }

    public List<Book> getBooksBorrowedByMemberName(String name) {
        return bookRepo.findByBorrowers_Name(name);
    }

    public List<String> getDistinctBorrowedBookTitles() {
        return bookRepo.findUniqueBorrowedTitles();
    }

    public List<BorrowedBookCount> getBorrowedBookCounts() {
        return bookRepo.findBorrowedBookCounts();
    }
}
package com.nerdysoft.library.services;

import com.nerdysoft.library.dto.BorrowedBookCount;
import com.nerdysoft.library.entities.Book;
import com.nerdysoft.library.repositories.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;


    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }


    @Transactional
    public Book addBook(Book book) {
        Optional<Book> existing = bookRepository.findByTitleAndAuthor(book.getTitle(), book.getAuthor());
        if (existing.isPresent()) {
            Book exBook = existing.get();
            exBook.setAmount(exBook.getAmount() + 1);
            return bookRepository.save(exBook);
        }
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Книгу з ID " + id + " не знайдено"));
    }

    @Transactional
    public Book updateBook(Long id, Book updatedBook) {
        Book existing = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Книгу з ID " + id + " не знайдено"));

        existing.setTitle(updatedBook.getTitle());
        existing.setAuthor(updatedBook.getAuthor());
        existing.setAmount(updatedBook.getAmount());

        return bookRepository.save(existing);
    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Книга не знайдена"));

        if (!book.getBorrowers().isEmpty()) {
            throw new IllegalStateException("Неможливо видалити книгу: вона позичена хоча б одним учасником");
        }

        bookRepository.delete(book);
    }

    public List<Book> getBooksBorrowedByMemberName(String name) {
        return bookRepository.findByBorrowers_Name(name);
    }

    public List<String> getDistinctBorrowedBookTitles() {
        return bookRepository.findUniqueBorrowedTitles();
    }

    public List<BorrowedBookCount> getBorrowedBookCounts() {
        return bookRepository.findBorrowedBookCounts();
    }

}
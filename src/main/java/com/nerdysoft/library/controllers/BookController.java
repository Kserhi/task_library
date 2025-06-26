package com.nerdysoft.library.controllers;

import com.nerdysoft.library.dto.BorrowedBookCount;
import com.nerdysoft.library.entities.Book;
import com.nerdysoft.library.services.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Validated
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<Book> addBook(@Valid @RequestBody Book book) {
        Book saved = bookService.addBook(book);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody Book updatedBook
    ) {
        return ResponseEntity.ok(bookService.updateBook(id, updatedBook));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/borrowed/by-member/{name}")
    public ResponseEntity<List<Book>> getBooksBorrowedByMemberName(@PathVariable String name) {
        return ResponseEntity.ok(bookService.getBooksBorrowedByMemberName(name));
    }

    @GetMapping("/borrowed/distinct-titles")
    public ResponseEntity<List<String>> getDistinctBorrowedTitles() {
        return ResponseEntity.ok(bookService.getDistinctBorrowedBookTitles());
    }

    @GetMapping("/borrowed/statistics")
    public ResponseEntity<List<BorrowedBookCount>> getBorrowedBookStatistics() {
        return ResponseEntity.ok(bookService.getBorrowedBookCounts());
    }
}
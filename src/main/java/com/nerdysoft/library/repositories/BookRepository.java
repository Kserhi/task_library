package com.nerdysoft.library.repositories;

import com.nerdysoft.library.dto.BorrowedBookCount;
import com.nerdysoft.library.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByTitleAndAuthor(String title, String author);
    List<Book> findByBorrowers_Name(String name);

    @Query("SELECT DISTINCT b.title FROM Book b JOIN b.borrowers m")
    List<String> findUniqueBorrowedTitles();

    @Query("SELECT new com.example.library.dto.BorrowedBookCount(b.title, COUNT(m)) "
            + "FROM Book b JOIN b.borrowers m GROUP BY b.title")
    List<BorrowedBookCount> findBorrowedBookCounts();
}


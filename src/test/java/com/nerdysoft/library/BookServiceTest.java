package com.nerdysoft.library;

import com.nerdysoft.library.entities.Book;
import com.nerdysoft.library.entities.Member;
import com.nerdysoft.library.repositories.BookRepository;
import com.nerdysoft.library.dto.BorrowedBookCount;
import com.nerdysoft.library.services.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepo;

    @InjectMocks
    private BookService bookService;

    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        book1 = new Book(1L, "Java", "John Doe", 1, new HashSet<>());
        book2 = new Book(2L, "Spring", "Jane Smith", 2, new HashSet<>());
    }

    @Test
    void whenAddNewBook_thenSaved() {
        when(bookRepo.findByTitleAndAuthor("Java", "John Doe")).thenReturn(Optional.empty());
        when(bookRepo.save(any(Book.class))).thenReturn(book1);

        Book result = bookService.addBook(new Book(null, "Java", "John Doe", 1, new HashSet<>()));

        assertNotNull(result);
        assertEquals("Java", result.getTitle());
        assertEquals("John Doe", result.getAuthor());
        assertEquals(1, result.getAmount());
        verify(bookRepo).save(any(Book.class));
    }

    @Test
    void whenAddExistingBook_thenIncrementAmount() {
        Book existing = new Book(1L, "Java", "John Doe", 2, new HashSet<>());
        when(bookRepo.findByTitleAndAuthor("Java", "John Doe")).thenReturn(Optional.of(existing));
        when(bookRepo.save(existing)).thenReturn(existing);

        Book result = bookService.addBook(new Book(null, "Java", "John Doe", 0, new HashSet<>()));

        assertEquals(3, result.getAmount());
        verify(bookRepo).save(existing);
    }

    @Test
    void whenGetAllBooks_thenReturnList() {
        when(bookRepo.findAll()).thenReturn(List.of(book1, book2));

        List<Book> list = bookService.getAllBooks();

        assertEquals(2, list.size());
        assertTrue(list.contains(book1));
        assertTrue(list.contains(book2));
        verify(bookRepo).findAll();
    }

    @Test
    void whenGetBookByIdFound_thenReturn() {
        when(bookRepo.findById(1L)).thenReturn(Optional.of(book1));

        Book result = bookService.getBookById(1L);

        assertEquals(book1, result);
        verify(bookRepo).findById(1L);
    }

    @Test
    void whenGetBookByIdNotFound_thenThrow() {
        when(bookRepo.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookService.getBookById(99L));
        assertTrue(ex.getMessage().contains("не знайдена"));
        verify(bookRepo).findById(99L);
    }

    @Test
    void whenUpdateBook_thenFieldsUpdated() {
        when(bookRepo.findById(1L)).thenReturn(Optional.of(book1));
        when(bookRepo.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));

        Book updated = new Book(null, "Java Advanced", "John Doe", 5, new HashSet<>());
        Book result = bookService.updateBook(1L, updated);

        assertEquals("Java Advanced", result.getTitle());
        assertEquals(5, result.getAmount());
        verify(bookRepo).save(book1);
    }

    @Test
    void whenDeleteBookNotBorrowed_thenDeleted() {
        when(bookRepo.findById(1L)).thenReturn(Optional.of(book1));

        bookService.deleteBook(1L);

        verify(bookRepo).delete(book1);
    }

    @Test
    void whenDeleteBookBorrowed_thenThrow() {
        book1.getBorrowers().add(mock(Member.class));
        when(bookRepo.findById(1L)).thenReturn(Optional.of(book1));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> bookService.deleteBook(1L));
        assertTrue(ex.getMessage().contains("позичена"));
        verify(bookRepo, never()).delete(any());
    }

    @Test
    void whenGetBooksBorrowedByMemberName_thenDelegates() {
        when(bookRepo.findByBorrowers_Name("Alice")).thenReturn(List.of(book2));

        List<Book> list = bookService.getBooksBorrowedByMemberName("Alice");

        assertEquals(1, list.size());
        assertEquals(book2, list.get(0));
        verify(bookRepo).findByBorrowers_Name("Alice");
    }

    @Test
    void whenGetDistinctBorrowedBookTitles_thenReturn() {
        when(bookRepo.findUniqueBorrowedTitles()).thenReturn(List.of("Java", "Spring"));

        List<String> titles = bookService.getDistinctBorrowedBookTitles();

        assertEquals(2, titles.size());
        verify(bookRepo).findUniqueBorrowedTitles();
    }

    @Test
    void whenGetBorrowedBookCounts_thenReturnDTOs() {
        BorrowedBookCount dto = new BorrowedBookCount("Java", 3L);
        when(bookRepo.findBorrowedBookCounts()).thenReturn(List.of(dto));

        List<BorrowedBookCount> counts = bookService.getBorrowedBookCounts();

        assertEquals(1, counts.size());
        assertEquals("Java", counts.get(0).getTitle());
        assertEquals(3L, counts.get(0).getCount());
        verify(bookRepo).findBorrowedBookCounts();
    }
}
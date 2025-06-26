package com.nerdysoft.library;


import com.nerdysoft.library.entities.Member;
import com.nerdysoft.library.entities.Book;
import com.nerdysoft.library.repositories.MemberRepository;
import com.nerdysoft.library.repositories.BookRepository;
import com.nerdysoft.library.services.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepo;

    @Mock
    private BookRepository bookRepo;

    @InjectMocks
    private MemberService memberService;

    private Member member;
    private Book book;

    @BeforeEach
    void setUp() {
        member = new Member(1L, "Alice", null, new HashSet<>());
        book = new Book(2L, "Java", "John Doe", 1, new HashSet<>());
        ReflectionTestUtils.setField(memberService, "borrowLimit", 2);
    }

    @Test
    void whenCreateMember_thenSaved() {
        when(memberRepo.save(any(Member.class))).thenReturn(member);

        Member result = memberService.createMember(new Member(null, "Alice", null, new HashSet<>()));

        assertEquals("Alice", result.getName());
        verify(memberRepo).save(any(Member.class));
    }

    @Test
    void whenGetAllMembers_thenReturnList() {
        when(memberRepo.findAll()).thenReturn(List.of(member));

        List<Member> list = memberService.getAllMembers();

        assertEquals(1, list.size());
        verify(memberRepo).findAll();
    }

    @Test
    void whenGetMemberByIdFound_thenReturn() {
        when(memberRepo.findById(1L)).thenReturn(Optional.of(member));

        Member result = memberService.getMemberById(1L);

        assertEquals(member, result);
        verify(memberRepo).findById(1L);
    }

    @Test
    void whenGetMemberByIdNotFound_thenThrow() {
        when(memberRepo.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> memberService.getMemberById(99L));
        assertTrue(ex.getMessage().contains("не знайдений"));
        verify(memberRepo).findById(99L);
    }

    @Test
    void whenUpdateMember_thenNameChanged() {
        when(memberRepo.findById(1L)).thenReturn(Optional.of(member));
        when(memberRepo.save(any(Member.class))).thenAnswer(i -> i.getArgument(0));

        Member upd = new Member(null, "Bob", null, new HashSet<>());
        Member result = memberService.updateMember(1L, upd);

        assertEquals("Bob", result.getName());
        verify(memberRepo).save(member);
    }

    @Test
    void whenDeleteMemberWithoutBorrowed_thenDeleted() {
        when(memberRepo.findById(1L)).thenReturn(Optional.of(member));

        memberService.deleteMember(1L);

        verify(memberRepo).delete(member);
    }

    @Test
    void whenDeleteMemberWithBorrowed_thenThrow() {
        member.getBorrowedBooks().add(book);
        when(memberRepo.findById(1L)).thenReturn(Optional.of(member));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> memberService.deleteMember(1L));
        assertTrue(ex.getMessage().contains("нездані книги"));
        verify(memberRepo, never()).delete(any());
    }


    @Test
    void whenBorrowBookLimitExceeded_thenThrow() {
        // simulate already borrowed up to limit
        Book b1 = new Book(3L, "X", "Y Z", 1, new HashSet<>());
        Book b2 = new Book(4L, "A", "B C", 1, new HashSet<>());
        member.getBorrowedBooks().addAll(List.of(b1, b2));
        when(memberRepo.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepo.findById(2L)).thenReturn(Optional.of(book));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> memberService.borrowBook(1L, 2L));
        assertTrue(ex.getMessage().contains("Перевищено ліміт"));
    }

    @Test
    void whenBorrowBookNotAvailable_thenThrow() {
        book.setAmount(0);
        when(memberRepo.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepo.findById(2L)).thenReturn(Optional.of(book));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> memberService.borrowBook(1L, 2L));
        assertTrue(ex.getMessage().contains("недоступна"));
    }
}
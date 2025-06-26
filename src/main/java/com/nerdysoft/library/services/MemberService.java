package com.nerdysoft.library.services;

import com.nerdysoft.library.entities.Book;
import com.nerdysoft.library.entities.Member;
import com.nerdysoft.library.repositories.BookRepository;
import com.nerdysoft.library.repositories.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final MemberRepository memberRepo;
    private final BookRepository bookRepo;

    @Value("${library.borrow.limit}")
    private int borrowLimit;

    public MemberService(MemberRepository memberRepo, BookRepository bookRepo) {
        this.memberRepo = memberRepo;
        this.bookRepo = bookRepo;
    }

    public Member createMember(Member member) {
        return memberRepo.save(member);
    }

    @Transactional
    public void borrowBook(Long memberId, Long bookId) {
        Member member = memberRepo.findById(memberId).orElseThrow();
        Book book = bookRepo.findById(bookId).orElseThrow();
        if (member.getBorrowedBooks().size() >= borrowLimit) {
            throw new IllegalStateException("Перевищено ліміт позичених книг");
        }
        if (book.getAmount() <= 0) {
            throw new IllegalStateException("Книга недоступна");
        }
        book.setAmount(book.getAmount() - 1);
        member.getBorrowedBooks().add(book);
        book.getBorrowers().add(member);
        memberRepo.save(member);
    }

    @Transactional
    public void returnBook(Long memberId, Long bookId) {
        Member member = memberRepo.findById(memberId).orElseThrow();
        Book book = bookRepo.findById(bookId).orElseThrow();
        if (!member.getBorrowedBooks().remove(book)) {
            throw new IllegalStateException("Користувач не позичав цю книгу");
        }
        book.getBorrowers().remove(member);
        book.setAmount(book.getAmount() + 1);
        memberRepo.save(member);
    }

    public void deleteMember(Long id) {
        Member member = memberRepo.findById(id).orElseThrow();
        if (!member.getBorrowedBooks().isEmpty()) {
            throw new IllegalStateException("Неможливо видалити учасника: є нездані книги");
        }
        memberRepo.delete(member);
    }

}
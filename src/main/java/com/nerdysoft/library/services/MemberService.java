package com.nerdysoft.library.services;

import com.nerdysoft.library.entities.Book;
import com.nerdysoft.library.entities.Member;
import com.nerdysoft.library.repositories.BookRepository;
import com.nerdysoft.library.repositories.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Member> getAllMembers() {
        return memberRepo.findAll();
    }

    public Member getMemberById(Long id) {
        return memberRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Учасника з ID " + id + " не знайдено"));
    }

    public Member updateMember(Long id, Member updatedMember) {
        Member existing = memberRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Учасника з ID " + id + " не знайдено"));

        existing.setName(updatedMember.getName());
        return memberRepo.save(existing);
    }

    public void deleteMember(Long id) {
        Member member = memberRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Учасник не знайдений"));

        if (!member.getBorrowedBooks().isEmpty()) {
            throw new IllegalStateException("Неможливо видалити учасника: є нездані книги");
        }

        memberRepo.delete(member);
    }

    @Transactional
    public void borrowBook(Long memberId, Long bookId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Учасник не знайдений"));
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Книга не знайдена"));

        if (member.getBorrowedBooks().size() >= borrowLimit) {
            throw new IllegalStateException("Перевищено ліміт позичених книг");
        }

        if (book.getAmount() <= 0) {
            throw new IllegalStateException("Книга недоступна");
        }

        if (member.getBorrowedBooks().contains(book)) {
            throw new IllegalStateException("Учасник вже позичив цю книгу");
        }

        book.setAmount(book.getAmount() - 1);
        member.getBorrowedBooks().add(book);
        book.getBorrowers().add(member);

        memberRepo.save(member);
        bookRepo.save(book);
    }

    @Transactional
    public void returnBook(Long memberId, Long bookId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Учасник не знайдений"));
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Книга не знайдена"));

        if (!member.getBorrowedBooks().contains(book)) {
            throw new IllegalStateException("Ця книга не була позичена цим учасником");
        }

        member.getBorrowedBooks().remove(book);
        book.getBorrowers().remove(member);
        book.setAmount(book.getAmount() + 1);

        memberRepo.save(member);
        bookRepo.save(book);
    }
}
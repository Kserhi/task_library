package com.nerdysoft.library.services;

import com.nerdysoft.library.entities.Member;
import com.nerdysoft.library.entities.Book;
import com.nerdysoft.library.repositories.MemberRepository;
import com.nerdysoft.library.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepo;
    private final BookRepository bookRepo;

    @Value("${library.borrow.limit}")
    private int borrowLimit;

    public Member createMember(Member member) {
        return memberRepo.save(member);
    }

    public List<Member> getAllMembers() {
        return memberRepo.findAll();
    }

    public Member getMemberById(Long id) {
        return memberRepo.findById(id).orElseThrow(() -> new RuntimeException("Учасник не знайдений"));
    }

    public Member updateMember(Long id, Member upd) {
        Member ex = memberRepo.findById(id).orElseThrow(() -> new RuntimeException("Учасник не знайдений"));
        ex.setName(upd.getName());
        return memberRepo.save(ex);
    }

    public void deleteMember(Long id) {
        Member ex = memberRepo.findById(id).orElseThrow(() -> new RuntimeException("Учасник не знайдений"));
        if (!ex.getBorrowedBooks().isEmpty()) {
            throw new IllegalStateException("Неможливо видалити учасника: є нездані книги");
        }
        memberRepo.delete(ex);
    }

    @Transactional
    public void borrowBook(Long memberId, Long bookId) {
        Member m = memberRepo.findById(memberId).orElseThrow(() -> new RuntimeException("Учасник не знайдений"));
        Book b = bookRepo.findById(bookId).orElseThrow(() -> new RuntimeException("Книга не знайдена"));
        if (m.getBorrowedBooks().size() >= borrowLimit) {
            throw new IllegalStateException("Перевищено ліміт");
        }
        if (b.getAmount() <= 0) {
            throw new IllegalStateException("Книга недоступна");
        }
        if (m.getBorrowedBooks().contains(b)) {
            throw new IllegalStateException("Вже позичена");
        }
        b.setAmount(b.getAmount() - 1);
        m.getBorrowedBooks().add(b);
        b.getBorrowers().add(m);
        memberRepo.save(m);
        bookRepo.save(b);
    }

    @Transactional
    public void returnBook(Long memberId, Long bookId) {
        Member m = memberRepo.findById(memberId).orElseThrow(() -> new RuntimeException("Учасник не знайдений"));
        Book b = bookRepo.findById(bookId).orElseThrow(() -> new RuntimeException("Книга не знайдена"));
        if (!m.getBorrowedBooks().contains(b)) {
            throw new IllegalStateException("Не позичав");
        }
        m.getBorrowedBooks().remove(b);
        b.getBorrowers().remove(m);
        b.setAmount(b.getAmount() + 1);
        memberRepo.save(m);
        bookRepo.save(b);
    }
}
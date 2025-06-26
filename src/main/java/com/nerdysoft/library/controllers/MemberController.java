package com.nerdysoft.library.controllers;


import com.nerdysoft.library.entities.Member;
import com.nerdysoft.library.services.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Validated
public class MemberController {
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Member> create(@Valid @RequestBody Member member) {
        return new ResponseEntity<>(memberService.createMember(member), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Member>> getAll() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> getById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Member> update(@PathVariable Long id, @Valid @RequestBody Member member) {
        return ResponseEntity.ok(memberService.updateMember(id, member));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{memberId}/borrow/{bookId}")
    public ResponseEntity<Void> borrow(@PathVariable Long memberId, @PathVariable Long bookId) {
        memberService.borrowBook(memberId, bookId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{memberId}/return/{bookId}")
    public ResponseEntity<Void> returnBook(@PathVariable Long memberId, @PathVariable Long bookId) {
        memberService.returnBook(memberId, bookId);
        return ResponseEntity.ok().build();
    }
}
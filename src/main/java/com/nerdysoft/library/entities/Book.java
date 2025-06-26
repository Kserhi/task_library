package com.nerdysoft.library.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3)
    @Pattern(regexp = "^[A-Z].*")
    private String title;

    @NotBlank
    @Pattern(regexp = "^[A-Z][a-z]+ [A-Z][a-z]+$")
    private String author;

    @Column(nullable = false)
    private int amount;

    @ManyToMany(mappedBy = "borrowedBooks")
    private Set<Member> borrowers = new HashSet<>();
}
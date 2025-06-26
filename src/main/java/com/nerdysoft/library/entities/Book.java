package com.nerdysoft.library.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @NotBlank @Size(min = 3) @Pattern(regexp = "^[A-Z].*")
    @ToString.Include
    private String title;

    @NotBlank @Pattern(regexp = "^[A-Z][a-z]+ [A-Z][a-z]+$")
    @ToString.Include
    private String author;

    @Column(nullable = false)
    @ToString.Include
    private int amount;

    @ManyToMany(mappedBy = "borrowedBooks")
    @JsonIgnore
    private Set<Member> borrowers = new HashSet<>();
}
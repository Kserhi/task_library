package com.nerdysoft.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BorrowedBookCount {
    private String title;
    private Long count;
}
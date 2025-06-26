package com.nerdysoft.library.dto;

import lombok.Data;

@Data
public class BorrowedBookCount {
    private String title;
    private Long count;
}

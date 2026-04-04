package com.zorvyn.finance.dto;

import com.zorvyn.finance.model.TransactionType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private String category;
    private String description;
    private LocalDate date;
    private LocalDateTime createdAt;
    private String username;
}

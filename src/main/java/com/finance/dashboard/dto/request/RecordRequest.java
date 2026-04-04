package com.finance.dashboard.dto.request;

import com.finance.dashboard.enums.RecordType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecordRequest(

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        @Digits(integer = 13, fraction = 2, message = "Amount format: up to 13 digits, 2 decimal places")
        BigDecimal amount,

        @NotNull(message = "Type is required (INCOME or EXPENSE)")
        RecordType type,

        @NotBlank(message = "Category is required")
        @Size(max = 100, message = "Category must not exceed 100 characters")
        String category,

        @NotNull(message = "Record date is required")
        LocalDate recordDate,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description

) {}

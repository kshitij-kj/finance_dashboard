package com.finance.dashboard.dto.response;

import com.finance.dashboard.enums.RecordType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record RecordResponse(
        UUID id,
        BigDecimal amount,
        RecordType type,
        String category,
        LocalDate recordDate,
        String description,
        String createdByEmail,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

package com.finance.dashboard.dto.response;

import com.finance.dashboard.enums.RecordType;

import java.math.BigDecimal;

public record MonthlyTrendResponse(
        String month,        // e.g. "2024-11"
        RecordType type,
        BigDecimal total
) {
}

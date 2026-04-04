package com.finance.dashboard.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal netBalance,
        long totalRecords,
        List<CategoryTotalResponse> categoryTotals,
        List<RecordResponse> recentActivity
) {
}

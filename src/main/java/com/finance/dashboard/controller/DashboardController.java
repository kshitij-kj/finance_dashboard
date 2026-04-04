package com.finance.dashboard.controller;

import com.finance.dashboard.dto.response.CategoryTotalResponse;
import com.finance.dashboard.dto.response.DashboardSummaryResponse;
import com.finance.dashboard.dto.response.MonthlyTrendResponse;
import com.finance.dashboard.enums.RecordType;
import com.finance.dashboard.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final AnalyticsService analyticsService;

    /**
     * Full dashboard summary — total income, expenses, net balance,
     * category breakdown, and 10 most recent records.
     * Roles: ANALYST, ADMIN
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary() {
        return ResponseEntity.ok(analyticsService.getSummary());
    }

    /**
     * Monthly income vs expense totals — used for trend charts.
     * Returns newest months first.
     * Roles: ANALYST, ADMIN
     */
    @GetMapping("/trends/monthly")
    public ResponseEntity<List<MonthlyTrendResponse>> getMonthlyTrends() {
        return ResponseEntity.ok(analyticsService.getMonthlyTrends());
    }

    /**
     * Category breakdown with optional filters.
     * Pass ?type=EXPENSE to see expense categories only.
     * Pass ?startDate=2024-01-01&endDate=2024-12-31 to scope to a date range.
     * Roles: ANALYST, ADMIN
     */
    @GetMapping("/breakdown")
    public ResponseEntity<List<CategoryTotalResponse>> getCategoryBreakdown(
            @RequestParam(required = false) RecordType type,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(
                analyticsService.getCategoryBreakdown(type, startDate, endDate));
    }
}

package com.finance.dashboard.service;

import com.finance.dashboard.dto.response.CategoryTotalResponse;
import com.finance.dashboard.dto.response.DashboardSummaryResponse;
import com.finance.dashboard.dto.response.MonthlyTrendResponse;
import com.finance.dashboard.dto.response.RecordResponse;
import com.finance.dashboard.enums.RecordType;
import com.finance.dashboard.mapper.RecordMapper;
import com.finance.dashboard.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final FinancialRecordRepository recordRepository;
    private final RecordMapper recordMapper;

    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public DashboardSummaryResponse getSummary() {
        BigDecimal totalIncome = recordRepository
                .sumByType(RecordType.INCOME)
                .orElse(BigDecimal.ZERO);

        BigDecimal totalExpenses = recordRepository
                .sumByType(RecordType.EXPENSE)
                .orElse(BigDecimal.ZERO);

        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        long totalRecords = recordRepository.countActive();

        List<CategoryTotalResponse> categoryTotals = recordRepository
                .sumByCategory()
                .stream()
                .map(row -> new CategoryTotalResponse(
                        (String) row[0],
                        (BigDecimal) row[1]
                ))
                .toList();

        List<RecordResponse> recentActivity = recordMapper.toResponseList(
                recordRepository.findTop10ByIsDeletedFalseOrderByCreatedAtDesc()
        );

        return new DashboardSummaryResponse(
                totalIncome,
                totalExpenses,
                netBalance,
                totalRecords,
                categoryTotals,
                recentActivity
        );
    }

    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public List<MonthlyTrendResponse> getMonthlyTrends() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        return recordRepository.monthlyTotals()
                .stream()
                .map(row -> new MonthlyTrendResponse(
                        // DATE_TRUNC returns a java.sql.Date or LocalDate — format to "yyyy-MM"
                        LocalDate.parse(row[0].toString().substring(0, 10))
                                .format(formatter),
                        (RecordType) row[1],
                        (BigDecimal) row[2]
                ))
                .toList();
    }

    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public List<CategoryTotalResponse> getCategoryBreakdown(
            RecordType type,
            LocalDate startDate,
            LocalDate endDate) {

        return recordRepository
                .sumByCategoryFiltered(type, startDate, endDate)
                .stream()
                .map(row -> new CategoryTotalResponse(
                        (String) row[0],
                        (BigDecimal) row[1]
                ))
                .toList();
    }
}

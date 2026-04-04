package com.finance.dashboard.repository;

import com.finance.dashboard.entity.FinancialRecord;
import com.finance.dashboard.enums.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, UUID> {

    // Soft-delete aware: never return deleted records
    List<FinancialRecord> findAllByIsDeletedFalse();

    List<FinancialRecord> findTop10ByIsDeletedFalseOrderByCreatedAtDesc();

    // The main filterable query — all params are optional (nullable)
    @Query("""
    SELECT r FROM FinancialRecord r
    WHERE r.isDeleted = false
      AND (CAST(:type AS string) IS NULL OR r.type = :type)
      AND (CAST(:category AS string) IS NULL OR r.category = :category)
      AND (CAST(:startDate AS date) IS NULL OR r.recordDate >= :startDate)
      AND (CAST(:endDate AS date) IS NULL OR r.recordDate <= :endDate)
    """)
    Page<FinancialRecord> findWithFilters(
            @Param("type")      RecordType type,
            @Param("category")  String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate")   LocalDate endDate,
            Pageable pageable
    );

    // Analytics queries (used on Day 3)
    @Query("SELECT SUM(r.amount) FROM FinancialRecord r WHERE r.type = :type AND r.isDeleted = false")
    Optional<BigDecimal> sumByType(@Param("type") RecordType type);

    @Query("""
        SELECT r.category, SUM(r.amount)
        FROM FinancialRecord r
        WHERE r.isDeleted = false
        GROUP BY r.category
        ORDER BY SUM(r.amount) DESC
        """)
    List<Object[]> sumByCategory();

    @Query("""
        SELECT FUNCTION('DATE_TRUNC', 'month', r.recordDate), r.type, SUM(r.amount)
        FROM FinancialRecord r
        WHERE r.isDeleted = false
        GROUP BY FUNCTION('DATE_TRUNC', 'month', r.recordDate), r.type
        ORDER BY 1 DESC
        """)
    List<Object[]> monthlyTotals();

    @Query("""
    SELECT r.category, SUM(r.amount)
    FROM FinancialRecord r
    WHERE r.isDeleted = false
      AND (CAST(:type AS string) IS NULL OR r.type = :type)
      AND (CAST(:startDate AS date) IS NULL OR r.recordDate >= :startDate)
      AND (CAST(:endDate AS date) IS NULL OR r.recordDate <= :endDate)
    GROUP BY r.category
    ORDER BY SUM(r.amount) DESC
    """)
    List<Object[]> sumByCategoryFiltered(
            @Param("type")      RecordType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate")   LocalDate endDate
    );

    // Count of active records (for summary)
    @Query("SELECT COUNT(r) FROM FinancialRecord r WHERE r.isDeleted = false")
    long countActive();

}

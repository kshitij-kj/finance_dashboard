package com.finance.dashboard.entity;

import com.finance.dashboard.enums.RecordType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "financial_records")
@Getter
@Setter
@NoArgsConstructor
public class FinancialRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordType type;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private LocalDate recordDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}

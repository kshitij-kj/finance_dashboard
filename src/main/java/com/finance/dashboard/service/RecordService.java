package com.finance.dashboard.service;

import com.finance.dashboard.dto.request.RecordRequest;
import com.finance.dashboard.dto.response.RecordResponse;
import com.finance.dashboard.entity.FinancialRecord;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.enums.RecordType;
import com.finance.dashboard.mapper.RecordMapper;
import com.finance.dashboard.repository.FinancialRecordRepository;
import com.finance.dashboard.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;
    private final RecordMapper recordMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public RecordResponse create(RecordRequest request, UUID creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + creatorId));

        FinancialRecord record = new FinancialRecord();
        record.setAmount(request.amount());
        record.setType(request.type());
        record.setCategory(request.category().trim());
        record.setRecordDate(request.recordDate());
        record.setDescription(request.description());
        record.setCreatedBy(creator);

        return recordMapper.toResponse(recordRepository.save(record));
    }

    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    @Transactional(readOnly = true)
    public Page<RecordResponse> getAll(RecordType type,
                                       String category,
                                       LocalDate startDate,
                                       LocalDate endDate,
                                       int page,
                                       int size) {
        Pageable pageable = PageRequest.of(
                page, size, Sort.by(Sort.Direction.DESC, "recordDate")
        );
        return recordRepository
                .findWithFilters(type, category, startDate, endDate, pageable)
                .map(recordMapper::toResponse);
    }

    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    @Transactional(readOnly = true)
    public RecordResponse getById(UUID id) {
        return recordMapper.toResponse(findActiveRecord(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public RecordResponse update(UUID id, RecordRequest request) {
        FinancialRecord record = findActiveRecord(id);

        record.setAmount(request.amount());
        record.setType(request.type());
        record.setCategory(request.category().trim());
        record.setRecordDate(request.recordDate());
        record.setDescription(request.description());

        return recordMapper.toResponse(recordRepository.save(record));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void softDelete(UUID id) {
        FinancialRecord record = findActiveRecord(id);
        record.setDeleted(true);
        recordRepository.save(record);
        // Note: record stays in DB forever — soft delete only hides it
    }

    // Private helper — reused by getById, update, softDelete
    // Throws 404 if record doesn't exist OR is already soft-deleted
    private FinancialRecord findActiveRecord(UUID id) {
        return recordRepository.findById(id)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() ->
                        new EntityNotFoundException("Financial record not found: " + id));
    }
}

package com.finance.dashboard.controller;

import com.finance.dashboard.dto.request.RecordRequest;
import com.finance.dashboard.dto.response.RecordResponse;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.enums.RecordType;
import com.finance.dashboard.service.RecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @PostMapping
    public ResponseEntity<RecordResponse> create(
            @Valid @RequestBody RecordRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(recordService.create(request, currentUser.getId()));
    }

    @GetMapping
    public ResponseEntity<Page<RecordResponse>> getAll(
            @RequestParam(required = false) RecordType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                recordService.getAll(type, category, startDate, endDate, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecordResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(recordService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecordResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody RecordRequest request) {
        return ResponseEntity.ok(recordService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        recordService.softDelete(id);
        return ResponseEntity.noContent().build();  // 204 No Content — standard for deletes
    }
}

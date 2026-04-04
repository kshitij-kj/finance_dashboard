package com.finance.dashboard.mapper;

import com.finance.dashboard.dto.response.RecordResponse;
import com.finance.dashboard.entity.FinancialRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecordMapper {

    @Mapping(target = "createdByEmail",
            expression = "java(record.getCreatedBy().getEmail())")
    RecordResponse toResponse(FinancialRecord record);

    List<RecordResponse> toResponseList(List<FinancialRecord> records);
}

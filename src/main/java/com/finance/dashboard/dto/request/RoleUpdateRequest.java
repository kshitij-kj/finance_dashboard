package com.finance.dashboard.dto.request;

import com.finance.dashboard.enums.Role;
import jakarta.validation.constraints.NotNull;

public record RoleUpdateRequest(
        @NotNull(message = "Role is required")
        Role role
) {}

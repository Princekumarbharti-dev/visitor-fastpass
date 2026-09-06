package com.visitorfastpass.identity.api.dto;

import com.visitorfastpass.identity.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class EmployeeDtos {
    private EmployeeDtos() {}

    public record CreateEmployeeRequest(
            @NotBlank @Size(max = 50) String username,
            @NotBlank @Email @Size(max = 120) String email,
            @NotBlank @Size(min = 8, max = 72) String password,
            @NotNull Role role,
            @NotBlank @Size(max = 30) String employeeCode,
            @NotBlank @Size(max = 100) String fullName,
            @Size(max = 80) String department,
            @Size(max = 80) String designation,
            @Pattern(regexp = "^$|^[0-9+() -]{7,20}$", message = "Phone number format is invalid") String phone
    ) {}

    public record UpdateEmployeeRequest(
            @NotBlank @Size(max = 100) String fullName,
            @Size(max = 80) String department,
            @Size(max = 80) String designation,
            @Pattern(regexp = "^$|^[0-9+() -]{7,20}$", message = "Phone number format is invalid") String phone
    ) {}

    public record ChangeStatusRequest(@NotNull Boolean active) {}

    public record EmployeeResponse(
            Long id,
            Long userId,
            String employeeCode,
            String fullName,
            String email,
            String department,
            String designation,
            String phone,
            Role role,
            boolean active
    ) {}

    public record HostSummary(
            Long id,
            String fullName,
            String department,
            String designation
    ) {}
}

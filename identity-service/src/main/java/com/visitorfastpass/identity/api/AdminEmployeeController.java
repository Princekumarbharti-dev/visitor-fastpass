package com.visitorfastpass.identity.api;

import com.visitorfastpass.identity.api.dto.EmployeeDtos.ChangeStatusRequest;
import com.visitorfastpass.identity.api.dto.EmployeeDtos.CreateEmployeeRequest;
import com.visitorfastpass.identity.api.dto.EmployeeDtos.EmployeeResponse;
import com.visitorfastpass.identity.api.dto.EmployeeDtos.UpdateEmployeeRequest;
import com.visitorfastpass.identity.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/employees")
@Tag(name = "Admin Employees")
@SecurityRequirement(name = "bearerAuth")
public class AdminEmployeeController {
    private final EmployeeService employeeService;

    public AdminEmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('EMPLOYEE_READ')")
    @Operation(summary = "List all employees")
    public List<EmployeeResponse> findAll() {
        return employeeService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE_READ')")
    @Operation(summary = "Get an employee by ID")
    public EmployeeResponse findById(@PathVariable long id) {
        return employeeService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EMPLOYEE_CREATE')")
    @Operation(summary = "Create an employee login and profile")
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeResponse response = employeeService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/admin/employees/" + response.id())).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE')")
    @Operation(summary = "Update an employee profile")
    public EmployeeResponse update(@PathVariable long id,
                                   @Valid @RequestBody UpdateEmployeeRequest request) {
        return employeeService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('EMPLOYEE_DISABLE')")
    @Operation(summary = "Activate or deactivate an employee")
    public EmployeeResponse changeStatus(@PathVariable long id,
                                         @Valid @RequestBody ChangeStatusRequest request) {
        return employeeService.changeStatus(id, request);
    }
}

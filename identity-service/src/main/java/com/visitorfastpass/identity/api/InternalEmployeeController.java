package com.visitorfastpass.identity.api;

import com.visitorfastpass.identity.api.dto.EmployeeDtos.HostSummary;
import com.visitorfastpass.identity.service.EmployeeService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal/hosts")
@Hidden
public class InternalEmployeeController {
    private final EmployeeService employeeService;

    public InternalEmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('INTERNAL_SERVICE')")
    public HostSummary findActiveHost(@PathVariable long id) {
        return employeeService.findActiveHost(id);
    }
}

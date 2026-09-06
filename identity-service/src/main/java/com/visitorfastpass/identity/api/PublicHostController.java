package com.visitorfastpass.identity.api;

import com.visitorfastpass.identity.api.dto.EmployeeDtos.HostSummary;
import com.visitorfastpass.identity.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/hosts")
@Tag(name = "Public Hosts")
public class PublicHostController {
    private final EmployeeService employeeService;

    public PublicHostController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    @Operation(summary = "List active hosts available for visitor registration")
    public List<HostSummary> activeHosts() {
        return employeeService.activeHosts();
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get an active host for visitor registration validation")
    public HostSummary activeHost(@PathVariable long id) {
        return employeeService.findActiveHost(id);
    }
}

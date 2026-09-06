package com.visitorfastpass.identity.service;

import com.visitorfastpass.identity.api.dto.EmployeeDtos.ChangeStatusRequest;
import com.visitorfastpass.identity.api.dto.EmployeeDtos.CreateEmployeeRequest;
import com.visitorfastpass.identity.api.dto.EmployeeDtos.EmployeeResponse;
import com.visitorfastpass.identity.api.dto.EmployeeDtos.HostSummary;
import com.visitorfastpass.identity.api.dto.EmployeeDtos.UpdateEmployeeRequest;
import com.visitorfastpass.identity.domain.Employee;
import com.visitorfastpass.identity.domain.Role;
import com.visitorfastpass.identity.domain.User;
import com.visitorfastpass.identity.exception.ApiException;
import com.visitorfastpass.identity.repository.EmployeeRepository;
import com.visitorfastpass.identity.repository.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository, UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<HostSummary> activeHosts() {
        return employeeRepository.findByActiveTrueAndUserRoleOrderByFullNameAsc(Role.HOST).stream()
                .map(employee -> new HostSummary(employee.getId(), employee.getFullName(),
                        employee.getDepartment(), employee.getDesignation()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> findAll() {
        return employeeRepository.findAllByOrderByFullNameAsc().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse findById(long id) {
        return toResponse(requiredEmployee(id));
    }

    @Transactional(readOnly = true)
    public HostSummary findActiveHost(long id) {
        Employee employee = requiredEmployee(id);
        if (!employee.isActive() || employee.getUser().getRole() != Role.HOST) {
            throw new ApiException(HttpStatus.NOT_FOUND, "HOST_NOT_FOUND", "Active host was not found");
        }
        return new HostSummary(employee.getId(), employee.getFullName(),
                employee.getDepartment(), employee.getDesignation());
    }

    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest request) {
        if (userRepository.existsByUsernameIgnoreCase(request.username().trim())) {
            throw conflict("USERNAME_EXISTS", "Username is already in use");
        }
        if (userRepository.existsByEmailIgnoreCase(request.email().trim())) {
            throw conflict("EMAIL_EXISTS", "Email is already in use");
        }
        if (employeeRepository.existsByEmployeeCodeIgnoreCase(request.employeeCode().trim())) {
            throw conflict("EMPLOYEE_CODE_EXISTS", "Employee code is already in use");
        }

        User user = userRepository.save(new User(
                request.username().trim(), request.email().trim().toLowerCase(),
                passwordEncoder.encode(request.password()), request.role()));
        Employee employee = employeeRepository.save(new Employee(
                user, request.employeeCode().trim(), request.fullName().trim(),
                clean(request.department()), clean(request.designation()), clean(request.phone())));
        return toResponse(employee);
    }

    @Transactional
    public EmployeeResponse update(long id, UpdateEmployeeRequest request) {
        Employee employee = requiredEmployee(id);
        employee.updateProfile(request.fullName().trim(), clean(request.department()),
                clean(request.designation()), clean(request.phone()));
        return toResponse(employee);
    }

    @Transactional
    public EmployeeResponse changeStatus(long id, ChangeStatusRequest request) {
        Employee employee = requiredEmployee(id);
        employee.changeActive(request.active());
        return toResponse(employee);
    }

    private Employee requiredEmployee(long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "EMPLOYEE_NOT_FOUND", "Employee was not found"));
    }

    private EmployeeResponse toResponse(Employee employee) {
        User user = employee.getUser();
        return new EmployeeResponse(employee.getId(), user.getId(), employee.getEmployeeCode(),
                employee.getFullName(), user.getEmail(), employee.getDepartment(),
                employee.getDesignation(), employee.getPhone(), user.getRole(), employee.isActive());
    }

    private ApiException conflict(String code, String message) {
        return new ApiException(HttpStatus.CONFLICT, code, message);
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

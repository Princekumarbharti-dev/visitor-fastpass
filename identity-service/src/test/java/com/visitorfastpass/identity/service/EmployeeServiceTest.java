package com.visitorfastpass.identity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.visitorfastpass.identity.api.dto.EmployeeDtos.CreateEmployeeRequest;
import com.visitorfastpass.identity.domain.Employee;
import com.visitorfastpass.identity.domain.Role;
import com.visitorfastpass.identity.domain.User;
import com.visitorfastpass.identity.exception.ApiException;
import com.visitorfastpass.identity.repository.EmployeeRepository;
import com.visitorfastpass.identity.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @Mock EmployeeRepository employeeRepository;
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;

    private EmployeeService service;

    @BeforeEach
    void setUp() {
        service = new EmployeeService(employeeRepository, userRepository, passwordEncoder);
    }

    @Test
    void createsEmployeeWithEncodedPassword() {
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "asha", "asha@example.com", "Password@1", Role.HOST, "EMP-12",
                "Asha Sharma", "Engineering", "Manager", "+91 9876543210");
        when(passwordEncoder.encode("Password@1")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.create(request);

        assertThat(response.fullName()).isEqualTo("Asha Sharma");
        assertThat(response.role()).isEqualTo(Role.HOST);
        verify(passwordEncoder).encode("Password@1");
    }

    @Test
    void rejectsDuplicateUsername() {
        when(userRepository.existsByUsernameIgnoreCase("asha")).thenReturn(true);
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "asha", "asha@example.com", "Password@1", Role.HOST, "EMP-12",
                "Asha Sharma", null, null, null);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(ApiException.class)
                .hasMessage("Username is already in use");
    }

    @Test
    void rejectsInactiveHostLookup() {
        User user = new User("asha", "asha@example.com", "encoded", Role.HOST);
        Employee employee = new Employee(user, "EMP-12", "Asha Sharma", null, null, null);
        employee.changeActive(false);
        when(employeeRepository.findById(12L)).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> service.findActiveHost(12L))
                .isInstanceOf(ApiException.class)
                .hasMessage("Active host was not found");
    }
}

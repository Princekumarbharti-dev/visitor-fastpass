package com.visitorfastpass.identity.service;

import com.visitorfastpass.identity.api.dto.AuthDtos.LoginRequest;
import com.visitorfastpass.identity.api.dto.AuthDtos.LoginResponse;
import com.visitorfastpass.identity.domain.Employee;
import com.visitorfastpass.identity.domain.User;
import com.visitorfastpass.identity.exception.ApiException;
import com.visitorfastpass.identity.repository.EmployeeRepository;
import com.visitorfastpass.identity.repository.UserRepository;
import com.visitorfastpass.identity.security.JwtProperties;
import com.visitorfastpass.identity.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public AuthenticationService(UserRepository userRepository, EmployeeRepository employeeRepository,
                                 PasswordEncoder passwordEncoder, JwtService jwtService,
                                 JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsernameIgnoreCase(request.username().trim())
                .orElseThrow(this::invalidCredentials);

        if (!user.isActive() || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw invalidCredentials();
        }

        Employee employee = employeeRepository.findByUserId(user.getId()).orElse(null);
        String token = jwtService.createAccessToken(user, employee);
        return new LoginResponse(token, "Bearer", jwtProperties.accessTokenMinutes() * 60,
                user.getRole().name(), employee == null ? null : employee.getId(),
                employee == null ? user.getUsername() : employee.getFullName());
    }

    private ApiException invalidCredentials() {
        return new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid username or password");
    }
}

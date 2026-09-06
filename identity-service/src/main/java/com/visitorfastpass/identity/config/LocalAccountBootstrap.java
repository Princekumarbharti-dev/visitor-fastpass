package com.visitorfastpass.identity.config;

import com.visitorfastpass.identity.config.BootstrapProperties.Account;
import com.visitorfastpass.identity.domain.Employee;
import com.visitorfastpass.identity.domain.Role;
import com.visitorfastpass.identity.domain.User;
import com.visitorfastpass.identity.repository.EmployeeRepository;
import com.visitorfastpass.identity.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@EnableConfigurationProperties(BootstrapProperties.class)
public class LocalAccountBootstrap implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(LocalAccountBootstrap.class);

    private final BootstrapProperties properties;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public LocalAccountBootstrap(BootstrapProperties properties, UserRepository userRepository,
                                 EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.properties = properties;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!properties.enabled()) return;
        createIfMissing(properties.admin(), Role.ADMIN);
        createIfMissing(properties.host(), Role.HOST);
        createIfMissing(properties.reception(), Role.RECEPTION);
        log.info("Local bootstrap accounts are ready; disable with APP_BOOTSTRAP_ENABLED=false outside development");
    }

    private void createIfMissing(Account account, Role role) {
        if (userRepository.existsByUsernameIgnoreCase(account.username())) return;
        User user = userRepository.save(new User(account.username(), account.email(),
                passwordEncoder.encode(account.password()), role));
        employeeRepository.save(new Employee(user, account.employeeCode(), account.fullName(),
                account.department(), account.designation(), null));
    }
}

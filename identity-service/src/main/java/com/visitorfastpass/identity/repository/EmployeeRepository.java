package com.visitorfastpass.identity.repository;

import com.visitorfastpass.identity.domain.Employee;
import com.visitorfastpass.identity.domain.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Override
    @EntityGraph(attributePaths = "user")
    Optional<Employee> findById(Long id);

    @EntityGraph(attributePaths = "user")
    Optional<Employee> findByUserId(Long userId);

    @EntityGraph(attributePaths = "user")
    List<Employee> findAllByOrderByFullNameAsc();

    @EntityGraph(attributePaths = "user")
    List<Employee> findByActiveTrueAndUserRoleOrderByFullNameAsc(Role role);

    boolean existsByEmployeeCodeIgnoreCase(String employeeCode);
}

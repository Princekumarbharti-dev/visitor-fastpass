package com.visitorfastpass.visitor.repository;

import com.visitorfastpass.visitor.domain.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {
}

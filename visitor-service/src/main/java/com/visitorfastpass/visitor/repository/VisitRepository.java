package com.visitorfastpass.visitor.repository;

import com.visitorfastpass.visitor.domain.Visit;
import com.visitorfastpass.visitor.domain.VisitStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VisitRepository extends JpaRepository<Visit, Long>, JpaSpecificationExecutor<Visit> {
    @EntityGraph(attributePaths = "visitor")
    Optional<Visit> findByPublicReference(String publicReference);

    @Override
    @EntityGraph(attributePaths = "visitor")
    Optional<Visit> findById(Long id);

    @EntityGraph(attributePaths = "visitor")
    List<Visit> findByHostIdAndStatusOrderByScheduledAtAsc(Long hostId, VisitStatus status);

    @EntityGraph(attributePaths = "visitor")
    List<Visit> findByHostIdAndStatusNotOrderByScheduledAtDesc(Long hostId, VisitStatus status);

    long countByCreatedAtBetween(Instant start, Instant end);
    long countByStatus(VisitStatus status);
    boolean existsByPublicReference(String publicReference);
}

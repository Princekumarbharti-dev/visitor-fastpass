package com.visitorfastpass.pass.repository;

import com.visitorfastpass.pass.domain.VisitorPass;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitorPassRepository extends JpaRepository<VisitorPass, Long> {
    Optional<VisitorPass> findByVisitId(Long visitId);
    Optional<VisitorPass> findByPassNumber(String passNumber);
    Optional<VisitorPass> findByQrTokenHash(String qrTokenHash);
    boolean existsByPassNumber(String passNumber);
}

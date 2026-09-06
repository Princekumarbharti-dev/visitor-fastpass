package com.visitorfastpass.visitor.repository;

import com.visitorfastpass.visitor.domain.VisitEvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitEventRepository extends JpaRepository<VisitEvent, Long> {
    List<VisitEvent> findByVisitIdOrderByEventTimeAsc(Long visitId);
}

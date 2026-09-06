package com.visitorfastpass.pass.repository;

import com.visitorfastpass.pass.domain.PassScan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassScanRepository extends JpaRepository<PassScan, Long> {
}

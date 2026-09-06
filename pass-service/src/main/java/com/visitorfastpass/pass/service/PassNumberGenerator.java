package com.visitorfastpass.pass.service;

import com.visitorfastpass.pass.repository.VisitorPassRepository;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class PassNumberGenerator {
    private static final char[] ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private final SecureRandom random = new SecureRandom();
    private final VisitorPassRepository repository;

    public PassNumberGenerator(VisitorPassRepository repository) { this.repository = repository; }

    public String next() {
        for (int attempt = 0; attempt < 5; attempt++) {
            StringBuilder suffix = new StringBuilder();
            for (int i = 0; i < 6; i++) suffix.append(ALPHABET[random.nextInt(ALPHABET.length)]);
            String number = "FP-" + LocalDate.now(ZoneOffset.UTC).format(DateTimeFormatter.BASIC_ISO_DATE) + "-" + suffix;
            if (!repository.existsByPassNumber(number)) return number;
        }
        throw new IllegalStateException("Unable to generate a unique pass number");
    }
}

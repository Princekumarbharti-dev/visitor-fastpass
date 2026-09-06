package com.visitorfastpass.visitor.service;

import com.visitorfastpass.visitor.repository.VisitRepository;
import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class PublicReferenceGenerator {
    private static final char[] ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private final SecureRandom random = new SecureRandom();
    private final VisitRepository visitRepository;

    public PublicReferenceGenerator(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    public String next() {
        for (int attempt = 0; attempt < 5; attempt++) {
            StringBuilder value = new StringBuilder("VF-");
            for (int i = 0; i < 9; i++) value.append(ALPHABET[random.nextInt(ALPHABET.length)]);
            String reference = value.toString();
            if (!visitRepository.existsByPublicReference(reference)) return reference;
        }
        throw new IllegalStateException("Unable to generate a unique visit reference");
    }
}

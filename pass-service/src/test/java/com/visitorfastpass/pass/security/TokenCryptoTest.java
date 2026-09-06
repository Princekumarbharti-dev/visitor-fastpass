package com.visitorfastpass.pass.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.visitorfastpass.pass.config.PassProperties;
import org.junit.jupiter.api.Test;

class TokenCryptoTest {
    private final TokenCrypto crypto = new TokenCrypto(new PassProperties(
            "test-pass-encryption-secret-at-least-32-characters", "http://localhost:5173", 120, 480));

    @Test
    void tokenIsHighEntropyAndEncryptionRoundTrips() {
        String token = crypto.newToken();
        String encrypted = crypto.encrypt(token);

        assertThat(token).hasSizeGreaterThanOrEqualTo(40);
        assertThat(encrypted).doesNotContain(token);
        assertThat(crypto.decrypt(encrypted)).isEqualTo(token);
        assertThat(crypto.hash(token)).hasSize(64);
    }

    @Test
    void generatedTokensAreUnique() {
        assertThat(crypto.newToken()).isNotEqualTo(crypto.newToken());
    }
}

package com.visitorfastpass.visitor.service;

import com.visitorfastpass.visitor.client.PassClient;
import com.visitorfastpass.visitor.client.PassClient.GeneratePassRequest;
import com.visitorfastpass.visitor.client.PassClient.GeneratePassResponse;
import com.visitorfastpass.visitor.client.PassClient.VerifyPassResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

@Service
public class PassProvisioningGateway {
    private final PassClient passClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;
    private final String internalApiKey;

    public PassProvisioningGateway(PassClient passClient,
                                   CircuitBreakerFactory<?, ?> circuitBreakerFactory,
                                   @Value("${app.internal-api-key}") String internalApiKey) {
        this.passClient = passClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.internalApiKey = internalApiKey;
    }

    public GeneratePassResponse generate(GeneratePassRequest request) {
        return circuitBreakerFactory.create("passService").run(
                () -> passClient.generate(internalApiKey, request),
                throwable -> { throw new PassProvisioningException("Pass Service is unavailable", throwable); });
    }

    public VerifyPassResponse verify(String token, String location) {
        return circuitBreakerFactory.create("passService").run(
                () -> passClient.verify(internalApiKey, new PassClient.VerifyPassRequest(token, location)),
                throwable -> { throw new PassProvisioningException("Pass verification is unavailable", throwable); });
    }

    public void markUsed(long visitId, String token) {
        circuitBreakerFactory.create("passService").run(
                () -> {
                    passClient.markUsed(internalApiKey, visitId, new PassClient.UsePassRequest(token));
                    return null;
                }, throwable -> { throw new PassProvisioningException("Pass consumption is unavailable", throwable); });
    }

    public static class PassProvisioningException extends RuntimeException {
        public PassProvisioningException(String message, Throwable cause) { super(message, cause); }
    }
}

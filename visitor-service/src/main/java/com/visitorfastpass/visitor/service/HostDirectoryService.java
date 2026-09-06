package com.visitorfastpass.visitor.service;

import com.visitorfastpass.visitor.client.IdentityClient;
import com.visitorfastpass.visitor.client.IdentityClient.HostResponse;
import com.visitorfastpass.visitor.exception.ApiException;
import feign.FeignException;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class HostDirectoryService {
    private final IdentityClient identityClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public HostDirectoryService(IdentityClient identityClient,
                                CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.identityClient = identityClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    public HostResponse requireActiveHost(long hostId) {
        return circuitBreakerFactory.create("identityService").run(
                () -> identityClient.findActiveHost(hostId),
                throwable -> hostLookupFallback(throwable));
    }

    private HostResponse hostLookupFallback(Throwable throwable) {
        if (throwable instanceof FeignException.NotFound) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "HOST_NOT_FOUND",
                    "The selected host does not exist or is inactive");
        }
        throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "HOST_SERVICE_UNAVAILABLE",
                "Host validation is temporarily unavailable; please try again");
    }
}

package com.visitorfastpass.visitor.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "identity-service", path = "/api/v1/public/hosts")
public interface IdentityClient {
    @GetMapping("/{id}")
    HostResponse findActiveHost(@PathVariable("id") long id);

    record HostResponse(Long id, String fullName, String department, String designation) {}
}

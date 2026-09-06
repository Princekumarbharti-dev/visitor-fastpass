package com.visitorfastpass.visitor.service;

import com.visitorfastpass.visitor.api.dto.VisitorRegistrationRequest;
import com.visitorfastpass.visitor.api.dto.VisitorRegistrationResponse;
import com.visitorfastpass.visitor.client.IdentityClient.HostResponse;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private final HostDirectoryService hostDirectoryService;
    private final VisitCommandService commandService;

    public RegistrationService(HostDirectoryService hostDirectoryService, VisitCommandService commandService) {
        this.hostDirectoryService = hostDirectoryService;
        this.commandService = commandService;
    }

    public VisitorRegistrationResponse register(VisitorRegistrationRequest request) {
        HostResponse host = hostDirectoryService.requireActiveHost(request.hostId());
        return commandService.register(request, host);
    }
}

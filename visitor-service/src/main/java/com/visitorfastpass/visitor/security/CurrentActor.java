package com.visitorfastpass.visitor.security;

import com.visitorfastpass.visitor.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class CurrentActor {
    public Actor get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken token)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_REQUIRED",
                    "A valid bearer token is required");
        }
        Long userId = Long.valueOf(token.getToken().getSubject());
        Long employeeId = token.getToken().getClaim("employeeId");
        String role = token.getToken().getClaimAsString("role");
        String displayName = token.getToken().getClaimAsString("displayName");
        return new Actor(userId, employeeId, role, displayName == null ? token.getName() : displayName);
    }

    public record Actor(Long userId, Long employeeId, String role, String displayName) {
        public boolean isAdmin() { return "ADMIN".equals(role); }
    }
}

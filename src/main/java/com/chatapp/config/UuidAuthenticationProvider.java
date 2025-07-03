package com.chatapp.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class UuidAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UUID sessionId = (UUID) authentication.getCredentials();
        // Wrap in UserDetails or use directly
        return new UuidAuthenticationToken(sessionId, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UuidAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

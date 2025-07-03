package com.chatapp.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

public class UuidAuthenticationToken extends AbstractAuthenticationToken {

    private final UUID uuid;

    public UuidAuthenticationToken(UUID uuid) {
        super(null);
        this.uuid = uuid;
        setAuthenticated(false);
    }

    public UuidAuthenticationToken(UUID uuid, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.uuid = uuid;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return uuid;
    }

    @Override
    public Object getPrincipal() {
        return uuid;
    }
}

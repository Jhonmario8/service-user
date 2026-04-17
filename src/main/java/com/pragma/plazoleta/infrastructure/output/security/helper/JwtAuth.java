package com.pragma.plazoleta.infrastructure.output.security.helper;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

@AllArgsConstructor
public class JwtAuth implements Authentication {

    private final Map<String, Object> claims;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return claims;
    }

    @Override
    public Object getPrincipal() {
        return claims.get("sub");
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new UnsupportedOperationException("No se puede cambiar el estado de autenticación");
    }

    @Override
    public String getName() {
        return (String) claims.get("sub");
    }
}

package com.travel.journal.security;

import com.travel.journal.dto.UserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.Map;

public class CustomOidcUser implements OidcUser {
    private final OidcUser oidcUser;
    private final UserDto userDto;
    public CustomOidcUser(OidcUser oidcUser, UserDto userDto) {
        this.oidcUser = oidcUser;
        this.userDto = userDto;
    }

    @Override
    public Map<String, Object> getClaims() {
        return oidcUser.getClaims();
    }

    @Override
    public OidcIdToken getIdToken() {
        return oidcUser.getIdToken();
    }
    @Override
    public OidcUserInfo getUserInfo() {
        return oidcUser.getUserInfo();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oidcUser.getAuthorities();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oidcUser.getAttributes();
    }

    @Override
    public String getName() {
        return oidcUser.getName();
    }

    public UserDto getUserDto() {return userDto;}
}

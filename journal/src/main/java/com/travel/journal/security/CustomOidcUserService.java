package com.travel.journal.security;

import com.travel.journal.dto.UserDto;
import com.travel.journal.entity.UserEntity;
import com.travel.journal.repo.UserRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CustomOidcUserService extends OidcUserService {
    private final UserRepository userRepository;
    public CustomOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        String email = oidcUser.getEmail();
        String givenName = oidcUser.getAttribute("given_name");

        UserEntity user = userRepository.findByEmail(email).orElseGet(
                () -> {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setEmail(email);
                    userEntity.setDisplayName(givenName);
                    userEntity.setJoinedAt(LocalDateTime.now());
                    return userRepository.save(userEntity);
                });
        UserDto userDto =  new UserDto(user);
        return new CustomOidcUser(oidcUser, userDto);
    }
    public UserDto loadUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if(user != null) {
            return new UserDto(user);
        }else{
            return null;
        }
    }
}

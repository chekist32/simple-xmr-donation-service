package com.sokol.simplemonerodonationservice.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;
import java.util.stream.Collectors;

public class UserUtils {
    public static UserDetails UserEntityToUserDetailsMapper(UserEntity user) {
        return new User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isAccountNonLocked(),
                user.getAuthorities()
        );
    }
    public static Set<GrantedAuthority> RoleSetToGrantedAuthoritySetMapper(Set<Role> roles) {
        return roles
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
    }

    public static UserDataResponseDTO UserEntityToUserDataResponseDTOMapper(UserEntity user) {
        return new UserDataResponseDTO(
                user.getUsername(),
                user.getEmail()
        );
    }
}

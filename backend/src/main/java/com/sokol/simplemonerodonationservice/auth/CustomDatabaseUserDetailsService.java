package com.sokol.simplemonerodonationservice.auth;

import com.sokol.simplemonerodonationservice.user.UserEntity;
import com.sokol.simplemonerodonationservice.user.UserRepository;
import com.sokol.simplemonerodonationservice.user.UserUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomDatabaseUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomDatabaseUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String principal) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByPrincipal(principal)
                .orElseThrow(() -> new UsernameNotFoundException("There is no user with such username/email: " + principal));

        return UserUtils.UserEntityToUserDetailsMapper(user);
    }
}

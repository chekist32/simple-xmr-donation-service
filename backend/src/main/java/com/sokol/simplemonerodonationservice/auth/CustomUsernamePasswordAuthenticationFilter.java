package com.sokol.simplemonerodonationservice.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sokol.simplemonerodonationservice.auth.dto.AuthRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.io.IOException;
import java.io.InputStream;

public class CustomUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final String DEFAULT_AUTH_REQUEST_MATCHER = "/api/auth/login";

    public CustomUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_AUTH_REQUEST_MATCHER, authenticationManager);
        this.setSecurityContextRepository(new HttpSessionSecurityContextRepository());
        this.setAuthenticationFailureHandler(
                new SimpleUrlAuthenticationFailureHandler("/api/auth/status/failure")
        );
        this.setAuthenticationSuccessHandler(
                new SimpleUrlAuthenticationSuccessHandler("/api/auth/status/success")
        );
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res)
            throws AuthenticationException, IOException {

            InputStream bodyInputStream = req.getInputStream();

            AuthRequestDTO loginData = new ObjectMapper()
                    .readValue(bodyInputStream, AuthRequestDTO.class);

            bodyInputStream.close();

            if (loginData.principal() == null || loginData.principal().trim().isEmpty() ||
                loginData.password() == null || loginData.password().trim().isEmpty())
                throw new BadCredentialsException("Bad credentials");

        return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginData.principal().trim(),
                        loginData.password().trim()
                )
        );
    }


}

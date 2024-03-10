package com.sokol.simplemonerodonationservice.auth;

import com.sokol.simplemonerodonationservice.auth.registration.ConfirmationTokenEntity;
import com.sokol.simplemonerodonationservice.auth.registration.RegistrationRequestDTO;
import com.sokol.simplemonerodonationservice.base.exception.BadRequestException;
import com.sokol.simplemonerodonationservice.base.http.HttpError;
import com.sokol.simplemonerodonationservice.email.EmailService;
import com.sokol.simplemonerodonationservice.user.UserEntity;
import com.sokol.simplemonerodonationservice.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final static String UIUrl = System.getenv("ADMIN_PANEL_UI_URL");
    private final UserService userService;
    private final EmailService emailService;

    public AuthController(UserService userService,
                          EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegistrationRequestDTO registrationDTO) {
        ConfirmationTokenEntity confirmationToken = userService.registerUser(registrationDTO);

        String link = UIUrl + "/confirmation?token=" + confirmationToken.getToken();

        emailService.sendEmail(registrationDTO.email(), "Confirmation", link);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO, Principal principal) {
        if (changePasswordRequestDTO.newPassword().compareTo(changePasswordRequestDTO.repeatNewPassword()) != 0)
            throw new BadRequestException("Passwords must match");

        userService.changeUserPassword(principal.getName(), changePasswordRequestDTO);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO) {
        ConfirmationTokenEntity confirmationToken = userService.resetUserPassword(resetPasswordRequestDTO.email());

        String link = UIUrl + "/resetPassword?token=" + confirmationToken.getToken();
        emailService.sendEmail(confirmationToken.getUser().getEmail(), "Reset password", link);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @Valid @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO) {
        if (changePasswordRequestDTO.newPassword().compareTo(changePasswordRequestDTO.repeatNewPassword()) != 0)
            throw new BadRequestException("Passwords must match");

        userService.resetUserPassword(token, changePasswordRequestDTO.newPassword());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/changeEmail")
    public ResponseEntity<String> changeEmail(@Valid @RequestBody ChangeEmailRequestDTO changeEmailRequestDTO, Principal principal) {
        if (changeEmailRequestDTO.newEmail().compareTo(changeEmailRequestDTO.repeatNewEmail()) != 0)
            throw new BadRequestException("Email must match");

        ConfirmationTokenEntity confirmationToken = userService.changeEmail(principal.getName(), changeEmailRequestDTO);

        String link = UIUrl + "/changeEmail?token=" + confirmationToken.getToken();
        emailService.sendEmail(confirmationToken.getUser().getEmail(), "Change email", link);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/changeEmail")
    public ResponseEntity<String> changeEmail(@RequestParam("token") String token) {
        userService.changeEmail(token);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/register/confirmation")
    public ResponseEntity<String> confirmEmail(@RequestParam("token") String token, HttpServletRequest request) {
        UserEntity activatedUser = userService.activateUser(token);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        activatedUser.getUsername(),
                        null,
                        activatedUser.getAuthorities()
                )
        );
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/status/success")
    public ResponseEntity<String> successfulAuthentication() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/status/failure")
    public ResponseEntity<Map<String, Object>> failedAuthentication() {
        Map<String, Object> body = HttpError.createDefaultErrorResponseBody("Bad credentials", "/api/auth/login", HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }
}

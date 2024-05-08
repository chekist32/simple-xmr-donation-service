package com.sokol.simplemonerodonationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sokol.simplemonerodonationservice.auth.AuthController;
import com.sokol.simplemonerodonationservice.auth.ChangeEmailRequestDTO;
import com.sokol.simplemonerodonationservice.auth.ChangePasswordRequestDTO;
import com.sokol.simplemonerodonationservice.auth.ResetPasswordRequestDTO;
import com.sokol.simplemonerodonationservice.auth.registration.ConfirmationTokenEntity;
import com.sokol.simplemonerodonationservice.auth.registration.RegistrationRequestDTO;
import com.sokol.simplemonerodonationservice.email.EmailService;
import com.sokol.simplemonerodonationservice.user.UserEntity;
import com.sokol.simplemonerodonationservice.user.UserService;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(value = AuthController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AuthControllerTest {
    private final static String moreThan64Chars = RandomString.make(65);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private EmailService emailService;

    @Test
    public void registerUserPOST_ShouldReturnValidationError() throws Exception {

        // Empty fields
        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegistrationRequestDTO("", "", "")))
        ).andExpect(status().isBadRequest());

        // More than 64 chars
        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegistrationRequestDTO(moreThan64Chars, moreThan64Chars, moreThan64Chars)))
        ).andExpect(status().isBadRequest());

        // Bad email format
        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegistrationRequestDTO("a", "test@email.", "c")))
        ).andExpect(status().isBadRequest());
    }
    @Test
    public void registerUserPOST_ShouldReturnStatusCreated() throws Exception {
        RegistrationRequestDTO registrationRequestDTO = new RegistrationRequestDTO("username", "test@email.com", "pass");
        ConfirmationTokenEntity confirmationToken = new ConfirmationTokenEntity(new UserEntity("", "", ""));
        when(userService.registerUser(registrationRequestDTO))
                .thenReturn(confirmationToken);

        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO))
        ).andExpect(status().isCreated());
    }

    @Test
    public void changePasswordPOST_ShouldReturnValidationError() throws Exception {
        mockMvc.perform(
                post("/api/auth/changePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChangePasswordRequestDTO("", "")))
        ).andExpect(status().isBadRequest());

        mockMvc.perform(
                post("/api/auth/changePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChangePasswordRequestDTO(moreThan64Chars, moreThan64Chars)))
        ).andExpect(status().isBadRequest());

        mockMvc.perform(
                post("/api/auth/changePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChangePasswordRequestDTO("matched", "unmatched")))
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void changePasswordPOST_ShouldReturnStatusOk() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");

        ChangePasswordRequestDTO changePasswordRequestDTO = new ChangePasswordRequestDTO("matched", "matched");
        when(userService.changeUserPassword(anyString(), any(changePasswordRequestDTO.getClass())))
                .thenReturn(new UserEntity("", "",  ""));

        mockMvc.perform(
                post("/api/auth/changePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .content(objectMapper.writeValueAsString(changePasswordRequestDTO))
        ).andExpect(status().isOk());
    }

    @Test
    public void resetPasswordPOST_ShouldReturnValidationError() throws Exception {
        mockMvc.perform(
                post("/api/auth/resetPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ResetPasswordRequestDTO("")))
        ).andExpect(status().isBadRequest());
        mockMvc.perform(
                post("/api/auth/resetPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ResetPasswordRequestDTO("just\"not\"right@example.com")))
        ).andExpect(status().isBadRequest());
    }
    @Test
    public void resetPasswordPOST_ShouldReturnStatusOk() throws Exception {
        ResetPasswordRequestDTO resetPasswordRequestDTO = new ResetPasswordRequestDTO( "test@email.com");
        ConfirmationTokenEntity confirmationToken = new ConfirmationTokenEntity(new UserEntity("", "", ""));
        when(userService.resetUserPassword(resetPasswordRequestDTO.email()))
                .thenReturn(confirmationToken);

        mockMvc.perform(
                post("/api/auth/resetPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetPasswordRequestDTO))
        ).andExpect(status().isOk());
    }

    @Test
    public void resetPasswordPUT_ShouldReturnValidationError() throws Exception {
        mockMvc.perform(
                put("/api/auth/resetPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChangePasswordRequestDTO("", "")))
        ).andExpect(status().isBadRequest());

        mockMvc.perform(
                put("/api/auth/resetPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChangePasswordRequestDTO(moreThan64Chars, moreThan64Chars)))
        ).andExpect(status().isBadRequest());

        mockMvc.perform(
                put("/api/auth/resetPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChangePasswordRequestDTO("matched", "unmatched")))
        ).andExpect(status().isBadRequest());

        mockMvc.perform(
                put("/api/auth/resetPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChangePasswordRequestDTO("matched", "matched")))
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void resetPasswordPUT_ShouldReturnStatusOk() throws Exception {
        ChangePasswordRequestDTO changePasswordRequestDTO = new ChangePasswordRequestDTO("matched", "matched");
        when(userService.resetUserPassword(anyString(), anyString()))
                .thenReturn(new UserEntity("", "", ""));


        mockMvc.perform(
                put("/api/auth/resetPassword")
                        .queryParam("token", "test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequestDTO))
        ).andExpect(status().isOk());

    }

    @Test
    public void changeEmailPOST_ShouldReturnValidationError() throws Exception {
        mockMvc.perform(
                post("/api/auth/changeEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChangeEmailRequestDTO("", "")))
        ).andExpect(status().isBadRequest());

        mockMvc.perform(
                post("/api/auth/changeEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChangeEmailRequestDTO("test@email.", "test@email.com")))
        ).andExpect(status().isBadRequest());

        mockMvc.perform(
                post("/api/auth/changeEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChangeEmailRequestDTO("test@email.com", "test1@email.com")))
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void changeEmailPOST_ShouldReturnStatusOk() throws Exception {
        ChangeEmailRequestDTO changeEmailRequestDTO = new ChangeEmailRequestDTO("test@email.com", "test@email.com");
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");

        when(userService.changeEmail(principal.getName(), changeEmailRequestDTO))
                .thenReturn(new ConfirmationTokenEntity(new UserEntity("","","")));

        mockMvc.perform(
                post("/api/auth/changeEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .content(objectMapper.writeValueAsString(changeEmailRequestDTO))
        ).andExpect(status().isOk());
    }

    @Test
    public void changeEmailGET_ShouldReturnValidationError() throws Exception {
        mockMvc.perform(
                get("/api/auth/changeEmail")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void changeEmailGET_ShouldReturnStatusOk() throws Exception {
        when(userService.changeEmail(anyString()))
                .thenReturn(new UserEntity("","",""));

        mockMvc.perform(
                get("/api/auth/changeEmail")
                        .queryParam("token", "token")
        ).andExpect(status().isOk());
    }

    @Test
    public void confirmEmailGET_ShouldReturnValidationError() throws Exception {
        mockMvc.perform(
                get("/api/auth/register/confirmation")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void confirmEmailGET_ShouldReturnStatusOk() throws Exception {
        when(userService.activateUser(anyString()))
                .thenReturn(new UserEntity("", "", ""));

        mockMvc.perform(
                get("/api/auth/register/confirmation")
                        .queryParam("token", "token")
        ).andExpect(status().isOk());
    }

}

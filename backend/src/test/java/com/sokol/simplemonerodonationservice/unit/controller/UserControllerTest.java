package com.sokol.simplemonerodonationservice.unit.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sokol.simplemonerodonationservice.base.exception.ResourceNotFoundException;
import com.sokol.simplemonerodonationservice.crypto.CryptoConfirmationType;
import com.sokol.simplemonerodonationservice.donation.service.DonationService;
import com.sokol.simplemonerodonationservice.donation.dto.DonationSettingsDataDTO;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataDTO;
import com.sokol.simplemonerodonationservice.user.UserController;
import com.sokol.simplemonerodonationservice.user.UserDataResponseDTO;
import com.sokol.simplemonerodonationservice.user.service.UserService;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.security.Principal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class UserControllerTest {
    private final static String moreThan64Chars = RandomString.make(65);
    private final static String moreThan255Chars = RandomString.make(256);

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private DonationService donationService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void retrieveUserData_ShouldReturnStatusNotFound() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("name");

        when(userService.getUserDataByPrincipal(anyString()))
                .thenThrow(new ResourceNotFoundException("There is no user with such principal"));

        mockMvc.perform(
                get("/api/user")
                        .principal(principal)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void retrieveUserData_ShouldReturnStatusOk() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("name");

        when(userService.getUserDataByPrincipal(principal.getName()))
                .thenReturn(new UserDataResponseDTO("name", "name@email.com"));

        MvcResult mvcResult = mockMvc.perform(
                get("/api/user")
                        .principal(principal)
        ).andExpect(status().isOk()).andReturn();

        UserDataResponseDTO userDataResponseDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDataResponseDTO.class);

        assertEquals(
                new UserDataResponseDTO("name", "name@email.com"),
                userDataResponseDTO
        );
    }

    @Test
    public void retrieveDonationUserData_ShouldReturnStatusNotFound() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("name");

        when(donationService.getDonationUserDataByPrincipal(anyString()))
                .thenThrow(new ResourceNotFoundException(""));

        mockMvc.perform(
                get("/api/user/profile")
                        .principal(principal)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void retrieveDonationUserData_ShouldReturnStatusOk() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("name");

        when(donationService.getDonationUserDataByPrincipal("name"))
                .thenReturn(new DonationUserDataDTO("name", "name@email.com"));

        MvcResult mvcResult = mockMvc.perform(
                get("/api/user/profile")
                        .principal(principal)
        ).andExpect(status().isOk()).andReturn();

        DonationUserDataDTO response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), DonationUserDataDTO.class) ;

        assertEquals(
                new DonationUserDataDTO("name", "name@email.com"),
                response
        );
    }

    @Test
    public void editUserDonationData_ShouldReturnValidationError() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("name");

        when(donationService.modifyDonationUserDataByPrincipal(any(), any()))
                .thenReturn(new DonationUserDataDTO("", ""));

        // Blank username
        mockMvc.perform(
                put("/api/user/settings/profile")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DonationUserDataDTO("", "")))
                ).andExpect(status().isBadRequest());

        // More than 64 chars username
        mockMvc.perform(
                put("/api/user/settings/profile")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DonationUserDataDTO(moreThan64Chars, "")))
        ).andExpect(status().isBadRequest());

        // More than 255 chars greetingText
        mockMvc.perform(
                put("/api/user/settings/profile")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DonationUserDataDTO("username", moreThan255Chars)))
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void editUserDonationData_ShouldReturnStatusOk() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("name");

        when(donationService.modifyDonationUserDataByPrincipal(eq(principal.getName()), any()))
                .thenReturn(new DonationUserDataDTO("username", "greetingText"));


        MvcResult mvcResult = mockMvc.perform(
                put("/api/user/settings/profile")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DonationUserDataDTO("username", "greetingText")))
        ).andExpect(status().isOk()).andReturn();

        assertEquals(
                new DonationUserDataDTO("username", "greetingText"),
                objectMapper.readValue(mvcResult.getRequest().getContentAsString(), DonationUserDataDTO.class)
        );
    }

    @Test
    public void getDonationSettingsData_ShouldReturnStatusNotFound() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("name");

        when(donationService.getDonationSettingsDataDTOByPrincipal(anyString()))
                .thenThrow(new ResourceNotFoundException(""));

        mockMvc.perform(
                get("/api/user/settings/donation")
                        .principal(principal)
                ).andExpect(status().isNotFound());
    }

    @Test
    public void getDonationSettingsData_ShouldReturnStatusOk() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("name");

        when(donationService.getDonationSettingsDataDTOByPrincipal(anyString()))
                .thenReturn(new DonationSettingsDataDTO("", "", 1, 1));

        mockMvc.perform(
                get("/api/user/settings/donation")
                        .principal(principal)
        ).andExpect(status().isOk());
    }


    @Test
    public void generateNewUserToken_ShouldReturnStatusNotFound() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("name");

        when(donationService.regenerateDonationToken(anyString()))
                .thenThrow(new ResourceNotFoundException(""));

        mockMvc.perform(
                put("/api/user/settings/donation/genNewToken")
                        .principal(principal)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void generateNewUserToken_ShouldReturnStatusCreated() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("name");

        when(donationService.regenerateDonationToken(principal.getName()))
                .thenReturn(new DonationSettingsDataDTO(UUID.randomUUID().toString(), "", 1, 1));

        mockMvc.perform(
                put("/api/user/settings/donation/genNewToken")
                        .principal(principal)
        ).andExpect(status().isCreated());
    }

    @Test
    public void updateDonationSettingsData_ShouldReturnValidationError() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("name");

        when(donationService.updateDonationSettingsData(eq(principal.getName()), any()))
                .thenReturn(new DonationSettingsDataDTO("", "", 1, 1));

        // Blank userToken
        mockMvc.perform(
                put("/api/user/settings/donation")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DonationSettingsDataDTO("", CryptoConfirmationType.UNCONFIRMED.name(), 60, 1)))
        ).andExpect(status().isBadRequest());

        // Blank confirmation type
        mockMvc.perform(
                put("/api/user/settings/donation")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DonationSettingsDataDTO(UUID.randomUUID().toString(), "", 60, 1)))
        ).andExpect(status().isBadRequest());

        // Not enum value
        mockMvc.perform(
                put("/api/user/settings/donation")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DonationSettingsDataDTO(UUID.randomUUID().toString(), "Confirm", 60, 1)))
        ).andExpect(status().isBadRequest());

        // Timeout less than 60
        mockMvc.perform(
                put("/api/user/settings/donation")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DonationSettingsDataDTO(UUID.randomUUID().toString(), CryptoConfirmationType.UNCONFIRMED.name(), -12, 1)))
        ).andExpect(status().isBadRequest());

        // Min amount not positive
        mockMvc.perform(
                put("/api/user/settings/donation")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DonationSettingsDataDTO(UUID.randomUUID().toString(), CryptoConfirmationType.UNCONFIRMED.name(), 60, -1)))
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void updateDonationSettingsData_ShouldReturnStatusNotFound() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("name");

        Principal principal1 = Mockito.mock(Principal.class);
        when(principal1.getName()).thenReturn("name1");

        when(donationService.updateDonationSettingsData(anyString(), any()))
                .thenThrow(new ResourceNotFoundException(""));

        when(donationService.updateDonationSettingsData(eq(principal.getName()), any()))
                .thenReturn(new DonationSettingsDataDTO("", "", 1, 1));

        // Blank userToken
        mockMvc.perform(
                put("/api/user/settings/donation")
                        .principal(principal1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DonationSettingsDataDTO(UUID.randomUUID().toString(), CryptoConfirmationType.UNCONFIRMED.name(), 60, 1)))
        ).andExpect(status().isNotFound());
    }

    @Test
    public void updateDonationSettingsData_ShouldReturnStatusCreated() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("name");

        Principal principal1 = Mockito.mock(Principal.class);
        when(principal1.getName()).thenReturn("name1");

        when(donationService.updateDonationSettingsData(anyString(), any()))
                .thenThrow(new ResourceNotFoundException(""));

        when(donationService.updateDonationSettingsData(eq(principal.getName()), any()))
                .thenReturn(new DonationSettingsDataDTO("", "", 1, 1));

        MvcResult mvcResult = mockMvc.perform(
                put("/api/user/settings/donation")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DonationSettingsDataDTO(UUID.randomUUID().toString(), CryptoConfirmationType.UNCONFIRMED.name(), 60, 1)))
        ).andExpect(status().isCreated()).andReturn();

        assertEquals(
                new DonationSettingsDataDTO("", "", 1, 1),
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), DonationSettingsDataDTO.class)
        );

    }



}

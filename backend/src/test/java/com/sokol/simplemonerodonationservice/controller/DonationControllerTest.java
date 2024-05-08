package com.sokol.simplemonerodonationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sokol.simplemonerodonationservice.base.exception.ResourceNotFoundException;
import com.sokol.simplemonerodonationservice.crypto.coin.CoinType;
import com.sokol.simplemonerodonationservice.donation.DonationController;
import com.sokol.simplemonerodonationservice.donation.DonationRequestDTO;
import com.sokol.simplemonerodonationservice.donation.DonationResponseDTO;
import com.sokol.simplemonerodonationservice.donation.DonationService;
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
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DonationController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class DonationControllerTest {
    private final static String moreThan300Chars = RandomString.make(301);
    private final static String moreThan64Chars = RandomString.make(65);

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DonationService donationService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAllDonations_ShouldReturnStatusNotFound() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("name");

        when(donationService.getAllDonations(anyString()))
                .thenThrow(new ResourceNotFoundException(""));

        mockMvc.perform(
                get("/api/donation/getAll")
                        .principal(principal)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void getAllDonations_ShouldReturnStatusOk() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("name");

        when(donationService.getAllDonations(principal.getName()))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(
                get("/api/donation/getAll")
                        .principal(principal)
        ).andExpect(status().isOk());
    }

    @Test
    public void getAllDonationsByPageNum_ShouldReturnStatusNotFound() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("name");

        when(donationService.getAllDonations(principal.getName()))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(
                get("/api/donation/getAll")
                        .principal(principal)
        ).andExpect(status().isOk());
    }

    @Test
    public void getAllDonationsByPageNum_ShouldReturnStatusOk() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("name");

        when(donationService.getAllDonations(principal.getName()))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(
                get("/api/donation/getAll")
                        .principal(principal)
        ).andExpect(status().isOk());
    }

    @Test
    public void fetchMoneroSubaddressByUsername_ShouldReturnValidationError() throws Exception {
        when(donationService.implementDonationRequest(any(), any()))
                .thenReturn(new DonationResponseDTO("",1, "", 1, ""));

        // Blank username
        mockMvc.perform(
                post("/api/donation/donate/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DonationRequestDTO(" ", "", CoinType.XMR.name())))
        ).andExpect(status().isBadRequest());

        // More than 64 chars username
        mockMvc.perform(
                post("/api/donation/donate/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DonationRequestDTO(moreThan64Chars, "", CoinType.XMR.name())))
        ).andExpect(status().isBadRequest());

        // More than 300 chars donationText
        mockMvc.perform(
                post("/api/donation/donate/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DonationRequestDTO("username", moreThan300Chars, CoinType.XMR.name())))
        ).andExpect(status().isBadRequest());

        // Blank coin type
        mockMvc.perform(
                post("/api/donation/donate/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DonationRequestDTO("username", "", " ")))
        ).andExpect(status().isBadRequest());

        // Not enum value coinType
        mockMvc.perform(
                post("/api/donation/donate/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DonationRequestDTO("username", "", "xmr")))
        ).andExpect(status().isBadRequest());

    }

    @Test
    public void fetchMoneroSubaddressByUsername_ShouldReturnStatusOk() throws Exception {
        when(donationService.implementDonationRequest(any(), any()))
                .thenReturn(new DonationResponseDTO("",1, "", 1, ""));

        mockMvc.perform(
                post("/api/donation/donate/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DonationRequestDTO("username", "", CoinType.XMR.name())))
        ).andExpect(status().isOk());
    }




}

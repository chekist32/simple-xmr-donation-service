package com.sokol.simplemonerodonationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sokol.simplemonerodonationservice.base.exception.ResourceNotFoundException;
import com.sokol.simplemonerodonationservice.donation.DonationController;
import com.sokol.simplemonerodonationservice.donation.DonationService;
import com.sokol.simplemonerodonationservice.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.security.Principal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DonationController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class DonationControllerTest {
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





}

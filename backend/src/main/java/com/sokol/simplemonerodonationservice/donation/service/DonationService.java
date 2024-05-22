package com.sokol.simplemonerodonationservice.donation.service;

import com.sokol.simplemonerodonationservice.crypto.payment.event.ConfirmedPaymentEvent;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataDTO;
import com.sokol.simplemonerodonationservice.donation.dto.DonationDTO;
import com.sokol.simplemonerodonationservice.donation.dto.DonationRequestDTO;
import com.sokol.simplemonerodonationservice.donation.dto.DonationResponseDTO;
import com.sokol.simplemonerodonationservice.donation.dto.DonationSettingsDataDTO;

import java.util.List;

public interface DonationService {
    long getDonationCount(String principal);

    List<DonationDTO> getAllDonations(String principal);
    List<DonationDTO> getAllDonations(String principal, int pageNum);
    List<DonationDTO> getAllDonations(String principal, int pageNum, int pageSize);

    DonationResponseDTO implementDonationRequest(DonationRequestDTO donationRequestDTO, String username);

    DonationUserDataDTO getDonationUserDataByUsername(String username);
    DonationUserDataDTO getDonationUserDataByPrincipal(String principal);

    DonationUserDataDTO modifyDonationUserDataByPrincipal(String principal, DonationUserDataDTO donationUserDataDTO);

    boolean validateToken(String token);

    DonationSettingsDataDTO getDonationSettingsDataDTOByPrincipal(String principal);

    DonationSettingsDataDTO regenerateDonationToken(String principal);

    DonationSettingsDataDTO updateDonationSettingsData(String principal, DonationSettingsDataDTO donationSettingsDataDTO);

    void onConfirmedPayment(ConfirmedPaymentEvent confirmedPaymentEvent);
}

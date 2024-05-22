package com.sokol.simplemonerodonationservice.donation.notification;

import com.sokol.simplemonerodonationservice.donation.dto.DonationDTO;

public interface DonationNotificationService {
    void sendTestDonationMessageToAllClients();
    void sendDonationMessageToAllClients(DonationDTO donationDTO);
}

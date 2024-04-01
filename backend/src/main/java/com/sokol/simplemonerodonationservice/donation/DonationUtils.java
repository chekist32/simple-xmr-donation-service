package com.sokol.simplemonerodonationservice.donation;

import com.sokol.simplemonerodonationservice.crypto.payment.PaymentEntity;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataEntity;

public class DonationUtils {
    public static DonationDTO DonationEntityToDonationDTOMapper(DonationEntity donationEntity) {
        PaymentEntity payment = donationEntity.getPayment();

        return new DonationDTO(
                donationEntity.getSenderUsername().trim(),
                payment.getAmount(),
                payment.getCoinType().name(),
                donationEntity.getDonationText().trim(),
                payment.getConfirmedAt()
        );
    }
    public static DonationSettingsDataDTO DonationUserDataToDonationSettingsDataDTOMapper(DonationUserDataEntity donationUserData) {
        return new DonationSettingsDataDTO(
                donationUserData.getToken(),
                donationUserData.getConfirmationType().toString(),
                donationUserData.getTimeout()/1000,
                donationUserData.getMinDonationAmount()
        );
    }
}

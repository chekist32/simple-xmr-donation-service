package com.sokol.simplemonerodonationservice.donation;

public class DonationUtils {
    public static DonationDTO DonationEntityToDonationDTOMapper(DonationEntity donationEntity) {
        return new DonationDTO(
                donationEntity.getSenderUsername().trim(),
                donationEntity.getAmount().toString().trim() + " XMR",
                donationEntity.getDonationText().trim(),
                donationEntity.getConfirmedAt()
        );
    }

}

package com.sokol.simplemonerodonationservice.donation;

import com.sokol.simplemonerodonationservice.base.annotation.enumvalue.EnumValue;
import com.sokol.simplemonerodonationservice.crypto.CryptoConfirmationType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record DonationSettingsDataDTO(
        @NotBlank
        String userToken,

        @NotBlank
        @EnumValue(enumClass = CryptoConfirmationType.class)
        String confirmationType,

        @Min(value = 60)
        int timeout,

        @Positive
        double minAmount
) { }

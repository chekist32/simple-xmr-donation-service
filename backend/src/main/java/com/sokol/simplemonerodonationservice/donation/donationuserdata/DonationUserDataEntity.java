package com.sokol.simplemonerodonationservice.donation.donationuserdata;

import com.sokol.simplemonerodonationservice.crypto.CryptoConfirmationType;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "donation_user_data")
public class DonationUserDataEntity {
    @Id
    @GeneratedValue
    private Integer id;
    private String greetingText;
    @Column(nullable = false)
    private double minDonationAmount;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CryptoConfirmationType confirmationType;
    @Column(
            nullable = false,
            unique = true
    )
    private String token = UUID.randomUUID().toString();
    @Column(nullable = false)
    private int timeout;

    public DonationUserDataEntity() { this("Default greeting text", 0.1, CryptoConfirmationType.UNCONFIRMED, 40 * 60 * 1000); }

    public DonationUserDataEntity(String greetingText, double minDonationAmount, CryptoConfirmationType confirmationType, int timeout) {
        this.greetingText = greetingText;
        this.confirmationType = confirmationType;
        this.setTimeout(timeout);
        this.setMinDonationAmount(minDonationAmount);
    }

    public Integer getId() {
        return id;
    }

    public String getGreetingText() {
        return greetingText;
    }

    public void setGreetingText(String greetingText) {
        this.greetingText = greetingText;
    }

    public double getMinDonationAmount() {
        return minDonationAmount;
    }

    public void setMinDonationAmount(double minDonationAmount) {
        this.minDonationAmount = minDonationAmount > 0 ? minDonationAmount : 0.01;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = Math.max(60, timeout);
    }

    public CryptoConfirmationType getConfirmationType() {
        return confirmationType;
    }

    public void setConfirmationType(CryptoConfirmationType confirmationType) {
        this.confirmationType = confirmationType;
    }

    public String getToken() {
        return token;
    }

    public void regenerateToken() {
        this.token = UUID.randomUUID().toString();
    }
}

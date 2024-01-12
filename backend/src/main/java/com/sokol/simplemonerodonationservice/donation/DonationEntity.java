package com.sokol.simplemonerodonationservice.donation;

import com.sokol.simplemonerodonationservice.payment.PaymentEntity;
import com.sokol.simplemonerodonationservice.user.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "donations")
public class DonationEntity {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(length = 64)
    private String senderUsername;
    @Column(length = 300)
    private String donationText;
    private String moneroSubaddress;
    private Double amount;
    @Column(nullable = false)
    private LocalDateTime receivedAt;
    private LocalDateTime confirmedAt;
    @Column(nullable = false)
    private boolean isPaymentConfirmed = false;
    @Column(nullable = false)
    private boolean isPaymentExpired = false;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            nullable = false,
            name = "user_id"
    )
    private UserEntity user;
    @OneToOne
    @JoinColumn(
            nullable = false,
            name = "payment_id"
    )
    private PaymentEntity payment;

    public DonationEntity() { }

    public DonationEntity(String senderUsername,
                          String donationText,
                          LocalDateTime receivedAt,
                          UserEntity user,
                          PaymentEntity payment) {
        this.senderUsername = senderUsername;
        this.donationText = donationText;
        this.receivedAt = receivedAt;
        this.user = user;
        this.payment = payment;
    }

    public DonationEntity(String senderUsername,
                          String donationText,
                          String moneroSubaddress,
                          Double amount,
                          LocalDateTime receivedAt,
                          UserEntity user,
                          PaymentEntity payment) {
        this.senderUsername = senderUsername;
        this.donationText = donationText;
        this.moneroSubaddress = moneroSubaddress;
        this.amount = amount;
        this.receivedAt = receivedAt;
        this.user = user;
        this.payment = payment;
    }

    public UUID getId() {
        return id;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public String getDonationText() {
        return donationText;
    }

    public String getMoneroSubaddress() {
        return moneroSubaddress;
    }

    public void setMoneroSubaddress(String moneroSubaddress) {
        this.moneroSubaddress = moneroSubaddress;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public boolean getIsPaymentConfirmed() {
        return isPaymentConfirmed;
    }

    public void setIsPaymentConfirmed(boolean isPaymentConfirmed) {
        this.isPaymentConfirmed = isPaymentConfirmed;
    }

    public PaymentEntity getPayment() {
        return payment;
    }


}

package com.sokol.simplemonerodonationservice.donation;

import com.sokol.simplemonerodonationservice.payment.PaymentEntity;
import com.sokol.simplemonerodonationservice.user.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    @Column(nullable = false)
    private LocalDateTime receivedAt = LocalDateTime.now(ZoneOffset.UTC);
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
                          UserEntity user,
                          PaymentEntity payment) {
        this.senderUsername = senderUsername;
        this.donationText = donationText;
        this.user = user;
        this.payment = payment;
    }

    public DonationEntity(String senderUsername,
                          String donationText,
                          String moneroSubaddress,
                          UserEntity user,
                          PaymentEntity payment) {
        this.senderUsername = senderUsername;
        this.donationText = donationText;
        this.moneroSubaddress = moneroSubaddress;
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

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public PaymentEntity getPayment() {
        return payment;
    }
}

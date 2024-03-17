package com.sokol.simplemonerodonationservice.donation;

import com.sokol.simplemonerodonationservice.crypto.payment.PaymentEntity;
import com.sokol.simplemonerodonationservice.crypto.payment.PaymentPurposeType;
import com.sokol.simplemonerodonationservice.user.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
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
    @Column(nullable = false)
    private final LocalDateTime receivedAt = LocalDateTime.now(ZoneOffset.UTC);
    @ManyToOne
    @JoinColumn(
            nullable = false,
            name = "user_id"
    )
    private UserEntity user;
    @OneToOne
    @JoinColumn(
            nullable = false,
            unique = true,
            name = "payment_id"
    )
    private PaymentEntity payment;

    protected DonationEntity() { }

    public DonationEntity(String senderUsername,
                          String donationText,
                          UserEntity user,
                          PaymentEntity payment) {
        this.senderUsername = senderUsername;
        this.donationText = donationText;
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

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public PaymentEntity getPayment() {
        return payment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DonationEntity that = (DonationEntity) o;

        if (!Objects.equals(id, that.id)) return false;
        return receivedAt.equals(that.receivedAt);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + receivedAt.hashCode();
        return result;
    }
}

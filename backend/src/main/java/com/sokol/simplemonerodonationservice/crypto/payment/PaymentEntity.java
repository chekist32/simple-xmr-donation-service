package com.sokol.simplemonerodonationservice.crypto.payment;

import com.sokol.simplemonerodonationservice.crypto.coin.CoinType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class PaymentEntity {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false)
    private String cryptoAddress;
    private double requiredAmount;
    private double amount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(ZoneOffset.UTC);
    private LocalDateTime confirmedAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoinType coinType;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentPurposeType paymentPurpose;

    public PaymentEntity() { }

    public PaymentEntity(String cryptoAddress,
                         CoinType coinType,
                         PaymentPurposeType paymentPurpose) {
        this.cryptoAddress = cryptoAddress;
        this.coinType = coinType;
        this.paymentPurpose = paymentPurpose;
    }

    public PaymentEntity(String cryptoAddress,
                         double requiredAmount,
                         CoinType coinType,
                         PaymentPurposeType paymentPurpose) {
        this.cryptoAddress = cryptoAddress;
        this.requiredAmount = requiredAmount;
        this.coinType = coinType;
        this.paymentPurpose = paymentPurpose;
    }

    public UUID getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) { this.amount = amount; }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public CoinType getCoinType() {
        return coinType;
    }

    public String getCryptoAddress() {
        return cryptoAddress;
    }

    public double getRequiredAmount() {
        return requiredAmount;
    }

    public void setRequiredAmount(double requiredAmount) {
        this.requiredAmount = requiredAmount;
    }

    public PaymentPurposeType getPaymentPurpose() {
        return paymentPurpose;
    }

    public void setPaymentPurpose(PaymentPurposeType paymentPurpose) {
        this.paymentPurpose = paymentPurpose;
    }
}

package com.sokol.simplemonerodonationservice.payment;

import com.sokol.simplemonerodonationservice.crypto.CoinType;
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

    public PaymentEntity() { }

    public PaymentEntity(String cryptoAddress, CoinType coinType) {
        this.cryptoAddress = cryptoAddress;
        this.coinType = coinType; }

    public PaymentEntity(String cryptoAddress, double amount, CoinType coinType) {
        this.cryptoAddress = cryptoAddress;
        this.amount = amount;
        this.coinType = coinType;
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
}

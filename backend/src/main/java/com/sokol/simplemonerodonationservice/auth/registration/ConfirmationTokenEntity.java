package com.sokol.simplemonerodonationservice.auth.registration;

import com.sokol.simplemonerodonationservice.user.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "confirmation_tokens")
public class ConfirmationTokenEntity {
    private final long EXPIRATION_TIME = 24;
    @Id
    private String token = UUID.randomUUID().toString();
    @Column(nullable = false)
    private LocalDateTime expirationDate = LocalDateTime.now(ZoneOffset.UTC).plusHours(EXPIRATION_TIME);
    private LocalDateTime confirmedAt;
    @ManyToOne
    @JoinColumn(
            nullable = false,
            name = "user_id"
    )
    private UserEntity user;
    @Column(nullable = false)
    private boolean isActive = true;


    public ConfirmationTokenEntity(UserEntity user) {
        this.user = user;
    }

    public ConfirmationTokenEntity() { }

    public String getToken() {
        return token;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public UserEntity getUser() {
        return user;
    }

    public boolean isActive() {
        return isActive;
    }

    public void implementToken() {
        this.isActive = false;
        this.confirmedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    public void expireToken() {
        this.isActive = false;
    }

    @Override
    public String toString() {
        return "ConfirmationTokenEntity{" +
                ", token='" + token + '\'' +
                ", confirmedAt=" + confirmedAt +
                '}';
    }
}

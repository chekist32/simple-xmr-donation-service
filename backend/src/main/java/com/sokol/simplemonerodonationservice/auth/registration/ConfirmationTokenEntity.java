package com.sokol.simplemonerodonationservice.auth.registration;

import com.sokol.simplemonerodonationservice.user.UserEntity;
import com.sokol.simplemonerodonationservice.user.UserEntityModificationRequestEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "confirmation_tokens")
public class ConfirmationTokenEntity {
    private final long EXPIRATION_TIME = 24;
    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false)
    private String token;
    @Column(nullable = false)
    private LocalDateTime expirationDate = LocalDateTime.now(ZoneOffset.UTC).plusHours(EXPIRATION_TIME);
    private LocalDateTime confirmedAt;
    @ManyToOne
    @JoinColumn(
            nullable = false,
            name = "user_id"
    )
    private UserEntity user;
    @OneToOne
    @JoinColumn(
            name = "user_entity_modification_request_id"
    )
    private UserEntityModificationRequestEntity modificationRequest;
    @Column(nullable = false)
    private boolean isActive = true;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ConfirmationTokenType confirmationTokenType;


    public ConfirmationTokenEntity(String token,
                                   UserEntity user,
                                   ConfirmationTokenType confirmationTokenType,
                                   UserEntityModificationRequestEntity modificationRequest) {
        this.token = token;
        this.user = user;
        this.confirmationTokenType = confirmationTokenType;
        this.modificationRequest = modificationRequest;
    }

    public ConfirmationTokenEntity() { }

    public UUID getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public UserEntity getUser() {
        return user;
    }

    public UserEntityModificationRequestEntity getModificationRequest() {
        return modificationRequest;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "ConfirmationTokenEntity{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", confirmedAt=" + confirmedAt +
                '}';
    }
}

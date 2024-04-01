package com.sokol.simplemonerodonationservice.auth.registration;

import com.sokol.simplemonerodonationservice.user.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "confirmation_tokens")
public class ConfirmationTokenEntity {
    private final static long EXPIRATION_TIME = 24;
    @Id
    private final String token = UUID.randomUUID().toString();
    @Column(nullable = false)
    private final LocalDateTime expirationDate = LocalDateTime.now(ZoneOffset.UTC).plusHours(EXPIRATION_TIME);
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

    protected ConfirmationTokenEntity() { }

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfirmationTokenEntity that = (ConfirmationTokenEntity) o;

        if (!Objects.equals(token, that.token)) return false;
        return expirationDate.equals(that.expirationDate);
    }

    @Override
    public int hashCode() {
        int result = token != null ? token.hashCode() : 0;
        result = 31 * result + expirationDate.hashCode();
        return result;
    }
}

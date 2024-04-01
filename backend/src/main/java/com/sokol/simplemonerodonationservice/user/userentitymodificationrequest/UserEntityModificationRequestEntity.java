package com.sokol.simplemonerodonationservice.user.userentitymodificationrequest;

import com.sokol.simplemonerodonationservice.auth.registration.ConfirmationTokenEntity;
import com.sokol.simplemonerodonationservice.user.UserEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "user_modification_request")
public class UserEntityModificationRequestEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String newEmail;
    private String newPassword;
    private boolean setEnabled;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserEntityModificationRequestEntityType modificationRequestType;
    @OneToOne
    @JoinColumn(
            name = "confirmation_token_id",
            nullable = false
    )
    private ConfirmationTokenEntity confirmationToken;
    @OneToOne
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private UserEntity user;


    public UserEntityModificationRequestEntity() { }

    public UserEntityModificationRequestEntity(UserEntityModificationRequestEntityType modificationRequestType,
                                                ConfirmationTokenEntity confirmationToken,
                                                UserEntity user) {
        this.modificationRequestType = modificationRequestType;
        this.confirmationToken = confirmationToken;
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public boolean getSetEnabled() {
        return setEnabled;
    }

    public UserEntityModificationRequestEntity setNewEmail(String newEmail) {
        this.newEmail = newEmail;
        return this;
    }

    public UserEntityModificationRequestEntity setNewPassword(String newPassword) {
        this.newPassword = newPassword;
        return this;
    }

    public UserEntityModificationRequestEntity setSetEnabled(boolean setEnabled) {
        this.setEnabled = setEnabled;
        return this;
    }

    public ConfirmationTokenEntity getConfirmationToken() {
        return confirmationToken;
    }

    public UserEntityModificationRequestEntityType getModificationRequestType() {
        return modificationRequestType;
    }

    public UserEntity getUser() {
        return user;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEntityModificationRequestEntity that = (UserEntityModificationRequestEntity) o;

        if (!id.equals(that.id)) return false;
        return modificationRequestType == that.modificationRequestType;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + modificationRequestType.hashCode();
        return result;
    }
}

package com.sokol.simplemonerodonationservice.user;

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
    private UserEntityModificationRequestEntityType modificationRequestEntityType;

    public UserEntityModificationRequestEntity() { }
    public UserEntityModificationRequestEntity(UserEntityModificationRequestEntityType modificationRequestEntityType) {
        this.modificationRequestEntityType = modificationRequestEntityType;
    }

    public UUID getId() {
        return id;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public boolean getSetEnabled() {
        return setEnabled;
    }

    public void setSetEnabled(boolean setEnabled) {
        this.setEnabled = setEnabled;
    }

    public UserEntityModificationRequestEntityType getUserEntityModificationRequestEntityType() {
        return this.modificationRequestEntityType;
    }
}

package com.sokol.simplemonerodonationservice.donation.donationuserdata;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "donation_user_data")
public class DonationUserDataEntity {
    @Id
    @GeneratedValue
    private Integer id;
    private String greetingText;

    public DonationUserDataEntity() { this("Default greeting text"); }

    public DonationUserDataEntity(String greetingText) {
        this.greetingText = greetingText;
    }

    public Integer getId() {
        return id;
    }

    public String getGreetingText() {
        return greetingText;
    }

    public void setGreetingText(String greetingText) {
        this.greetingText = greetingText;
    }
}

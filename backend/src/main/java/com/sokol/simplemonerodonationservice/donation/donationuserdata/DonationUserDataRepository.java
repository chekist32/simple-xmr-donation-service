package com.sokol.simplemonerodonationservice.donation.donationuserdata;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface DonationUserDataRepository extends CrudRepository<DonationUserDataEntity, Integer> {
    boolean existsByToken(String token);
}

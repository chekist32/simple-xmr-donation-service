package com.sokol.simplemonerodonationservice.donation.donationuserdata;

import org.springframework.data.repository.CrudRepository;

public interface DonationUserDataRepository extends CrudRepository<DonationUserDataEntity, Integer> {
    boolean existsByToken(String token);
}

package com.sokol.simplemonerodonationservice.repository;

import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataEntity;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase
class DonationUserDataRepositoryTest {

    @Autowired
    private DonationUserDataRepository donationUserDataRepository;

    private final static List<DonationUserDataEntity> donationUserDataEntities = List.of(
            new DonationUserDataEntity(),
            new DonationUserDataEntity(),
            new DonationUserDataEntity()
    );


    protected static Stream<DonationUserDataEntity> donationUserDataEntityProvider() {
        return donationUserDataEntities.stream();
    }

    @BeforeEach
    protected void init() {
        donationUserDataRepository.saveAll(donationUserDataEntities);
    }

    @ParameterizedTest
    @MethodSource("donationUserDataEntityProvider")
    public void existsByToken_ShouldReturnTrue(DonationUserDataEntity donationUserData) {
        assertTrue(donationUserDataRepository.existsByToken(donationUserData.getToken()));
    }
}

package com.sokol.simplemonerodonationservice.integration.api;

import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataEntity;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataRepository;
import com.sokol.simplemonerodonationservice.user.UserEntity;
import com.sokol.simplemonerodonationservice.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class UserApiTest {
    @Container
    private static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DonationUserDataRepository donationUserDataRepository;

    private void initUser() {
        DonationUserDataEntity donationUserData = new DonationUserDataEntity();
        donationUserDataRepository.save(donationUserData);

        UserEntity user = new UserEntity("email@email.com", "username", "pass");
        user.setDonationUserData(donationUserData);
        userRepository.save(user);
    }






}

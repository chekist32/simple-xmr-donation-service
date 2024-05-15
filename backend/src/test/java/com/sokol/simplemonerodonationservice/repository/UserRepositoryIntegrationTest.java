package com.sokol.simplemonerodonationservice.repository;

import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataEntity;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataRepository;
import com.sokol.simplemonerodonationservice.user.UserEntity;
import com.sokol.simplemonerodonationservice.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase()
class UserRepositoryIntegrationTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DonationUserDataRepository donationUserDataRepository;

    private static final List<UserEntity> users = List.of(
            new UserEntity(
                    "test1@email.com",
                    "testusername1",
                    "testpass1"
            ),
            new UserEntity(
                "test2@email.com",
                        "testusername2",
                        "testpass2"
            ),
            new UserEntity(
                "test3@email.com",
                        "testusername3",
                        "testpass3"
            )
    );

    protected static Stream<UserEntity> userProvider() {
        return users.stream();
    }

    @BeforeEach
    protected void init() {
        users.forEach(user -> user.setDonationUserData(donationUserDataRepository.save(new DonationUserDataEntity())));
        userRepository.saveAll(users);
    }

    @Test
    protected void saveAll_ShouldSaveAllEntities() {
        Set<String> userEmailsSet = new HashSet<>();
        Set<String> userUsernamesSet = new HashSet<>();
        for (var user : users) {
            userEmailsSet.add(user.getEmail());
            userUsernamesSet.add(user.getUsername());
        }

        Set<String> fetchedUserEmailsSet = new HashSet<>();
        Set<String> fetchedUserUsernamesSet = new HashSet<>();
        for (var user : userRepository.findAll()) {
            fetchedUserEmailsSet.add(user.getEmail());
            fetchedUserUsernamesSet.add(user.getUsername());
        }

        assertEquals(userEmailsSet, fetchedUserEmailsSet);
        assertEquals(userUsernamesSet, fetchedUserUsernamesSet);
    }

    @ParameterizedTest
    @MethodSource("userProvider")
    public void findByUsername_ShouldReturnUserByUsername(UserEntity user) {
        Optional<UserEntity> fetchedUser = userRepository.findByUsername(user.getUsername());

        assertTrue(fetchedUser.isPresent());

        assertEquals(user.getUsername(), fetchedUser.get().getUsername());
        assertEquals(user.getEmail(), fetchedUser.get().getEmail());
        assertEquals(user.getPassword(), fetchedUser.get().getPassword());
    }

    @ParameterizedTest
    @MethodSource("userProvider")
    public void findByEmail_ShouldReturnUserByEmail(UserEntity user) {
        Optional<UserEntity> fetchedUser = userRepository.findByEmail(user.getEmail());

        assertTrue(fetchedUser.isPresent());

        assertEquals(user.getUsername(), fetchedUser.get().getUsername());
        assertEquals(user.getEmail(), fetchedUser.get().getEmail());
        assertEquals(user.getPassword(), fetchedUser.get().getPassword());
    }

    @ParameterizedTest
    @MethodSource("userProvider")
    public void existsByEmail_ShouldReturnTrueIfUserWithSuchEmailExists(UserEntity user) {
        assertTrue(userRepository.existsByEmail(user.getEmail()));
    }

    @ParameterizedTest
    @MethodSource("userProvider")
    public void existsByUsername_ShouldReturnTrueIfUserWithSuchUsernameExists(UserEntity user) {
        assertTrue(userRepository.existsByUsername(user.getUsername()));
    }

    @ParameterizedTest
    @MethodSource("userProvider")
    public void existsByEmailAndIsEnabledFalse_ShouldReturnTrueIfUserWithSuchEmailAndIsEnabledFalseExists(UserEntity user) {
        assertTrue(userRepository.existsByEmailAndIsEnabledFalse(user.getEmail()));
    }

    @Test
    public void existsByEmailAndIsEnabledFalse_ShouldReturnFalse() {
        UserEntity user = userRepository.findByEmail(users.get(0).getEmail()).get();
        user.setEnabled(true);
        userRepository.save(user);

        assertFalse(userRepository.existsByEmailAndIsEnabledFalse(user.getEmail()));
    }

    @ParameterizedTest
    @MethodSource("userProvider")
    public void findByPrincipal_ShouldReturnSameRecord(UserEntity user) {
        UserEntity fetchedUserByUsername = userRepository.findByPrincipal(user.getUsername()).get();
        UserEntity fetchedUserByEmail = userRepository.findByPrincipal(user.getEmail()).get();

        assertEquals(fetchedUserByEmail, fetchedUserByUsername);
    }

    @Test
    public void countByIsEnabledTrue_ShouldReturn2() {
        UserEntity user = userRepository.findByEmail(users.get(0).getEmail()).get();
        user.setEnabled(true);
        userRepository.save(user);

        UserEntity user1 = userRepository.findByEmail(users.get(1).getEmail()).get();
        user1.setEnabled(true);
        userRepository.save(user1);

        assertEquals(2, userRepository.countByIsEnabledTrue());

    }

}

package com.sokol.simplemonerodonationservice.user;

import com.sokol.simplemonerodonationservice.auth.ChangeEmailRequestDTO;
import com.sokol.simplemonerodonationservice.auth.ChangePasswordRequestDTO;
import com.sokol.simplemonerodonationservice.auth.registration.ConfirmationTokenEntity;
import com.sokol.simplemonerodonationservice.auth.registration.ConfirmationTokenService;
import com.sokol.simplemonerodonationservice.auth.registration.RegistrationRequestDTO;
import com.sokol.simplemonerodonationservice.base.exception.DuplicateResourceException;
import com.sokol.simplemonerodonationservice.base.exception.ResourceNotFoundException;
import com.sokol.simplemonerodonationservice.donation.DonationSettingsDataDTO;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataEntity;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final DonationUserDataRepository donationUserDataRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       ConfirmationTokenService confirmationTokenService,
                       DonationUserDataRepository donationUserDataRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenService = confirmationTokenService;
        this.donationUserDataRepository = donationUserDataRepository;
    }

    public ConfirmationTokenEntity registerUser(RegistrationRequestDTO registrationDTO) {
        String email = registrationDTO.email().trim();
        String username = registrationDTO.username().trim();
        String hashedPassword = passwordEncoder.encode(registrationDTO.password().trim());

        if (userRepository.countByIsEnabledTrue() > 0)
            throw new DuplicateResourceException("Admin user is already created");
        else if (userRepository.existsByEmailAndIsEnabledFalse(email))
            return confirmationTokenService.createRegistrationConfirmationToken(userRepository.findByEmail(email).get());
        else if (userRepository.existsByUsername(username))
            throw new DuplicateResourceException("User with such username already exists");
        else if (userRepository.existsByEmail(email))
            throw new DuplicateResourceException("User with such email already exists");

        UserEntity registeredUser = new UserEntity(
                email,
                username,
                hashedPassword
        );

        DonationUserDataEntity donationUserData = new DonationUserDataEntity();
        donationUserDataRepository.save(donationUserData);

        registeredUser.setDonationUserData(donationUserData);
        userRepository.save(registeredUser);

        return confirmationTokenService.createRegistrationConfirmationToken(registeredUser);
    }

    public UserDataResponseDTO getUserDataByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with such username"));
        return new UserDataResponseDTO(
                user.getUsername(),
                user.getEmail()
        );
    }

    public UserDataResponseDTO getUserDataByPrincipal(String principal) {
        UserEntity user = userRepository.findByPrincipal(principal)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with such principal"));
        return new UserDataResponseDTO(
                user.getUsername(),
                user.getEmail()
        );
    }

    public DonationSettingsDataDTO regenerateUserTokenByPrincipal(String principal) {
        UserEntity user = userRepository.findByPrincipal(principal)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with such principal"));

        String newUserToken = UUID.randomUUID().toString();
        user.setToken(newUserToken);

        userRepository.save(user);

        return new DonationSettingsDataDTO(newUserToken);
    }

    public UserEntity changeUserPassword(String principal, ChangePasswordRequestDTO changePasswordRequestDTO) {
        UserEntity user = userRepository.findByPrincipal(principal)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with such principal"));

        user.setPassword(passwordEncoder.encode(changePasswordRequestDTO.newPassword()));

        return userRepository.save(user);
    }

    public ConfirmationTokenEntity changeEmail(String principal, ChangeEmailRequestDTO changeEmailRequestDTO) {
        UserEntity user = userRepository.findByPrincipal(principal)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with such principal"));

        return confirmationTokenService.createChangeEmailConfirmationToken(user, changeEmailRequestDTO.newEmail());
    }

    public ConfirmationTokenEntity resetUserPassword(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with such email"));

        return confirmationTokenService.createResetPasswordConfirmationToken(user);
    }


    private UserEntity implementUserEntityModificationRequest(UserEntity user, UserEntityModificationRequestEntity request) {
        switch (request.getUserEntityModificationRequestEntityType()) {
            case REGISTRATION -> user.setEnabled(request.getSetEnabled());
            case CHANGE_EMAIL -> user.setEmail(request.getNewEmail());
            case RESET_PASSWORD -> user.setPassword(request.getNewPassword());
            default -> throw new RuntimeException("Illegal state (undefined ConfirmationTokenType)");
        }

        return userRepository.save(user);
    }
    public UserEntity resetUserPassword(String token, String newPassword) {
        ConfirmationTokenEntity confirmationToken = confirmationTokenService
                .confirmResetPasswordConfirmationToken(token, passwordEncoder.encode(newPassword));

        return this.implementUserEntityModificationRequest(confirmationToken.getUser(), confirmationToken.getModificationRequest());
    }

    public UserEntity changeEmail(String token) {
        ConfirmationTokenEntity confirmationToken = confirmationTokenService
                .confirmEmailByChangeEmailConfirmationToken(token);

        return this.implementUserEntityModificationRequest(confirmationToken.getUser(), confirmationToken.getModificationRequest());
    }

    public UserEntity activateUser(String token) {
        ConfirmationTokenEntity confirmationToken = confirmationTokenService
                .confirmEmailByRegistrationConfirmationToken(token);

        return this.implementUserEntityModificationRequest(confirmationToken.getUser(), confirmationToken.getModificationRequest());
    }

}

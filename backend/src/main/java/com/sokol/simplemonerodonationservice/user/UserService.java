package com.sokol.simplemonerodonationservice.user;

import com.sokol.simplemonerodonationservice.auth.ChangeEmailRequestDTO;
import com.sokol.simplemonerodonationservice.auth.ChangePasswordRequestDTO;
import com.sokol.simplemonerodonationservice.auth.registration.ConfirmationTokenEntity;
import com.sokol.simplemonerodonationservice.auth.registration.RegistrationRequestDTO;
import com.sokol.simplemonerodonationservice.base.exception.DuplicateResourceException;
import com.sokol.simplemonerodonationservice.base.exception.ResourceNotFoundException;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataEntity;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataRepository;
import com.sokol.simplemonerodonationservice.user.userentitymodificationrequest.UserEntityModificationRequestService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DonationUserDataRepository donationUserDataRepository;
    private final UserEntityModificationRequestService userEntityModificationRequestService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       DonationUserDataRepository donationUserDataRepository,
                       UserEntityModificationRequestService userEntityModificationRequestService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.donationUserDataRepository = donationUserDataRepository;
        this.userEntityModificationRequestService = userEntityModificationRequestService;
    }

    public ConfirmationTokenEntity registerUser(RegistrationRequestDTO registrationDTO) {
        String email = registrationDTO.email().trim();
        String username = registrationDTO.username().trim();
        String hashedPassword = passwordEncoder.encode(registrationDTO.password().trim());

        if (userRepository.countByIsEnabledTrue() > 0)
            throw new DuplicateResourceException("Admin user is already created");
        else if (userRepository.existsByEmailAndIsEnabledFalse(email))
            return userEntityModificationRequestService.createRegistrationConfirmationToken(userRepository.findByEmail(email).get()).getConfirmationToken();
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

        return userEntityModificationRequestService.createRegistrationConfirmationToken(registeredUser).getConfirmationToken();
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

    public UserEntity changeUserPassword(String principal, ChangePasswordRequestDTO changePasswordRequestDTO) {
        UserEntity user = userRepository.findByPrincipal(principal)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with such principal"));

        user.setPassword(passwordEncoder.encode(changePasswordRequestDTO.newPassword()));

        return userRepository.save(user);
    }

    public ConfirmationTokenEntity changeEmail(String principal, ChangeEmailRequestDTO changeEmailRequestDTO) {
        UserEntity user = userRepository.findByPrincipal(principal)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with such principal"));

        return userEntityModificationRequestService.createChangeEmailModificationRequest(changeEmailRequestDTO.newEmail(), user).getConfirmationToken();
    }

    public ConfirmationTokenEntity resetUserPassword(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with such email"));

        return userEntityModificationRequestService.createResetPasswordModificationRequest(user).getConfirmationToken();
    }

    public UserEntity resetUserPassword(String token, String newPassword) {
        return userEntityModificationRequestService.implementUserEntityModificationRequest(
                userEntityModificationRequestService.updateNewPasswordForResetPasswordModificationRequest(token, newPassword)
        );
    }

    public UserEntity changeEmail(String token) {
        return userEntityModificationRequestService.implementUserEntityModificationRequest(token);
    }

    public UserEntity activateUser(String token) {
        return userEntityModificationRequestService.implementUserEntityModificationRequest(token);
    }
}

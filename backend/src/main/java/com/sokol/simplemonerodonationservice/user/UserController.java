package com.sokol.simplemonerodonationservice.user;

import com.sokol.simplemonerodonationservice.donation.DonationService;
import com.sokol.simplemonerodonationservice.donation.DonationSettingsDataDTO;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final DonationService donationService;

    public UserController(UserService userService, DonationService donationService) {
        this.userService = userService;
        this.donationService = donationService;
    }

    @GetMapping
    public ResponseEntity<UserDataResponseDTO> retrieveUserData(Principal principal) {
        return new ResponseEntity<>(userService.getUserDataByPrincipal(principal.getName()), HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDataResponseDTO> retrieveUserData(@PathVariable String username) {
        return new ResponseEntity<>(userService.getUserDataByUsername(username), HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<DonationUserDataDTO> retrieveDonationUserData(Principal principal) {
        return new ResponseEntity<>(donationService.getDonationUserDataByPrincipal(principal.getName()), HttpStatus.OK);
    }

    @PutMapping("/settings/profile")
    public ResponseEntity<DonationUserDataDTO> editUserDonationData(@RequestBody @Valid DonationUserDataDTO donationUserDataDTO, Principal principal) {
        return new ResponseEntity<>(donationService.modifyDonationUserDataByPrincipal(principal.getName(), donationUserDataDTO), HttpStatus.OK);
    }

    @GetMapping("/settings/donation")
    public ResponseEntity<DonationSettingsDataDTO> getDonationSettingsData(Principal principal) {
        return new ResponseEntity<>(donationService.getDonationSettingsDataDTOByPrincipal(principal.getName()), HttpStatus.OK);
    }

    @PutMapping("/settings/donation/genNewToken")
    public ResponseEntity<DonationSettingsDataDTO> generateNewUserToken(Principal principal) {
        return new ResponseEntity<>(donationService.regenerateDonationToken(principal.getName()), HttpStatus.CREATED);
    }

    @PutMapping("/settings/donation")
    public ResponseEntity<DonationSettingsDataDTO> updateDonationSettingsData(Principal principal, @Valid @RequestBody DonationSettingsDataDTO donationSettingsDataDTO) {
        return new ResponseEntity<>(donationService.updateDonationSettingsData(principal.getName(), donationSettingsDataDTO), HttpStatus.CREATED);
    }
}

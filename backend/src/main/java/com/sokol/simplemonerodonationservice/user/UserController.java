package com.sokol.simplemonerodonationservice.user;

import com.sokol.simplemonerodonationservice.donation.DonationService;
import com.sokol.simplemonerodonationservice.donation.DonationSettingsDataDTO;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataDTO;
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
        UserDataResponseDTO userDataResponseDTO = userService.getUserDataByPrincipal(principal.getName());

        return new ResponseEntity<>(userDataResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDataResponseDTO> retrieveUserData(@PathVariable String username) {
        UserDataResponseDTO userDataResponseDTO = userService.getUserDataByUsername(username);

        return new ResponseEntity<>(userDataResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<DonationUserDataDTO> retrieveDonationUserData(Principal principal) {
        DonationUserDataDTO donationUserDataDTO = donationService.getDonationUserDataByPrincipal(principal.getName());

        return new ResponseEntity<>(donationUserDataDTO, HttpStatus.OK);
    }

    @PutMapping("/settings/profile")
    public ResponseEntity<DonationUserDataDTO> editUserDonationData(@RequestBody DonationUserDataDTO donationUserDataDTO, Principal principal) {
        DonationUserDataDTO body = donationService.modifyDonationUserDataByPrincipal(principal.getName(), donationUserDataDTO);

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/settings/donation")
    public ResponseEntity<DonationSettingsDataDTO> getDonationSettingsData(Principal principal) {
        return new ResponseEntity<>(donationService.getDonationSettingsDataDTOByPrincipal(principal.getName()), HttpStatus.OK);
    }

    @PutMapping("/settings/donation/genNewToken")
    public ResponseEntity<DonationSettingsDataDTO> generateNewUserToken(Principal principal) {
        return new ResponseEntity<>(donationService.regenerateDonationToken(principal.getName()), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity
}

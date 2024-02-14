package com.sokol.simplemonerodonationservice.donation;

import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataDTO;
import com.sokol.simplemonerodonationservice.sse.SseEmitterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/donation")
public class DonationController {
    private final DonationService donationService;
    private final SseEmitterService sseEmitterService;

    public DonationController(DonationService donationService,
                              SseEmitterService sseEmitterService) {
        this.donationService = donationService;
        this.sseEmitterService = sseEmitterService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<DonationDTO>> getAllDonations(Principal principal) {
        return new ResponseEntity<>(donationService.getAllDonations(principal.getName()), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<DonationDTO>> getAllDonationsByPageNum(Principal principal, @RequestParam("page") int pageNum, @RequestParam("itemsPerPage") int itemsPerPage) {
        return new ResponseEntity<>(donationService.getAllDonations(principal.getName(), pageNum, itemsPerPage > 0 ? itemsPerPage : 2), HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getAllDonationsByPageNum(Principal principal) {
        return new ResponseEntity<>(donationService.getDonationCount(principal.getName()), HttpStatus.OK);
    }

    @GetMapping("/test")
    public void sendTestDonation() {
        sseEmitterService.sendTestDonationMessageToAllClients();
    }

    @GetMapping(value = "/emitter", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter donationSseEmitter(@RequestParam("token") String token) {
        if (!donationService.validateToken(token)) throw new AccessDeniedException("Bad token");
        return sseEmitterService.createDonationSseEmitter();
    }

    @GetMapping("/donate/{username}")
    public ResponseEntity<DonationUserDataDTO> fetchDonationUserDataByUsername(@PathVariable String username) {
        DonationUserDataDTO donationUserDataResponseDTO = donationService.getDonationUserDataByUsername(username);

        return new ResponseEntity<>(donationUserDataResponseDTO, HttpStatus.OK);
    }

    @PostMapping("/donate/{username}")
    public ResponseEntity<DonationResponseDTO> fetchMoneroSubbadressByUsername(
            @PathVariable String username,
            @RequestBody DonationRequestDTO donationRequestDTO
    ) {
        DonationResponseDTO body = donationService.implementDonationRequest(donationRequestDTO, username);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}

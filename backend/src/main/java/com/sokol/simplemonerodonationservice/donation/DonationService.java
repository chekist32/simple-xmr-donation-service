package com.sokol.simplemonerodonationservice.donation;

import com.sokol.simplemonerodonationservice.base.exception.ResourceNotFoundException;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataDTO;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataEntity;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataRepository;
import com.sokol.simplemonerodonationservice.monero.MoneroService;
import com.sokol.simplemonerodonationservice.monero.monerosubaddress.MoneroSubaddressEntity;
import com.sokol.simplemonerodonationservice.monero.monerosubaddress.MoneroSubaddressScheduledExecutorService;
import com.sokol.simplemonerodonationservice.payment.PaymentEntity;
import com.sokol.simplemonerodonationservice.payment.PaymentService;
import com.sokol.simplemonerodonationservice.user.UserEntity;
import com.sokol.simplemonerodonationservice.user.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class DonationService {
    private final DonationRepository donationRepository;
    private final MoneroService moneroService;
    private final UserRepository userRepository;
    private final DonationUserDataRepository donationUserDataRepository;
    private final MoneroSubaddressScheduledExecutorService moneroSubaddressScheduledExecutorService;
    private final PaymentService paymentService;

    public DonationService(DonationRepository donationRepository,
                           MoneroService moneroService,
                           UserRepository userRepository,
                           DonationUserDataRepository donationUserDataRepository,
                           MoneroSubaddressScheduledExecutorService moneroSubaddressScheduledExecutorService,
                           PaymentService paymentService) {
        this.donationRepository = donationRepository;
        this.moneroService = moneroService;
        this.userRepository = userRepository;
        this.donationUserDataRepository = donationUserDataRepository;
        this.moneroSubaddressScheduledExecutorService = moneroSubaddressScheduledExecutorService;
        this.paymentService = paymentService;
    }

    public long getDonationCount(String principal) {
        UserEntity user = userRepository.findByPrincipal(principal)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with such principal"));
        return donationRepository.countByUserAndConfirmedAtNotNull(user);
    }

    public List<DonationDTO> getAllDonations(String principal) {
        UserEntity user = userRepository.findByPrincipal(principal)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with such principal"));

        List<DonationEntity> donationEntityList = donationRepository.findByUserAndConfirmedAtNotNull(user);


        return donationEntityList.stream().map(DonationUtils::DonationEntityToDonationDTOMapper).toList();

    }

    public List<DonationDTO> getAllDonations(String principal, int pageNum) {
        return this.getAllDonations(principal, pageNum, 100);
    }

    public List<DonationDTO> getAllDonations(String principal, int pageNum, int pageSize) {
        UserEntity user = userRepository.findByPrincipal(principal)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with such principal"));

        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "confirmedAt"));
        List<DonationEntity> donationEntityList = donationRepository.findByUserAndConfirmedAtNotNull(user, pageable);

        return donationEntityList.stream().map(DonationUtils::DonationEntityToDonationDTOMapper).toList();
    }

    public DonationResponseDTO implementDonationRequest(DonationRequestDTO donationRequestDTO, String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with such username"));

        PaymentEntity payment = paymentService.createPayment();

        DonationEntity createdDonation = new DonationEntity(
                donationRequestDTO.senderUsername(),
                donationRequestDTO.donationText(),
                LocalDateTime.now(ZoneOffset.UTC),
                user,
                payment
        );

        MoneroSubaddressEntity moneroSubaddress = moneroService.getDonationMoneroSubaddress();
        createdDonation.setMoneroSubaddress(moneroSubaddress.getSubaddress());
        donationRepository.save(createdDonation);

        moneroSubaddressScheduledExecutorService.setOccupationTimeout(moneroSubaddress, createdDonation);

        return new DonationResponseDTO(moneroSubaddress.getSubaddress(), payment.getId().toString());
    }

    public DonationUserDataDTO getDonationUserDataByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with such username"));
        DonationUserDataEntity donationUserData = user.getDonationUserData();

        return new DonationUserDataDTO(user.getUsername(), donationUserData.getGreetingText());
    }

    public DonationUserDataDTO getDonationUserDataByPrincipal(String principal) {
        UserEntity user = userRepository.findByPrincipal(principal)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with such principal"));
        DonationUserDataEntity donationUserData = user.getDonationUserData();

        return new DonationUserDataDTO(user.getUsername(), donationUserData.getGreetingText());
    }

    public DonationUserDataDTO modifyDonationUserDataByPrincipal(String principal, DonationUserDataDTO donationUserDataDTO) {
        UserEntity user = userRepository.findByPrincipal(principal)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with such principal"));

        user.setUsername(donationUserDataDTO.username());
        userRepository.save(user);

        DonationUserDataEntity donationUserData = user.getDonationUserData();
        donationUserData.setGreetingText(donationUserDataDTO.greetingText());
        donationUserDataRepository.save(donationUserData);

        return donationUserDataDTO;
    }
    public boolean validateToken(String token) {
        return userRepository.existsByToken(token);
    }

    public DonationSettingsDataDTO getDonationSettingsDataDTOByPrincipal(String principal) {
        UserEntity user = userRepository.findByPrincipal(principal)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with such principal"));

        return new DonationSettingsDataDTO(user.getToken());
    }

}

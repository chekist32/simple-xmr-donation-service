package com.sokol.simplemonerodonationservice.donation;

import com.sokol.simplemonerodonationservice.base.exception.ResourceNotFoundException;
import com.sokol.simplemonerodonationservice.crypto.coin.CoinType;
import com.sokol.simplemonerodonationservice.crypto.payment.*;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataDTO;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataEntity;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataRepository;
import com.sokol.simplemonerodonationservice.crypto.coin.monero.MoneroService;
import com.sokol.simplemonerodonationservice.user.UserEntity;
import com.sokol.simplemonerodonationservice.user.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonationService {
    private final DonationRepository donationRepository;
    private final UserRepository userRepository;
    private final DonationUserDataRepository donationUserDataRepository;
    private final PaymentProcessor paymentProcessor;

    public DonationService(DonationRepository donationRepository,
                           UserRepository userRepository,
                           DonationUserDataRepository donationUserDataRepository,
                           PaymentProcessor paymentProcessor) {
        this.donationRepository = donationRepository;
        this.userRepository = userRepository;
        this.donationUserDataRepository = donationUserDataRepository;
        this.paymentProcessor = paymentProcessor;
    }

    private UserEntity findUserByPrincipal(String principal) {
        return userRepository.findByPrincipal(principal)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with such principal"));
    }

    public long getDonationCount(String principal) {
        return donationRepository.countByUser(findUserByPrincipal(principal));
    }

    public List<DonationDTO> getAllDonations(String principal) {
        return donationRepository.findByUser(findUserByPrincipal(principal))
                .stream()
                .map(DonationUtils::DonationEntityToDonationDTOMapper)
                .toList();
    }

    public List<DonationDTO> getAllDonations(String principal, int pageNum) {
        return this.getAllDonations(principal, pageNum, 100);
    }

    public List<DonationDTO> getAllDonations(String principal, int pageNum, int pageSize) {
        return donationRepository.findByUser(
                    findUserByPrincipal(principal),
                    PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "confirmedAt"))
                )
                .stream()
                .map(DonationUtils::DonationEntityToDonationDTOMapper)
                .toList();
    }

    public DonationResponseDTO implementDonationRequest(DonationRequestDTO donationRequestDTO, String username) {
        UserEntity user = findUserByPrincipal(username);
        DonationUserDataEntity donationUserData = user.getDonationUserData();

        PaymentEntity payment = paymentProcessor.generateCryptoPayment(
                CoinType.valueOf(donationRequestDTO.coinType()),
                PaymentPurposeType.DONATION,
                donationUserData.getMinDonationAmount(),
                donationUserData.getTimeout()
        );

        donationRepository.save(new DonationEntity(
                donationRequestDTO.senderUsername(),
                donationRequestDTO.donationText(),
                user,
                payment
        ));

        return new DonationResponseDTO(payment.getCryptoAddress(), payment.getId().toString());
    }

    public DonationUserDataDTO getDonationUserDataByUsername(String username) {
        UserEntity user = findUserByPrincipal(username);
        DonationUserDataEntity donationUserData = user.getDonationUserData();

        return new DonationUserDataDTO(user.getUsername(), donationUserData.getGreetingText());
    }

    public DonationUserDataDTO getDonationUserDataByPrincipal(String principal) {
        UserEntity user = findUserByPrincipal(principal);
        DonationUserDataEntity donationUserData = user.getDonationUserData();

        return new DonationUserDataDTO(user.getUsername(), donationUserData.getGreetingText());
    }

    public DonationUserDataDTO modifyDonationUserDataByPrincipal(String principal, DonationUserDataDTO donationUserDataDTO) {
        UserEntity user = findUserByPrincipal(principal);

        user.setUsername(donationUserDataDTO.username());
        userRepository.save(user);

        DonationUserDataEntity donationUserData = user.getDonationUserData();
        donationUserData.setGreetingText(donationUserDataDTO.greetingText());
        donationUserDataRepository.save(donationUserData);

        return donationUserDataDTO;
    }

    public boolean validateToken(String token) {
        return donationUserDataRepository.existsByToken(token);
    }

    public DonationSettingsDataDTO getDonationSettingsDataDTOByPrincipal(String principal) {
        UserEntity user = findUserByPrincipal(principal);

        DonationUserDataEntity donationUserData = user.getDonationUserData();

        return DonationUtils.DonationUserDataToDonationSettingsDataDTOMapper(donationUserData);
    }

    public DonationSettingsDataDTO regenerateDonationToken(String principal) {
        UserEntity user = findUserByPrincipal(principal);

        DonationUserDataEntity donationUserData = user.getDonationUserData();
        donationUserData.regenerateToken();
        donationUserDataRepository.save(donationUserData);

        return DonationUtils.DonationUserDataToDonationSettingsDataDTOMapper(donationUserData);
    }

    public DonationSettingsDataDTO updateDonationSettingsData(String principal, DonationSettingsDataDTO donationSettingsDataDTO) {
        UserEntity user = findUserByPrincipal(principal);

        DonationUserDataEntity donationUserData = user.getDonationUserData();
        donationUserData.setMinDonationAmount(donationSettingsDataDTO.minAmount());
        donationUserData.setTimeout(donationUserData.getTimeout());
        donationUserDataRepository.save(donationUserData);

        return DonationUtils.DonationUserDataToDonationSettingsDataDTOMapper(donationUserData);
    }
}

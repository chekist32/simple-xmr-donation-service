package com.sokol.simplemonerodonationservice.monero;

import com.sokol.simplemonerodonationservice.donation.DonationEntity;
import com.sokol.simplemonerodonationservice.donation.DonationRepository;
import com.sokol.simplemonerodonationservice.donation.DonationUtils;
import com.sokol.simplemonerodonationservice.monero.monerosubaddress.MoneroSubaddressRepository;
import com.sokol.simplemonerodonationservice.monero.monerosubaddress.MoneroSubaddressScheduledExecutorService;
import com.sokol.simplemonerodonationservice.payment.PaymentService;
import com.sokol.simplemonerodonationservice.sse.SseEmitterService;
import monero.wallet.model.MoneroOutputWallet;
import monero.wallet.model.MoneroTxWallet;
import monero.wallet.model.MoneroWalletListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Component
public class CustomMoneroWalletListener extends MoneroWalletListener {
    private final SseEmitterService sseEmitterService;
    private final MoneroSubaddressScheduledExecutorService moneroSubaddressScheduledExecutorService;
    private final MoneroSubaddressRepository moneroSubaddressRepository;
    private final DonationRepository donationRepository;
    private final PaymentService paymentService;
    private boolean confirmationRequired;
    private int confirmationNum;
    private BigInteger minAmount;
    @Autowired
    public CustomMoneroWalletListener(SseEmitterService sseEmitterService,
                                      MoneroSubaddressScheduledExecutorService moneroSubaddressScheduledExecutorService,
                                      DonationRepository donationRepository,
                                      MoneroSubaddressRepository moneroSubaddressRepository,
                                      PaymentService paymentService) {
        this(
                sseEmitterService,
                moneroSubaddressScheduledExecutorService,
                moneroSubaddressRepository,
                donationRepository, paymentService,
                false,
                0,
                BigDecimal.valueOf(0.01*MoneroConfig.pp).toBigInteger()
        );
    }
    public CustomMoneroWalletListener(SseEmitterService sseEmitterService,
                                      MoneroSubaddressScheduledExecutorService moneroSubaddressScheduledExecutorService,
                                      MoneroSubaddressRepository moneroSubaddressRepository,
                                      DonationRepository donationRepository,
                                      PaymentService paymentService,
                                      boolean confirmationRequired,
                                      int confirmationNum,
                                      BigInteger minAmount) {
        this.sseEmitterService = sseEmitterService;
        this.moneroSubaddressScheduledExecutorService = moneroSubaddressScheduledExecutorService;
        this.moneroSubaddressRepository = moneroSubaddressRepository;
        this.donationRepository = donationRepository;
        this.paymentService = paymentService;
        this.confirmationRequired = confirmationRequired;
        this.confirmationNum = confirmationNum;
        this.minAmount = minAmount;
    }

    boolean checkRequirements(MoneroTxWallet moneroTxWallet) {
        return moneroTxWallet.isIncoming() &&
                (moneroTxWallet.isConfirmed() == confirmationRequired && moneroTxWallet.getNumConfirmations() == confirmationNum) &&
                !moneroTxWallet.isDoubleSpendSeen() &&
                moneroTxWallet.getIncomingAmount().compareTo(minAmount) != -1;
    }

    @Override
    public void onOutputReceived(MoneroOutputWallet output) {
        MoneroTxWallet moneroTxWallet = output.getTx();
        if (checkRequirements(moneroTxWallet)) {
            moneroTxWallet.getIncomingTransfers().forEach(
                    moneroIncomingTransfer -> {
                        System.out.println(moneroIncomingTransfer);
                        String incomingTransferAddress = moneroIncomingTransfer.getAddress();
                        moneroSubaddressScheduledExecutorService.cancelScheduledTask(incomingTransferAddress);
                        moneroSubaddressRepository.updateIsIdleBySubaddress(incomingTransferAddress, true);
                        sendConfirmedDonationAssociatedWithAddress(
                                incomingTransferAddress,
                                moneroIncomingTransfer.getAmount().doubleValue() / MoneroConfig.pp
                            );
                    });
        }
    }
    public void sendConfirmedDonationAssociatedWithAddress(String moneroAddress, double amount) {
        Optional<DonationEntity> donationEntityOptional = donationRepository.findRelevantDonation(moneroAddress);

        if (donationEntityOptional.isPresent()) {
            DonationEntity donationEntity = donationEntityOptional.get();

            donationEntity.setAmount(amount);
            donationEntity.setConfirmedAt(LocalDateTime.now(ZoneOffset.UTC));
            donationEntity.setIsPaymentConfirmed(true);

            donationRepository.save(donationEntity);

            paymentService.confirmPayment(donationEntity.getPayment());

            sseEmitterService.sendDonationMessageToAllClients(
                    DonationUtils.DonationEntityToDonationDTOMapper(donationEntity)
            );
        }
    }

}

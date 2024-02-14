package com.sokol.simplemonerodonationservice.crypto.monero;

import com.sokol.simplemonerodonationservice.crypto.CryptoConfirmationType;
import com.sokol.simplemonerodonationservice.crypto.monero.monerosubaddress.MoneroSubaddressScheduledExecutorService;
import com.sokol.simplemonerodonationservice.donation.DonationEntity;
import com.sokol.simplemonerodonationservice.donation.DonationRepository;
import com.sokol.simplemonerodonationservice.donation.DonationUtils;
import com.sokol.simplemonerodonationservice.crypto.monero.monerosubaddress.MoneroSubaddressRepository;
import com.sokol.simplemonerodonationservice.payment.PaymentService;
import com.sokol.simplemonerodonationservice.sse.SseEmitterService;
import monero.wallet.model.MoneroOutputWallet;
import monero.wallet.model.MoneroTxWallet;
import monero.wallet.model.MoneroWalletListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

@Component
public class CustomMoneroWalletListener extends MoneroWalletListener {
    private final SseEmitterService sseEmitterService;
    private final MoneroSubaddressScheduledExecutorService moneroSubaddressScheduledExecutorService;
    private final MoneroSubaddressRepository moneroSubaddressRepository;
    private final DonationRepository donationRepository;
    private final PaymentService paymentService;
    private CryptoConfirmationType confirmationType;
    private BigInteger minAmount;

    public CustomMoneroWalletListener(SseEmitterService sseEmitterService,
                                      MoneroSubaddressScheduledExecutorService moneroSubaddressScheduledExecutorService,
                                      DonationRepository donationRepository,
                                      MoneroSubaddressRepository moneroSubaddressRepository,
                                      PaymentService paymentService) {
        this(
                sseEmitterService,
                moneroSubaddressScheduledExecutorService,
                moneroSubaddressRepository,
                donationRepository,
                paymentService,
                CryptoConfirmationType.UNCONFIRMED,
                0.01
        );
    }

    public CustomMoneroWalletListener(SseEmitterService sseEmitterService,
                                      MoneroSubaddressScheduledExecutorService moneroSubaddressScheduledExecutorService,
                                      MoneroSubaddressRepository moneroSubaddressRepository,
                                      DonationRepository donationRepository,
                                      PaymentService paymentService,
                                      CryptoConfirmationType confirmationType,
                                      double minAmount) {
        this.sseEmitterService = sseEmitterService;
        this.moneroSubaddressScheduledExecutorService = moneroSubaddressScheduledExecutorService;
        this.moneroSubaddressRepository = moneroSubaddressRepository;
        this.donationRepository = donationRepository;
        this.paymentService = paymentService;
        this.confirmationType = confirmationType;
        this.updateMinAmount(minAmount);
    }

    private boolean checkRequirements(MoneroTxWallet moneroTxWallet) {
        boolean result = moneroTxWallet.isIncoming() && !moneroTxWallet.isDoubleSpendSeen();

        switch (confirmationType) {
            case UNCONFIRMED -> result &= !moneroTxWallet.isConfirmed();
            case PARTIALLY_CONFIRMED -> result &= moneroTxWallet.isConfirmed() && moneroTxWallet.getNumConfirmations() == 0;
            case FULLY_CONFIRMED -> result &= moneroTxWallet.isConfirmed() && moneroTxWallet.getNumConfirmations() == 10;
            default -> { return false; }
        }

        return result && moneroTxWallet.getIncomingAmount().compareTo(minAmount) != -1;
    }

    @Override
    public void onOutputReceived(MoneroOutputWallet output) {
        MoneroTxWallet moneroTxWallet = output.getTx();
        if (checkRequirements(moneroTxWallet)) {
            moneroTxWallet.getIncomingTransfers().forEach(
                    moneroIncomingTransfer -> {
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

            paymentService.confirmPayment(donationEntity.getPayment(), amount);

            sseEmitterService.sendDonationMessageToAllClients(
                    DonationUtils.DonationEntityToDonationDTOMapper(donationEntity)
            );
        }
    }

    public void updateMinAmount(double minAmount) {
        if (minAmount > 0)
            this.minAmount = BigDecimal.valueOf(minAmount*MoneroConfig.pp).toBigInteger();
    }

    public void updateCryptoConfirmationType(CryptoConfirmationType confirmationType) {
        this.confirmationType = confirmationType;
    }
}

package com.sokol.simplemonerodonationservice.crypto.monero;

import com.sokol.simplemonerodonationservice.donation.DonationRepository;
import com.sokol.simplemonerodonationservice.crypto.monero.monerosubaddress.MoneroSubaddressRepository;
import com.sokol.simplemonerodonationservice.crypto.monero.monerosubaddress.MoneroSubaddressScheduledExecutorService;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataEntity;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataRepository;
import com.sokol.simplemonerodonationservice.payment.PaymentService;
import com.sokol.simplemonerodonationservice.sse.SseEmitterService;
import com.sokol.simplemonerodonationservice.user.UserRepository;
import monero.wallet.MoneroWalletRpc;
import monero.wallet.model.MoneroWalletListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MoneroConfig {
    public static final double pp = Math.pow(10, 12);
    @Value("${monero_wallet_config.wallet_path}")
    private String walletPath;
    @Value("${monero_wallet_config.wallet_password}")
    private String walletPassword;
    @Value("${monero_wallet_config.rpc_server_url}")
    private String rpcServerUrl;
    @Value("${monero_wallet_config.rpc_server_username}")
    private String rpcServerUsername;
    @Value("${monero_wallet_config.rpc_server_password}")
    private String rpcServerPassword;
    private CustomMoneroWalletListener moneroWalletListener;
    private final DonationUserDataRepository donationUserDataRepository;

    public MoneroConfig(SseEmitterService sseEmitterService,
                        MoneroSubaddressScheduledExecutorService moneroSubaddressScheduledExecutorService,
                        DonationRepository donationRepository,
                        PaymentService paymentService,
                        MoneroSubaddressRepository moneroSubaddressRepository,
                        DonationUserDataRepository donationUserDataRepository) {
        this.donationUserDataRepository = donationUserDataRepository;
        this.moneroWalletListener = new CustomMoneroWalletListener(
                sseEmitterService,
                moneroSubaddressScheduledExecutorService,
                donationRepository,
                moneroSubaddressRepository,
                paymentService
        );
    }

    @Bean
    public MoneroWalletRpc moneroWalletRpc() {
        MoneroWalletRpc wallet = new MoneroWalletRpc(rpcServerUrl, rpcServerUsername, rpcServerPassword);
        wallet.openWallet(walletPath, walletPassword);

        if (donationUserDataRepository.count() > 0) {
            DonationUserDataEntity donationUserData = donationUserDataRepository.findAll().iterator().next();
            moneroWalletListener.updateMinAmount(donationUserData.getMinDonationAmount());
            moneroWalletListener.updateCryptoConfirmationType(donationUserData.getConfirmationType());
        }

        wallet.addListener(moneroWalletListener);

        return wallet;
    }
}

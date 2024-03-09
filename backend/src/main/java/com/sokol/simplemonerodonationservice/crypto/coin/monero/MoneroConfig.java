package com.sokol.simplemonerodonationservice.crypto.coin.monero;

import com.sokol.simplemonerodonationservice.crypto.payment.PaymentServiceImpl;
import com.sokol.simplemonerodonationservice.donation.DonationRepository;
import com.sokol.simplemonerodonationservice.crypto.coin.monero.monerosubaddress.MoneroSubaddressRepository;
import com.sokol.simplemonerodonationservice.crypto.coin.monero.monerosubaddress.MoneroSubaddressScheduledExecutorService;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataEntity;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataRepository;
import com.sokol.simplemonerodonationservice.crypto.payment.PaymentService;
import com.sokol.simplemonerodonationservice.sse.SseEmitterService;
import monero.daemon.MoneroDaemonRpc;
import monero.daemon.model.MoneroNetworkType;
import monero.wallet.MoneroWalletFull;
import monero.wallet.MoneroWalletRpc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    private MoneroListener moneroListener;
    private final DonationUserDataRepository donationUserDataRepository;

    public MoneroConfig(DonationUserDataRepository donationUserDataRepository,
                        ApplicationEventPublisher applicationEventPublisher) {
        this.donationUserDataRepository = donationUserDataRepository;
        this.moneroListener = new MoneroListener(applicationEventPublisher);
    }

    @Bean
    public MoneroWalletRpc moneroWalletRpc() {

        MoneroWalletRpc wallet = new MoneroWalletRpc(rpcServerUrl, rpcServerUsername, rpcServerPassword);
        wallet.openWallet(walletPath, walletPassword);

        if (donationUserDataRepository.count() > 0) {
            DonationUserDataEntity donationUserData = donationUserDataRepository.findAll().iterator().next();
            moneroListener.updateCryptoConfirmationType(donationUserData.getConfirmationType());
        }

        wallet.addListener(moneroListener);

        return wallet;
    }
}

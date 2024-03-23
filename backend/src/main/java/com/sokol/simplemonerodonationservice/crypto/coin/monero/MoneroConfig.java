package com.sokol.simplemonerodonationservice.crypto.coin.monero;

import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataEntity;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataRepository;
import monero.daemon.model.MoneroNetworkType;
import monero.wallet.MoneroWalletFull;
import monero.wallet.MoneroWalletRpc;
import monero.wallet.model.MoneroWalletListener;
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
    private final MoneroListener moneroListener;
    private final DonationUserDataRepository donationUserDataRepository;

    public MoneroConfig(DonationUserDataRepository donationUserDataRepository,
                        ApplicationEventPublisher applicationEventPublisher) {
        this.donationUserDataRepository = donationUserDataRepository;
        this.moneroListener = new MoneroListener(applicationEventPublisher);
    }

    @Bean
    public MoneroWalletFull moneroWalletRpc() {
        MoneroWalletFull wallet = MoneroWalletFull.openWallet(walletPath, walletPassword, MoneroNetworkType.STAGENET, "http://stagenet.community.rino.io:38081");

        if (donationUserDataRepository.count() > 0) {
            DonationUserDataEntity donationUserData = donationUserDataRepository.findAll().iterator().next();
            moneroListener.updateCryptoConfirmationType(donationUserData.getConfirmationType());
        }

        wallet.sync(new MoneroWalletListener() {
            @Override
            public void onSyncProgress(long height, long startHeight, long endHeight, double percentDone, String message) {
                System.out.println("Height: "+height+"\nPerecentDone: "+percentDone);
            }
        });
        wallet.save();

        wallet.startSyncing(5000L);

        wallet.addListener(moneroListener);

        return wallet;
    }
}

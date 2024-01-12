package com.sokol.simplemonerodonationservice.monero;

import com.sokol.simplemonerodonationservice.donation.DonationRepository;
import com.sokol.simplemonerodonationservice.monero.monerosubaddress.MoneroSubaddressRepository;
import com.sokol.simplemonerodonationservice.monero.monerosubaddress.MoneroSubaddressScheduledExecutorService;
import com.sokol.simplemonerodonationservice.payment.PaymentService;
import com.sokol.simplemonerodonationservice.sse.SseEmitterService;
import monero.wallet.MoneroWalletRpc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MoneroConfig {
    public static double pp =  Math.pow(10, 12);
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
    private float minAmount = 0.01F;
    private boolean confirmationRequired = false;
    private final SseEmitterService sseEmitterService;
    private final MoneroSubaddressScheduledExecutorService moneroSubaddressScheduledExecutorService;
    private final DonationRepository donationRepository;
    private final PaymentService paymentService;
    private final MoneroSubaddressRepository moneroSubaddressRepository;

    public MoneroConfig(SseEmitterService sseEmitterService,
                        MoneroSubaddressScheduledExecutorService moneroSubaddressScheduledExecutorService,
                        DonationRepository donationRepository,
                        PaymentService paymentService,
                        MoneroSubaddressRepository moneroSubaddressRepository) {
        this.sseEmitterService = sseEmitterService;
        this.moneroSubaddressScheduledExecutorService = moneroSubaddressScheduledExecutorService;
        this.donationRepository = donationRepository;
        this.paymentService = paymentService;
        this.moneroSubaddressRepository = moneroSubaddressRepository;
    }

    @Bean
    public MoneroWalletRpc moneroWalletRpc() {
        MoneroWalletRpc wallet = new MoneroWalletRpc(rpcServerUrl, rpcServerUsername, rpcServerPassword);
        wallet.openWallet(walletPath, walletPassword);

        wallet.addListener(new CustomMoneroWalletListener(
                sseEmitterService,
                moneroSubaddressScheduledExecutorService,
                donationRepository,
                moneroSubaddressRepository,
                paymentService)
        );

        return wallet;
    }

}

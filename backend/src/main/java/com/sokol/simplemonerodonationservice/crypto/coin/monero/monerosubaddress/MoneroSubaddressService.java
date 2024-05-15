package com.sokol.simplemonerodonationservice.crypto.coin.monero.monerosubaddress;

import com.sokol.simplemonerodonationservice.base.exception.ResourceNotFoundException;
import com.sokol.simplemonerodonationservice.crypto.CryptoConfirmationType;
import com.sokol.simplemonerodonationservice.crypto.coin.CoinType;
import com.sokol.simplemonerodonationservice.crypto.coin.monero.MoneroListener;
import com.sokol.simplemonerodonationservice.crypto.coin.monero.MoneroUtils;
import com.sokol.simplemonerodonationservice.crypto.payment.event.ConfirmedPaymentEvent;
import com.sokol.simplemonerodonationservice.crypto.payment.event.ExpiredPaymentEvent;
import com.sokol.simplemonerodonationservice.crypto.payment.event.PaymentEvent;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import monero.wallet.MoneroWalletRpc;
import monero.wallet.model.MoneroAccount;
import monero.wallet.model.MoneroSubaddress;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MoneroSubaddressService {
    private final MoneroSubaddressRepository moneroSubaddressRepository;
    private final MoneroWalletRpc wallet;
    private final ApplicationEventPublisher applicationEventPublisher;

    public MoneroSubaddressService(MoneroSubaddressRepository moneroSubaddressRepository,
                                   MoneroWalletRpc wallet,
                                   ApplicationEventPublisher applicationEventPublisher) {
        this.moneroSubaddressRepository = moneroSubaddressRepository;
        this.wallet = wallet;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @PostConstruct
    public void init() {
        moneroSubaddressRepository.deleteAll();

        String primaryAddress = wallet.getAccount(0).getPrimaryAddress();

        List<MoneroSubaddress> moneroSubaddresses = wallet.getSubaddresses(0);

        if (!wallet.getSubaddresses(0).isEmpty()) {
            List<MoneroSubaddressEntity> moneroSubaddressEntities = moneroSubaddresses
                            .stream()
                            .map(moneroSubaddress -> MoneroUtils.MoneroSubaddressToMoneroSubaddressEntityMapper(moneroSubaddress, primaryAddress))
                            .toList();

            moneroSubaddressRepository.saveAll(moneroSubaddressEntities);
        } else createDonationMoneroSubaddress(0);
    }

    public MoneroSubaddressEntity getIdleDonationMoneroSubaddress(int accountId) {
        return this.getIdleDonationMoneroSubaddress(wallet.getAccount(accountId));
    }

    public MoneroSubaddressEntity getIdleDonationMoneroSubaddress(String primaryAddress) {
        MoneroAccount moneroAccount = wallet.getAccounts()
                .stream()
                .filter(account -> account.getPrimaryAddress().equals(primaryAddress))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("There is no account associated with this primary address: " + primaryAddress));

        return this.getIdleDonationMoneroSubaddress(moneroAccount);
    }

    @Transactional
    public MoneroSubaddressEntity getIdleDonationMoneroSubaddress(MoneroAccount account) {
        MoneroSubaddressEntity IdleDonationMoneroSubaddress =
                moneroSubaddressRepository.findFirstByPrimaryAddressAndIsIdleTrue(account.getPrimaryAddress())
                        .orElseGet(() -> createDonationMoneroSubaddress(account.getIndex()));

        moneroSubaddressRepository.updateIsIdleBySubaddress(IdleDonationMoneroSubaddress.getSubaddress(), false);

        return IdleDonationMoneroSubaddress;
    }

    public List<MoneroSubaddressEntity> getAllDonationMoneroSubaddresses(int accountId) {
        return this.getAllDonationMoneroSubaddresses(wallet.getAccount(accountId).getPrimaryAddress());
    }

    public List<MoneroSubaddressEntity> getAllDonationMoneroSubaddresses(String primaryAddress) {
        return moneroSubaddressRepository.findAllByPrimaryAddress(primaryAddress);
    }

    public MoneroSubaddressEntity createDonationMoneroSubaddress(int accountId) {
        MoneroSubaddress createdSubaddress = wallet.createSubaddress(accountId);
        String primaryAddress = wallet.getAccount(accountId).getPrimaryAddress();
        Set<String> subaddresses = moneroSubaddressRepository
                .findAllByPrimaryAddress(primaryAddress)
                .stream()
                .map(MoneroSubaddressEntity::getSubaddress)
                .collect(Collectors.toSet());

        while (subaddresses.contains(createdSubaddress.getAddress()))
            createdSubaddress = wallet.createSubaddress(accountId);

        MoneroSubaddressEntity createdMoneroSubaddressEntity =
                new MoneroSubaddressEntity(
                        createdSubaddress.getAddress(),
                        primaryAddress,
                        createdSubaddress.getIndex()
                );

        return moneroSubaddressRepository.save(createdMoneroSubaddressEntity);
    }

    public void setNewListener(CryptoConfirmationType confirmationType) {
        wallet.getListeners().forEach(wallet::removeListener);
        wallet.addListener(new MoneroListener(confirmationType, applicationEventPublisher));
    }

    @EventListener(classes = {ConfirmedPaymentEvent.class, ExpiredPaymentEvent.class})
    @Transactional
    protected void handlePaymentEvent(PaymentEvent paymentEvent) {
        if (paymentEvent.getPayment().getCoinType() == CoinType.XMR)
            moneroSubaddressRepository.updateIsIdleBySubaddress(
                paymentEvent.getPayment().getCryptoAddress(),
                true
            );
    }
}

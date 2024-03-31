package com.sokol.simplemonerodonationservice.crypto.payment;

import com.sokol.simplemonerodonationservice.base.exception.ResourceNotFoundException;
import com.sokol.simplemonerodonationservice.crypto.CryptoTransfer;
import com.sokol.simplemonerodonationservice.crypto.IncomingCryptoTransactionEvent;
import com.sokol.simplemonerodonationservice.crypto.coin.CoinType;
import com.sokol.simplemonerodonationservice.currencyconverter.CryptoCurrencyConverter;
import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.*;

@Service
public class PaymentProcessor {
    private final PaymentService paymentService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static final ConcurrentHashMap<UUID, ScheduledFuture<Void>> pendingPayments = new ConcurrentHashMap<>();

    public PaymentProcessor(PaymentService paymentService,
                            ApplicationEventPublisher applicationEventPublisher) {
        this.paymentService = paymentService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @PostConstruct
    public void init() {
        paymentService.findAllPendingPayments()
                .forEach(paymentService::expirePayment);
    }

    public PaymentEntity generateCryptoPayment(CoinType coinType, PaymentPurposeType paymentPurpose, double requiredAmount, long timeout) {
        PaymentEntity payment = paymentService.generateCryptoPayment(
                coinType,
                paymentPurpose,
                CryptoCurrencyConverter.convertUsdToCrypto(requiredAmount, coinType)
        );

        processPayment(payment, timeout);

        return payment;
    }


    private void processPayment(PaymentEntity payment, long timeout) {
        ScheduledFuture<Void> scheduledTask = (ScheduledFuture<Void>) executorService.schedule(
                () -> handleExpiredPayment(payment.getId()),
                timeout,
                TimeUnit.MILLISECONDS
        );

        pendingPayments.put(payment.getId(), scheduledTask);
    }


    private void handleExpiredPayment(UUID paymentId) {
        try {
            PaymentEntity payment = paymentService.findPaymentById(paymentId);
            paymentService.expirePayment(payment);

            removeScheduleTask(paymentId);

            applicationEventPublisher.publishEvent(new ExpiredPaymentEvent(this.getClass(), payment));
        } catch (ResourceNotFoundException ignored) { }
    }

    private void removeScheduleTask(UUID paymentId) {
        if (pendingPayments.containsKey(paymentId)) {
            pendingPayments.get(paymentId).cancel(true);
            pendingPayments.remove(paymentId);
        }
    }

    private boolean checkRequirements(PaymentEntity payment, CryptoTransfer cryptoTransfer) {
        return cryptoTransfer.amount() >= payment.getRequiredAmount();
    }

    @EventListener(classes = IncomingCryptoTransactionEvent.class)
    protected void handleIncomingCryptoTransactionEvent(IncomingCryptoTransactionEvent incomingCryptoTransactionEvent) {
        try {
            CryptoTransfer cryptoTransfer = incomingCryptoTransactionEvent.getCryptoTransfer();
            PaymentEntity payment = paymentService.findPendingPaymentByCryptoAddress(cryptoTransfer.cryptoAddressTo());

            if (!checkRequirements(payment, cryptoTransfer))
                return; // TODO;

            paymentService.confirmPayment(
                    payment,
                    cryptoTransfer.amount()
            );
            removeScheduleTask(payment.getId());

            applicationEventPublisher.publishEvent(new ConfirmedPaymentEvent(this.getClass(), payment));
        } catch (ResourceNotFoundException ignored) { }
    }



}

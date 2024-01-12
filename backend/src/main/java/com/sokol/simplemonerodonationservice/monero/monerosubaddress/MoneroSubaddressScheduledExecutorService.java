package com.sokol.simplemonerodonationservice.monero.monerosubaddress;

import com.sokol.simplemonerodonationservice.donation.DonationEntity;
import com.sokol.simplemonerodonationservice.donation.DonationRepository;
import com.sokol.simplemonerodonationservice.payment.PaymentService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class MoneroSubaddressScheduledExecutorService {
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private Map<String, ScheduledFuture<MoneroSubaddressEntity>> scheduledTasks = new HashMap<>();
    private final MoneroSubaddressRepository moneroSubaddressRepository;
    private final PaymentService paymentService;
    private final DonationRepository donationRepository;

    public MoneroSubaddressScheduledExecutorService(MoneroSubaddressRepository moneroSubaddressRepository,
                                                    PaymentService paymentService,
                                                    DonationRepository donationRepository) {
        this.moneroSubaddressRepository = moneroSubaddressRepository;
        this.paymentService = paymentService;
        this.donationRepository = donationRepository;
    }

    public void setOccupationTimeout(MoneroSubaddressEntity moneroSubaddress, DonationEntity donationEntity) {
        this.setOccupationTimeout(moneroSubaddress, donationEntity, 40, TimeUnit.MINUTES);
    }
    public void setOccupationTimeout(MoneroSubaddressEntity moneroSubaddress, DonationEntity donationEntity, long delay, TimeUnit timeUnit) {
        ScheduledFuture<MoneroSubaddressEntity> newScheduledTask = (ScheduledFuture<MoneroSubaddressEntity>) executorService.schedule(
                () -> {
                    moneroSubaddressRepository.updateIsIdleById(moneroSubaddress.getId(), true);
                    donationRepository.updateIsPaymentExpiredById(donationEntity.getId(), true);
                    paymentService.expirePayment(donationEntity.getPayment());
                    scheduledTasks.remove(moneroSubaddress.getSubaddress());
                },
                delay,
                timeUnit
        );
        scheduledTasks.put(moneroSubaddress.getSubaddress(), newScheduledTask);
    }

    public void cancelScheduledTask(String moneroSubaddress) {
        if (scheduledTasks.containsKey(moneroSubaddress)) {
            scheduledTasks.get(moneroSubaddress).cancel(true);
            scheduledTasks.remove(moneroSubaddress);
        }
    }


}

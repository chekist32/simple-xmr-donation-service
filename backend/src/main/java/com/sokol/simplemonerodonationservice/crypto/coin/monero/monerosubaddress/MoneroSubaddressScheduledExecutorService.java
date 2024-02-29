package com.sokol.simplemonerodonationservice.crypto.coin.monero.monerosubaddress;

import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataRepository;
import com.sokol.simplemonerodonationservice.payment.PaymentEntity;
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
    private int timeout;

    public MoneroSubaddressScheduledExecutorService(MoneroSubaddressRepository moneroSubaddressRepository,
                                                    DonationUserDataRepository donationUserDataRepository,
                                                    PaymentService paymentService) {
        this.moneroSubaddressRepository = moneroSubaddressRepository;
        this.paymentService = paymentService;
        if (donationUserDataRepository.count() > 0)
            this.updateTimeout(donationUserDataRepository.findAll().iterator().next().getTimeout());
        else this.updateTimeout(40 * 60);

    }

    public void setOccupationTimeout(MoneroSubaddressEntity moneroSubaddress, PaymentEntity paymentEntity) {
        this.setOccupationTimeout(moneroSubaddress, paymentEntity, timeout, TimeUnit.SECONDS);
    }
    public void setOccupationTimeout(MoneroSubaddressEntity moneroSubaddress, PaymentEntity paymentEntity, long delay, TimeUnit timeUnit) {
        ScheduledFuture<MoneroSubaddressEntity> newScheduledTask = (ScheduledFuture<MoneroSubaddressEntity>) executorService.schedule(
                () -> {
                    moneroSubaddressRepository.updateIsIdleBySubaddress(moneroSubaddress.getSubaddress(), true);
                    paymentService.expirePayment(paymentEntity);
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

    public void updateTimeout(int timeout) {
        this.timeout = timeout > 0 ? timeout : 40*60;
    }

}

package com.sokol.simplemonerodonationservice.donation.notification;

import com.sokol.simplemonerodonationservice.crypto.coin.CoinType;
import com.sokol.simplemonerodonationservice.donation.dto.DonationDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class SSEDonationNotificationServiceImpl implements SSEDonationNotificationService {
    private static final long SSE_TIMEOUT = 24 * 60 * 60 * 1000;
    private static final long KEEP_ALIVE_TIMEOUT = 40 * 1000;

    private final SseEmitter.SseEventBuilder keepAliveSseEventBuilder = SseEmitter.event().name("keep-alive").data("keep-alive");
    private final ConcurrentLinkedQueue<SseEmitter> inMemoryDonationSseEmitterList = new ConcurrentLinkedQueue<>();
    private final ScheduledExecutorService keepAliveExecutor = new ScheduledThreadPoolExecutor(1);


    public SSEDonationNotificationServiceImpl() {
        keepAliveExecutor.scheduleAtFixedRate(
                this::sendKeepAliveMessage,
                0,
                KEEP_ALIVE_TIMEOUT,
                TimeUnit.MILLISECONDS
        );
    }

    public SseEmitter createDonationSseEmitter() {
        SseEmitter sseEmitter = new SseEmitter(SSE_TIMEOUT);

        inMemoryDonationSseEmitterList.add(sseEmitter);

        sseEmitter.onTimeout(() -> inMemoryDonationSseEmitterList.remove(sseEmitter));
        sseEmitter.onCompletion(() -> inMemoryDonationSseEmitterList.remove(sseEmitter));
        sseEmitter.onError((throwable) -> inMemoryDonationSseEmitterList.remove(sseEmitter));

        return sseEmitter;
    }

    private void sendKeepAliveMessage() {
        sendMessageToAllClients(keepAliveSseEventBuilder);
    }

    public void sendTestDonationMessageToAllClients() {
        sendDonationMessageToAllClients(new DonationDTO(
                "Username",
                1,
                CoinType.XMR.name(),
                "This is a test (Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis)",
                LocalDateTime.now(ZoneOffset.UTC)
        ));
    }

    public void sendDonationMessageToAllClients(DonationDTO donationDTO) {
        sendMessageToAllClients(donationDTO);
    }

    private void sendMessageToAllClients(Object msg) {
        for (SseEmitter sseEmitter : inMemoryDonationSseEmitterList) {
            try { sseEmitter.send(msg); }
            catch (IOException e) { removeDonationSseEmitter(sseEmitter); }
        }
    }
    private void sendMessageToAllClients(SseEmitter.SseEventBuilder sseEventBuilder) {
        for (SseEmitter sseEmitter : inMemoryDonationSseEmitterList) {
            try { sseEmitter.send(sseEventBuilder); }
            catch (IOException e) { removeDonationSseEmitter(sseEmitter); }
        }
    }

    private void removeDonationSseEmitter(SseEmitter sseEmitter) {
        sseEmitter.complete();
        inMemoryDonationSseEmitterList.remove(sseEmitter);
    }
}
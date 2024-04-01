package com.sokol.simplemonerodonationservice.sse;

import com.sokol.simplemonerodonationservice.crypto.coin.CoinType;
import com.sokol.simplemonerodonationservice.donation.DonationDTO;
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
public class SseEmitterService {
    private static final SseEmitter.SseEventBuilder keepAliveSseEventBuilder = SseEmitter.event().name("keep-alive").data("keep-alive");
    private static final ConcurrentLinkedQueue<SseEmitter> inMemoryDonationSseEmitterList = new ConcurrentLinkedQueue<>();
    private static final ScheduledExecutorService keepAliveExecutor = new ScheduledThreadPoolExecutor(1);

    static {
        keepAliveExecutor.scheduleAtFixedRate(SseEmitterService::sendKeepAliveMessage,0, SseServiceConfig.KEEP_ALIVE_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private SseEmitterService() { }

    public static SseEmitter createDonationSseEmitter() {
        SseEmitter sseEmitter = new SseEmitter(SseServiceConfig.SSE_TIMEOUT);

        inMemoryDonationSseEmitterList.add(sseEmitter);

        sseEmitter.onTimeout(() -> inMemoryDonationSseEmitterList.remove(sseEmitter));
        sseEmitter.onCompletion(() -> inMemoryDonationSseEmitterList.remove(sseEmitter));
        sseEmitter.onError((throwable) -> inMemoryDonationSseEmitterList.remove(sseEmitter));

        return sseEmitter;
    }

    private static void sendKeepAliveMessage() {
        sendMessageToAllClients(keepAliveSseEventBuilder);
    }

    public static void sendTestDonationMessageToAllClients() {
        sendDonationMessageToAllClients(new DonationDTO(
                "Username",
                1,
                CoinType.XMR.name(),
                "This is a test (Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis)",
                LocalDateTime.now(ZoneOffset.UTC)
        ));
    }

    public static void sendDonationMessageToAllClients(DonationDTO donationDTO) {
        sendMessageToAllClients(donationDTO);
    }

    private static void sendMessageToAllClients(Object msg) {
        for (SseEmitter sseEmitter : inMemoryDonationSseEmitterList) {
            try {
                sseEmitter.send(msg);
            } catch (IOException e) {
                removeDonationSseEmitter(sseEmitter);
            }
        }
    }
    private static void sendMessageToAllClients(SseEmitter.SseEventBuilder sseEventBuilder) {
        for (SseEmitter sseEmitter : inMemoryDonationSseEmitterList) {
            try {
                sseEmitter.send(sseEventBuilder);
            } catch (IOException e) {
                removeDonationSseEmitter(sseEmitter);
            }
        }
    }

    private static void removeDonationSseEmitter(SseEmitter sseEmitter) {
        sseEmitter.complete();
        inMemoryDonationSseEmitterList.remove(sseEmitter);
    }
}
package com.sokol.simplemonerodonationservice.sse;

import com.sokol.simplemonerodonationservice.donation.DonationDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class SseEmitterService {
    private final long SSE_TIMEOUT = 24 * 60 * 60 * 1000;
    private final SseEmitter.SseEventBuilder keepAliveSseEventBuilder = SseEmitter.event().name("keep-alive").data("keep-alive");
    private final CopyOnWriteArrayList<SseEmitter> inMemoryDonationSseEmitterList = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService keepAliveExecutor = new ScheduledThreadPoolExecutor(1);

    public SseEmitterService() {
        keepAliveExecutor.scheduleAtFixedRate(this::sendKeepAliveMessage,0, 40, TimeUnit.SECONDS);
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
        this.sendMessageToAllClients(inMemoryDonationSseEmitterList, keepAliveSseEventBuilder);
    }

    public void sendTestDonationMessageToAllClients() {
        this.sendDonationMessageToAllClients(new DonationDTO(
                "Username",
                "1 XMR",
                "This is a test (Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis)",
                LocalDateTime.now(ZoneOffset.UTC)
        ));
    }

    public void sendDonationMessageToAllClients(DonationDTO donationDTO) {
        this.sendMessageToAllClients(inMemoryDonationSseEmitterList, donationDTO);
    }

    private void sendMessageToAllClients(CopyOnWriteArrayList<SseEmitter> sseEmitters, Object msg) {
        for (SseEmitter sseEmitter : sseEmitters) {
            try {
                sseEmitter.send(msg);
            } catch (IOException e) {
                e.printStackTrace();
                removeDonationSseEmitter(sseEmitter);
            }
        }
    }
    private void sendMessageToAllClients(CopyOnWriteArrayList<SseEmitter> sseEmitters, SseEmitter.SseEventBuilder sseEventBuilder) {
        for (SseEmitter sseEmitter : sseEmitters) {
            try {
                sseEmitter.send(sseEventBuilder);
            } catch (IOException e) {
                e.printStackTrace();
                removeDonationSseEmitter(sseEmitter);
            }
        }
    }


    private void removeDonationSseEmitter(SseEmitter sseEmitter) {
        sseEmitter.complete();
        inMemoryDonationSseEmitterList.remove(sseEmitter);
    }
}
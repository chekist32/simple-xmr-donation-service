package com.sokol.simplemonerodonationservice.donation.notification;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SSEDonationNotificationService extends DonationNotificationService {
    SseEmitter createDonationSseEmitter();
}
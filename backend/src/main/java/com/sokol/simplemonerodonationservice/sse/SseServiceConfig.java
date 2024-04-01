package com.sokol.simplemonerodonationservice.sse;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SseServiceConfig {
    public static final long SSE_TIMEOUT = 24 * 60 * 60 * 1000;
    public static final long KEEP_ALIVE_TIMEOUT = 40 * 1000;
}

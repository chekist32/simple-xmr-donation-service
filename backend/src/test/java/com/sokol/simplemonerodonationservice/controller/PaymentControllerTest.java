package com.sokol.simplemonerodonationservice.controller;

import com.sokol.simplemonerodonationservice.crypto.coin.CoinType;
import com.sokol.simplemonerodonationservice.crypto.payment.*;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

@WebMvcTest(value = PaymentController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class PaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Test
    public void getPaymentStatus_ShouldReturnDeferredResult() throws Exception {
        UUID test = UUID.randomUUID();
        when(paymentService.findPaymentById(test))
                .thenReturn(new PaymentEntity("crypto", CoinType.XMR, PaymentPurposeType.DONATION));

        MvcResult result1 = mockMvc.perform(
                get("/api/payment/{paymentId}/status", test)
        ).andReturn();

        MvcResult result2 = mockMvc.perform(
                get("/api/payment/{paymentId}/status", test)
        ).andReturn();

        MockHttpServletResponse response1 = result1.getResponse();
        MockHttpServletResponse response2 = result2.getResponse();

        assertEquals(HttpStatus.OK.value(), response1.getStatus());
        assertEquals(response1.getStatus(), response2.getStatus());
    }

    @Test
    public void getPaymentStatus_ShouldReturnDeferredResultWithStatusOk() throws Exception {
        UUID test = UUID.randomUUID();
        PaymentEntity payment = new PaymentEntity("crypto", CoinType.XMR, PaymentPurposeType.DONATION);
        payment.setPaymentStatus(PaymentStatus.CONFIRMED);

        when(paymentService.findPaymentById(test))
                .thenReturn(payment);

        MvcResult result1 = mockMvc.perform(
                get("/api/payment/{paymentId}/status", test)
        ).andReturn();

        MvcResult result2 = mockMvc.perform(
                get("/api/payment/{paymentId}/status", test)
        ).andReturn();

        ResponseEntity<Object> response1 = (ResponseEntity<Object>) result1.getAsyncResult();
        ResponseEntity<Object> response2 = (ResponseEntity<Object>) result2.getAsyncResult();

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(response1.getStatusCode(), response2.getStatusCode());
        assertEquals("Payment has been confirmed", response1.getBody());
        assertEquals(response1.getBody(), response2.getBody());
    }

    @Test
    public void getPaymentStatus_ShouldReturnDeferredResultWithStatusRequestTimeout() throws Exception {
        UUID test = UUID.randomUUID();
        PaymentEntity payment = new PaymentEntity("crypto", CoinType.XMR, PaymentPurposeType.DONATION);
        payment.setPaymentStatus(PaymentStatus.EXPIRED);

        when(paymentService.findPaymentById(test))
                .thenReturn(payment);

        MvcResult result1 = mockMvc.perform(
                get("/api/payment/{paymentId}/status", test)
        ).andReturn();

        MvcResult result2 = mockMvc.perform(
                get("/api/payment/{paymentId}/status", test)
        ).andReturn();

        ResponseEntity<Object> response1 = (ResponseEntity<Object>) result1.getAsyncResult();
        ResponseEntity<Object> response2 = (ResponseEntity<Object>) result2.getAsyncResult();

        assertEquals(HttpStatus.REQUEST_TIMEOUT, response1.getStatusCode());
        assertEquals(response1.getStatusCode(), response2.getStatusCode());
        assertEquals("Payment has been expired", response1.getBody());
        assertEquals(response1.getBody(), response2.getBody());
    }

}

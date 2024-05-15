package com.sokol.simplemonerodonationservice.repository;

import com.sokol.simplemonerodonationservice.crypto.coin.CoinType;
import com.sokol.simplemonerodonationservice.crypto.payment.PaymentEntity;
import com.sokol.simplemonerodonationservice.crypto.payment.PaymentPurposeType;
import com.sokol.simplemonerodonationservice.crypto.payment.PaymentRepository;
import com.sokol.simplemonerodonationservice.crypto.payment.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase
class PaymentRepositoryIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    private static final List<PaymentEntity> payments = new ArrayList<>();

    static {
        payments.add(
                new PaymentEntity("cryptoAddress1", 10, CoinType.XMR, PaymentPurposeType.DONATION)
        );
        payments.add(
                new PaymentEntity("cryptoAddress1", 20, CoinType.XMR, PaymentPurposeType.DONATION)
        );
        payments.add(
                new PaymentEntity("cryptoAddress2", 15, CoinType.XMR, PaymentPurposeType.DONATION)
        );
    }


    @BeforeEach
    protected void init() {
        paymentRepository.saveAll(payments);
    }

    @Test
    public void findPendingPaymentByCryptoAddress_ShouldReturnPaymentEntityWithRequiredAmount20() {
        PaymentEntity payment = paymentRepository.findPendingPaymentByCryptoAddress(payments.get(0).getCryptoAddress()).get();

        assertNotNull(payment);
        assertEquals(payment.getPaymentStatus(), PaymentStatus.PENDING);

        assertEquals(payment.getCryptoAddress(), payments.get(1).getCryptoAddress());
        assertEquals(payment.getRequiredAmount(), payments.get(1).getRequiredAmount());
    }


}

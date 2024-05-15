package com.sokol.simplemonerodonationservice.repository;

import com.sokol.simplemonerodonationservice.crypto.coin.CoinType;
import com.sokol.simplemonerodonationservice.crypto.payment.PaymentEntity;
import com.sokol.simplemonerodonationservice.crypto.payment.PaymentPurposeType;
import com.sokol.simplemonerodonationservice.crypto.payment.PaymentStatus;
import com.sokol.simplemonerodonationservice.donation.DonationEntity;
import com.sokol.simplemonerodonationservice.donation.DonationRepository;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataEntity;
import com.sokol.simplemonerodonationservice.user.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase
class DonationRepositoryIntegrationTest {
    @Autowired
    private DonationRepository donationRepository;
    @Autowired
    private TestEntityManager entityManager;

    private final List<UserEntity> users = new ArrayList<>();
    private final List<PaymentEntity> payments = new ArrayList<>();
    private final List<DonationEntity> donations= new ArrayList<>();

    @BeforeEach
    protected void init() {
        users.clear();
        payments.clear();
        donations.clear();

        UserEntity user1 = new UserEntity("email1", "username1", "pass1");
        UserEntity user2 = new UserEntity("email2", "username2", "pass2");

        user1.setDonationUserData(entityManager.persistAndFlush(new DonationUserDataEntity()));
        user2.setDonationUserData(entityManager.persistAndFlush(new DonationUserDataEntity()));
        users.add(entityManager.persist(user1)); users.add(entityManager.persist(user2)); entityManager.flush();

        payments.add(
                entityManager.persist(new PaymentEntity(
                        "cryptoaddress1",
                        10,
                        CoinType.XMR,
                        PaymentPurposeType.DONATION
                )));
        payments.add(
                entityManager.persist(new PaymentEntity(
                        "cryptoaddress2",
                        20,
                        CoinType.XMR,
                        PaymentPurposeType.DONATION
                )));
        payments.add(
                entityManager.persist(new PaymentEntity(
                        "cryptoaddress3",
                        30,
                        CoinType.XMR,
                        PaymentPurposeType.DONATION
                )));
        entityManager.flush();

        donations.add(
                entityManager.persist(new DonationEntity(
                        "sender1",
                        "donation1",
                        user1,
                        payments.get(0)
                )));
        donations.add(
                entityManager.persist(new DonationEntity(
                        "sender2",
                        "donation2",
                        user2,
                        payments.get(1)
                )));
        donations.add(
                entityManager.persist(new DonationEntity(
                        "sender3",
                        "donation3",
                        user1,
                        payments.get(2)
                )));
        entityManager.flush();
    }


    @Test
    public void findDonationByPayment_ShouldReturnDonationEntity() {
        int index = (int)(Math.random() * 1000) % payments.size();
        PaymentEntity payment = payments.get(index);

        DonationEntity donation = donationRepository.findDonationByPayment(payment).get();

        assertNotNull(donation);
        assertEquals(payment, donation.getPayment());
        assertEquals("donation"+(index+1), donation.getDonationText());
        assertEquals("sender"+(index+1), donation.getSenderUsername());
    }

    @Test
    public void findByUser_ShouldReturn0Elements() {
        int index = (int)(Math.random() * 1000) % users.size();
        UserEntity user = users.get(index);

        List<DonationEntity> donations = donationRepository.findByUser(user);

        assertEquals(0, donations.size());
    }

    @Test
    public void findByUser_ShouldReturn1Element() {
        PaymentEntity payment = entityManager.find(PaymentEntity.class, payments.get(0).getId());
        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        entityManager.persistAndFlush(payment);

        UserEntity user = users.get(0);

        List<DonationEntity> donations = donationRepository.findByUser(user);

        assertEquals(1, donations.size());
        assertEquals(payment, donations.get(0).getPayment());
        assertEquals("donation1", donations.get(0).getDonationText());
        assertEquals("sender1", donations.get(0).getSenderUsername());
    }

    @Test
    public void countByUser_ShouldReturn1() {
        PaymentEntity payment = entityManager.find(PaymentEntity.class, payments.get(0).getId());
        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        entityManager.persistAndFlush(payment);

        assertEquals(1, donationRepository.countByUser(users.get(0)));
    }

    @Test
    public void countByUser_ShouldReturn0() {
        assertEquals(0, donationRepository.countByUser(users.get(0)));
    }
}

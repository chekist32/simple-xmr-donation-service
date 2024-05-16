package com.sokol.simplemonerodonationservice.integration.api;

import com.sokol.simplemonerodonationservice.auth.AuthController;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataEntity;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataRepository;
import com.sokol.simplemonerodonationservice.email.EmailService;
import com.sokol.simplemonerodonationservice.user.UserEntity;
import com.sokol.simplemonerodonationservice.user.UserRepository;
import com.sokol.simplemonerodonationservice.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.ResourceUtils;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthApiTest {
    @Container
    private static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container
    private static DockerComposeContainer<?> moneroWallerRpcContainer;


    static {
        try {
            moneroWallerRpcContainer = new DockerComposeContainer<>(ResourceUtils.getFile("classpath:docker-compose-test.yml"))
                    .withExposedService("monero-rpc", 38083)
                    .withLocalCompose(false);
        } catch (FileNotFoundException e) { throw new RuntimeException(e); }

        postgresContainer.start();
        moneroWallerRpcContainer.start();

        System.setProperty("POSTGRES_DB_HOST", postgresContainer.getHost()+":"+postgresContainer.getFirstMappedPort());
        System.setProperty("POSTGRES_DB_NAME", postgresContainer.getDatabaseName());
        System.setProperty("POSTGRES_DB_USERNAME", postgresContainer.getUsername());
        System.setProperty("POSTGRES_DB_PASSWORD", postgresContainer.getPassword());
    }

    @Autowired
    private UserRepository userRepository;

    private void clearTable() {
        userRepository.deleteAll();
    }

    private void initUser() {
        UserEntity user = new UserEntity("email@email.com", "username", "pass");
        user.setDonationUserData(new DonationUserDataEntity());
        userRepository.save(user);
    }

    @Test
    public void test() {
        clearTable();
        initUser();
        assertEquals(1, userRepository.count());
    }
}

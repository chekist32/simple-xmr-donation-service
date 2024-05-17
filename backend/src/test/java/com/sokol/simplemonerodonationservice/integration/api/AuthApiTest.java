package com.sokol.simplemonerodonationservice.integration.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;
import com.sokol.simplemonerodonationservice.auth.registration.ConfirmationTokenEntity;
import com.sokol.simplemonerodonationservice.auth.registration.ConfirmationTokenRepository;
import com.sokol.simplemonerodonationservice.auth.registration.RegistrationRequestDTO;
import com.sokol.simplemonerodonationservice.donation.donationuserdata.DonationUserDataEntity;
import com.sokol.simplemonerodonationservice.user.UserEntity;
import com.sokol.simplemonerodonationservice.user.UserRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.FileNotFoundException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthApiTest {
    private final static String ORIGIN = System.getenv("ADMIN_PANEL_UI_URL");

    @Container
    private static PostgreSQLContainer<?> postgresContainer;
    @Container
    private static DockerComposeContainer<?> moneroWallerRpcContainer;
    @RegisterExtension
    private static GreenMailExtension greenMailExtension;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;


    static {
        postgresContainer = new PostgreSQLContainer<>("postgres:16-alpine");
        try {
            moneroWallerRpcContainer = new DockerComposeContainer<>(ResourceUtils.getFile("classpath:docker-compose-test.yml"))
                    .withExposedService("monero-rpc", 38083)
                    .withLocalCompose(false);
        } catch (FileNotFoundException e) { throw new RuntimeException(e); }
        greenMailExtension = new GreenMailExtension(ServerSetup.SMTP.port(3025))
                .withConfiguration(GreenMailConfiguration.aConfig().withUser("user","pass"))
                .withPerMethodLifecycle(false);

        postgresContainer.start();
        moneroWallerRpcContainer.start();

        System.setProperty("POSTGRES_DB_HOST", postgresContainer.getHost()+":"+postgresContainer.getFirstMappedPort());
        System.setProperty("POSTGRES_DB_NAME", postgresContainer.getDatabaseName());
        System.setProperty("POSTGRES_DB_USERNAME", postgresContainer.getUsername());
        System.setProperty("POSTGRES_DB_PASSWORD", postgresContainer.getPassword());
    }

    private void clearTable() {
        userRepository.deleteAll();
        confirmationTokenRepository.deleteAll();
    }
    private void initUser() {
        UserEntity user = new UserEntity("email@email.com", "username", "pass");
        user.setDonationUserData(new DonationUserDataEntity());
        userRepository.save(user);
    }

    @Test
    public void registrationTest() throws Exception {
        clearTable();

        RegistrationRequestDTO registrationRequest = new RegistrationRequestDTO("username", "email@email.com", "pass");

        mockMvc.perform(
                post("/api/auth/register")
                        .header("Origin", ORIGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest))
        ).andExpect(status().isCreated());

        assertEquals(1, userRepository.count());
        assertEquals(1, confirmationTokenRepository.count());

        UserEntity user = userRepository.findByEmail("email@email.com").get();
        assertNotNull(user);
        assertFalse(user.isEnabled());

        assertEquals(1, greenMailExtension.getReceivedMessages().length);
        MimeMessage message = greenMailExtension.getReceivedMessages()[0];
        String token = ((String) message.getContent()).split("token=")[1];

        mockMvc.perform(
                get("/api/auth/register/confirmation?token="+token)
                        .header("Origin", ORIGIN)
        ).andExpect(status().isOk());

        UserEntity registeredUser = userRepository.findByEmail("email@email.com").get();
        assertTrue(registeredUser.isEnabled());

        ConfirmationTokenEntity usedToken = confirmationTokenRepository
                .findById(UUID.fromString(token))
                .get();

        assertNotNull(usedToken);
        assertNotNull(usedToken.getConfirmedAt());
        assertFalse(usedToken.isActive());
    }
}

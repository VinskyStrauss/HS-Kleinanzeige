package de.hs.da.hskleinanzeigen;

import com.redis.testcontainers.RedisContainer;
import de.hs.da.hskleinanzeigen.entity.User;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
public class GetUserRedisIntegrationTest {
    static {
        GenericContainer<?> redis =
                new GenericContainer<>(DockerImageName.parse("redis:7.0.12")).withExposedPorts(6379);
        redis.start();
        System.setProperty("spring.redis.host", redis.getHost());
        System.setProperty("spring.redis.port", redis.getMappedPort(6379).toString());
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    User user = TestUtils.createUser("somevalidredisuser@email.de","Vorname", "Nachname", "Standort", "pass123supi","06254-call-me-maybe");

    @BeforeEach
    void setUp(){
        user = userRepository.save(user);
    }

    @AfterEach
    void tearDown(){
        userRepository.delete(user);
    }

    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis:7.0.12")).withExposedPorts(6379);

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379).toString());
    }

    @Test
    void testContainerRunning() {
        assertTrue(REDIS_CONTAINER.isRunning());
    }

    @Test
    void testGetUserByIdEndpoint() throws Exception {
        UserRepository userRepositoryMock = mock(UserRepository.class);
        UserRepository userRepositorySpy = spy(userRepositoryMock);

        mockMvc.perform(get(TestUtils.BASE_PATH_USER + "/{userId}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value("somevalidredisuser@email.de"))
                .andExpect(jsonPath("$.firstName").value("Vorname"))
                .andExpect(jsonPath("$.lastName").value("Nachname"))
                .andExpect(jsonPath("$.location").value("Standort"))
                .andExpect(jsonPath("$.phone").value("06254-call-me-maybe"));
        verify(userRepositorySpy, times(0)).findById(user.getId());

    }
}

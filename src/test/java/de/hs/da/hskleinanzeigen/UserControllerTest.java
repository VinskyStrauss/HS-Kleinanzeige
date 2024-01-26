package de.hs.da.hskleinanzeigen;

import de.hs.da.hskleinanzeigen.entity.User;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp(){
        User user = TestUtils.createUser("somevaliduser@email.de","Vorname", "Nachname", "Standort", "pass123supi","06254-call-me-maybe");
        userRepository.save(user);
    }

    @Test
    void createUserStatus201() throws Exception {
        final String CREATE_USER_PAYLOAD = "{\n" +
                "    \"email\": \"valid@email.com\",\n" +
                "    \"password\": \"secret\",\n" +
                "    \"firstName\": \"Thomas\",\n" +
                "    \"lastName\": \"Müller\",\n" +
                "    \"phone\": \"069-123456\",\n" +
                "    \"location\": \"Darmstadt\"\n" +
                "}";

        mockMvc.perform(post(TestUtils.BASE_PATH_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_USER_PAYLOAD)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("valid@email.com"))
                .andExpect(jsonPath("$.firstName").value("Thomas"))
                .andExpect(jsonPath("$.lastName").value("Müller"))
                .andExpect(jsonPath("$.location").value("Darmstadt"))
                .andExpect(jsonPath("$.phone").value("069-123456"));
    }

    @Test
    void createUserStatus400_InvalidPassword() throws Exception {
        final String CREATE_USER_PAYLOAD_WRONG_PASSWORD = "{\n" +
                "    \"email\":\"valid@email.com\",\n" +
                "    \"password\":\"sec\",\n" +
                "    \"firstName\":\"Thomas\",\n" +
                "    \"lastName\":\"Müller\",\n" +
                "    \"phone\":\"069-123456\",\n" +
                "    \"location\":\"Darmstadt\"\n" +
                "}";

        mockMvc.perform(post(TestUtils.BASE_PATH_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_USER_PAYLOAD_WRONG_PASSWORD)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isBadRequest());
    }
    @Test
    void createUserStatus400_InvalidName() throws Exception {
        final String CREATE_USER_PAYLOAD_WRONG_FIRST_NAME = "{\n" +
                "    \"email\":\"valid@email.com\",\n" +
                "    \"password\":\"secret\",\n" +
                "    \"firstName\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Aenean ut magna mollis, ornare sapien vitae, bibendum felis. Duis in libero sit amet mauris malesuada gravida. Nulla facilisi. Vivamus ac elit at nibh egestas auctor. Fusce et efficitur lacus, eget volutpat eros. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Curabitur tristique sit amet orci eu congue. Sed at elit a velit vestibulum rhoncus. Proin at bibendum nisi. Donec non tristique nulla, in convallis orci. Nulla eu ligula lectus. Donec sed tortor sit amet leo placerat facilisis. Donec laoreet felis felis, ac rhoncus nisl sodales vitae.\",\n" +
                "    \"lastName\":\"Müller\",\n" +
                "    \"phone\":\"069-123456\",\n" +
                "    \"location\":\"Darmstadt\"\n" +
                "}";

        mockMvc.perform(post(TestUtils.BASE_PATH_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_USER_PAYLOAD_WRONG_FIRST_NAME)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserStatus400_InvalidEmail() throws Exception {
        final String CREATE_USER_PAYLOAD_WRONG_EMAIL = "{\n" +
                "    \"email\":\"invalid\",\n" +
                "    \"password\":\"secret\",\n" +
                "    \"firstName\":\"Thomas\",\n" +
                "    \"lastName\":\"Müller\",\n" +
                "    \"phone\":\"069-123456\",\n" +
                "    \"location\":\"Darmstadt\"\n" +
                "}";

        mockMvc.perform(post(TestUtils.BASE_PATH_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_USER_PAYLOAD_WRONG_EMAIL)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserStatus409() throws Exception {
        userRepository.save(TestUtils.createUser("somevaliduser2@email.de","Vorname", "Nachname", "Standort", "pass123supi","06254-call-me-maybe"));
        final String CREATE_USER_PAYLOAD_INVALID = "{\n" +
                "    \"email\":\"somevaliduser2@email.de\",\n" +
                "    \"password\":\"secret\",\n" +
                "    \"firstName\":\"Thomas\",\n" +
                "    \"lastName\":\"Müller\",\n" +
                "    \"phone\":\"069-123456\",\n" +
                "    \"location\":\"Darmstadt\"\n" +
                "}";

        mockMvc.perform(post(TestUtils.BASE_PATH_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_USER_PAYLOAD_INVALID)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isConflict());
    }

    @Test
    void getUserByIdStatus200() throws Exception {
        mockMvc.perform(get(TestUtils.BASE_PATH_USER + "/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("somevalid@email.de"))
                .andExpect(jsonPath("$.firstName").value("Vorname"))
                .andExpect(jsonPath("$.lastName").value("Nachname"))
                .andExpect(jsonPath("$.location").value("Standort"))
                .andExpect(jsonPath("$.phone").value("06254-call-me-maybe"));
    }

    @Test
    void getUserByIdStatus404() throws Exception {
        mockMvc.perform(get(TestUtils.BASE_PATH_USER + "/{userId}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsersStatus200() throws Exception {
        mockMvc.perform(get(TestUtils.BASE_PATH_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "3")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(notNullValue()))
                .andExpect(jsonPath("$.content[0].email").value(notNullValue()))
                .andExpect(jsonPath("$.content[0].firstName").value(notNullValue()))
                .andExpect(jsonPath("$.content[0].lastName").value(notNullValue()))
                .andExpect(jsonPath("$.content[0].phone").value(notNullValue()))
                .andExpect(jsonPath("$.content[0].location").value(notNullValue()))
                .andExpect(jsonPath("$.pageable").value(notNullValue()))
                .andExpect(jsonPath("$.totalPages").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(1)));
    }
}

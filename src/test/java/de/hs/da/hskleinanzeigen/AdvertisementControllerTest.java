package de.hs.da.hskleinanzeigen;

import de.hs.da.hskleinanzeigen.entity.AdType;
import de.hs.da.hskleinanzeigen.entity.Advertisement;
import de.hs.da.hskleinanzeigen.entity.Category;
import de.hs.da.hskleinanzeigen.entity.User;
import de.hs.da.hskleinanzeigen.integration.TestUtils;
import de.hs.da.hskleinanzeigen.repository.AdvertisementRepository;
import de.hs.da.hskleinanzeigen.repository.CategoryRepository;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static de.hs.da.hskleinanzeigen.integration.TestUtils.createCategory;
import static de.hs.da.hskleinanzeigen.integration.TestUtils.createUser;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest
//@Transactional
public class AdvertisementControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdvertisementRepository advertisementRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp(){
        Category category1 = createCategory(200003,"NameA");
        User user = createUser(100002,"somevalid@email.de","Vorname", "Nachname", "Standort", "pass123supi","06254-call-me-maybe");
        category1 = categoryRepository.save(category1);
        categoryRepository.save(TestUtils.category2);
        user = userRepository.save(user);
        advertisementRepository.save(TestUtils.createAd(300003, AdType.OFFER, category1, user, "Titel", "Beschreibung", 42, "Standort"));
        advertisementRepository.save(TestUtils.createAd(300004, AdType.REQUEST, category1, user, "Titel", "Beschreibung", 42, "Standort"));
    }

    @AfterEach
    void tearDown(){
        categoryRepository.delete(TestUtils.category1);
        categoryRepository.delete(TestUtils.category2);
        userRepository.delete(TestUtils.user);
        advertisementRepository.delete(TestUtils.ad1);
        advertisementRepository.delete(TestUtils.ad2);
    }

    @Test
    void createAdvertisementStatus201() throws Exception {
        final String CREATE_ADD_PAYLOAD = "{\n" +
                "    \"type\" : \"OFFER\",\n" +
                "    \"categoryId\": 200003,\n" +
                "    \"title\":\"Zimmer in 4er WG\",\n" +
                "    \"description\":\"Wohnheim direkt neben der HS 2\",\n" +
                "    \"price\" : 400,\n" +
                "    \"location\":\"Birkenweg, Darmstadt\",\n" +
                "    \"userId\" : 100002\n" +
                "}";

        mockMvc.perform(post("/api/advertisements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_ADD_PAYLOAD)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isCreated())
                .andExpect((ResultMatcher) jsonPath("$.id").isNotEmpty())
                .andExpect((ResultMatcher) jsonPath("$.type").value("OFFER"))
                .andExpect((ResultMatcher) jsonPath("$.title").value("Zimmer in 4er WG"))
                .andExpect((ResultMatcher) jsonPath("$.description").value("Wohnheim direkt neben der HS"))
                .andExpect((ResultMatcher) jsonPath("$.price").value(400));

        Advertisement createdAd = advertisementRepository.findByTitle("Wohnheim direkt neben der HS 2").orElseThrow();
        advertisementRepository.delete(createdAd);
    }
}

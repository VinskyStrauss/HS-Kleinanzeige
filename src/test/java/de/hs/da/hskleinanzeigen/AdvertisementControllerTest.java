package de.hs.da.hskleinanzeigen;

import de.hs.da.hskleinanzeigen.entity.AdType;
import de.hs.da.hskleinanzeigen.entity.Category;
import de.hs.da.hskleinanzeigen.entity.User;
import de.hs.da.hskleinanzeigen.repository.AdvertisementRepository;
import de.hs.da.hskleinanzeigen.repository.CategoryRepository;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
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
        Category category1 = TestUtils.createCategory("NameA");
        User user = TestUtils.createUser("somevalid@email.de","Vorname", "Nachname", "Standort", "pass123supi","06254-call-me-maybe");
        category1 = categoryRepository.save(category1);
        user = userRepository.save(user);
        advertisementRepository.save(TestUtils.createAd(AdType.OFFER, category1, user, "Titel A", "Beschreibung", 42, "Standort"));
        advertisementRepository.save(TestUtils.createAd(AdType.REQUEST, category1, user, "Titel B", "Beschreibung", 42, "Standort"));
    }

    @Test
    void createAdvertisementStatus201() throws Exception {
        final String CREATE_ADD_PAYLOAD = "{\n" +
                "    \"type\" : \"OFFER\",\n" +
                "    \"categoryId\": 1,\n" +
                "    \"title\":\"Zimmer in 4er WG 2\",\n" +
                "    \"description\":\"Wohnheim direkt neben der HS\",\n" +
                "    \"price\" : 400,\n" +
                "    \"location\":\"Birkenweg, Darmstadt\",\n" +
                "    \"userId\" : 1\n" +
                "}";

        mockMvc.perform(post(TestUtils.BASE_PATH_AD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_ADD_PAYLOAD)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.type").value("OFFER"))
                .andExpect(jsonPath("$.title").value("Zimmer in 4er WG 2"))
                .andExpect(jsonPath("$.description").value("Wohnheim direkt neben der HS"))
                .andExpect(jsonPath("$.price").value(400));
    }

    @Test
    void createAdvertisementStatus400() throws Exception {
        final String CREATE_ADD_PAYLOAD_INCOMPLETE = "{\n" +
                "    \"type\" : \"OFFER\",\n" +
                "    \"categoryId\" : 200003,\n" +
                "    \"title\":\"Zimmer in 4er WG\",\n" +
                "    \"description\":\"Wohnheim direkt neben der HS\",\n" +
                "    \"price\" : 400,\n" +
                "    \"location\":\"Birkenweg, Darmstadt\"\n" +
                "}";

        mockMvc.perform(post(TestUtils.BASE_PATH_AD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_ADD_PAYLOAD_INCOMPLETE)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createAdvertisementStatus404() throws Exception {
        final String CREATE_ADD_PAYLOAD_INVALID = "{\n" +
                "    \"type\" : \"OFFER\",\n" +
                "    \"categoryId\": 200003,\n" +
                "    \"title\":\"Zimmer in 4er WG\",\n" +
                "    \"description\":\"Wohnheim direkt neben der HS\",\n" +
                "    \"price\" : 400,\n" +
                "    \"location\":\"Birkenweg, Darmstadt\",\n" +
                "    \"userId\" : 100009\n" +
                "}";

        mockMvc.perform(post(TestUtils.BASE_PATH_AD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_ADD_PAYLOAD_INVALID)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAdvertisementByIdStatus200() throws Exception {
        final int AD_ID = 1;

        mockMvc.perform(get(TestUtils.BASE_PATH_AD + "/{id}", AD_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(AD_ID))
                .andExpect(jsonPath("$.type").value("OFFER"))
                .andExpect(jsonPath("$.title").value("Titel A"))
                .andExpect(jsonPath("$.description").value("Beschreibung"))
                .andExpect(jsonPath("$.price").value(42))
                .andExpect(jsonPath("$.location").value("Standort"))
                .andExpect(jsonPath("$.category.id").value(1))
                .andExpect(jsonPath("$.category.name").value("NameA"))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.email").value("somevalid@email.de"))
                .andExpect(jsonPath("$.user.firstName").value("Vorname"))
                .andExpect(jsonPath("$.user.lastName").value("Nachname"))
                .andExpect(jsonPath("$.user.location").value("Standort"))
                .andExpect(jsonPath("$.user.phone").value("06254-call-me-maybe"));
    }

    @Test
    void getAdvertisementByIdStatus404() throws Exception {
        final int AD_ID_INVALID = 300009;

        mockMvc.perform(get(TestUtils.BASE_PATH_AD + "/{id}", AD_ID_INVALID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllAdvertisementsStatus200() throws Exception {
        mockMvc.perform(get(TestUtils.BASE_PATH_AD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "3")
                        .with(httpBasic("user", "user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").isNotEmpty())
                .andExpect(jsonPath("$.content[0].type").isNotEmpty())
                .andExpect(jsonPath("$.content[0].title").isNotEmpty())
                .andExpect(jsonPath("$.content[0].price").isNotEmpty())
                .andExpect(jsonPath("$.content[0].location").isNotEmpty())
                .andExpect(jsonPath("$.content[0].description").isNotEmpty())
                .andExpect(jsonPath("$.content[0].category").isNotEmpty())
                .andExpect(jsonPath("$.content[0].category.id").isNotEmpty())
                .andExpect(jsonPath("$.content[0].category.name").isNotEmpty())
                .andExpect(jsonPath("$.content[0].user").isNotEmpty())
                .andExpect(jsonPath("$.content[0].user.id").isNotEmpty())
                .andExpect(jsonPath("$.content[0].user.email").isNotEmpty())
                .andExpect(jsonPath("$.content[0].user.firstName").isNotEmpty())
                .andExpect(jsonPath("$.content[0].user.lastName").isNotEmpty())
                .andExpect(jsonPath("$.content[0].user.phone").isNotEmpty())
                .andExpect(jsonPath("$.content[0].user.location").isNotEmpty())
                .andExpect(jsonPath("$.pageable").isNotEmpty())
                .andExpect(jsonPath("$.totalPages").isNumber())
                .andExpect(jsonPath("$.totalElements").isNumber());
    }

    @Test
    void getAllAdvertisementsStatus200WithAllParams() throws Exception {
        mockMvc.perform(get(TestUtils.BASE_PATH_AD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("type", "REQUEST")
                        .param("categoryId", "1")
                        .param("priceFrom", "0")
                        .param("priceTo", "600")
                        .param("page", "0")
                        .param("size", "3")
                        .with(httpBasic("user", "user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.pageable").isNotEmpty());
    }

    @Test
    void getAllAdvertisementsStatus200_NoAdsFoundForGivenPrice() throws Exception {
        mockMvc.perform(get(TestUtils.BASE_PATH_AD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("type", "OFFER")
                        .param("categoryId", "1")
                        .param("priceFrom", "100000")
                        .param("priceTo", "100000")
                        .param("page", "0")
                        .param("size", "3")
                        .with(httpBasic("user", "user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.pageable").isNotEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void getAllAdvertisementsStatus200_NoAdsFoundForUnknownCategory() throws Exception {
        mockMvc.perform(get(TestUtils.BASE_PATH_AD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("type", "REQUEST")
                        .param("categoryId", "200002")
                        .param("priceFrom", "0")
                        .param("priceTo", "600")
                        .param("page", "0")
                        .param("size", "3")
                        .with(httpBasic("user", "user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.pageable").isNotEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}

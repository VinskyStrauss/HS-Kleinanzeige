package de.hs.da.hskleinanzeigen;

import de.hs.da.hskleinanzeigen.entity.AdType;
import de.hs.da.hskleinanzeigen.entity.Advertisement;
import de.hs.da.hskleinanzeigen.entity.Category;
import de.hs.da.hskleinanzeigen.entity.User;
import de.hs.da.hskleinanzeigen.repository.AdvertisementRepository;
import de.hs.da.hskleinanzeigen.repository.CategoryRepository;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
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
public class AdvertisementControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AdvertisementRepository advertisementRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;

    Category category = TestUtils.createCategory("NameAd");
    User user = TestUtils.createUser("somevalidad@email.de","Vorname", "Nachname", "Standort", "pass123supi","06254-call-me-maybe");

    Advertisement ad1;
    Advertisement ad2;

    @BeforeEach
    void setUp(){
        category = categoryRepository.save(category);
        user = userRepository.save(user);
        ad1 = advertisementRepository.save(TestUtils.createAd(AdType.OFFER, category, user, "Titel Ad", "Beschreibung", 42, "Standort"));
        ad2 = advertisementRepository.save(TestUtils.createAd(AdType.REQUEST, category, user, "Titel Bad", "Beschreibung", 42, "Standort"));
    }

    @AfterEach
    void tearDown(){
        advertisementRepository.delete(ad1);
        advertisementRepository.delete(ad2);
        categoryRepository.delete(category);
        userRepository.delete(user);
    }

    @Test
    void createAdvertisementStatus201() throws Exception {
        final String CREATE_ADD_PAYLOAD = "{\n" +
                "    \"type\" : \"OFFER\",\n" +
                "    \"categoryId\": " + category.getId() + ",\n" +
                "    \"title\":\"Zimmer in 4er WG 3\",\n" +
                "    \"description\":\"Wohnheim direkt neben der HS\",\n" +
                "    \"price\" : 400,\n" +
                "    \"location\":\"Birkenweg, Darmstadt\",\n" +
                "    \"userId\" : " + user.getId() + "\n" +
                "}";

        mockMvc.perform(post(TestUtils.BASE_PATH_AD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_ADD_PAYLOAD)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.type").value("OFFER"))
                .andExpect(jsonPath("$.title").value("Zimmer in 4er WG 3"))
                .andExpect(jsonPath("$.description").value("Wohnheim direkt neben der HS"))
                .andExpect(jsonPath("$.price").value(400));

        advertisementRepository.delete(advertisementRepository.findByTitle("Zimmer in 4er WG 3").orElseThrow());
    }

    @Test
    void createAdvertisementStatus400() throws Exception {
        final String CREATE_ADD_PAYLOAD_INCOMPLETE = """
                {
                    "type" : "OFFER",
                    "categoryId" : 200003,
                    "title":"Zimmer in 4er WG",
                    "description":"Wohnheim direkt neben der HS",
                    "price" : 400,
                    "location":"Birkenweg, Darmstadt"
                }""";

        mockMvc.perform(post(TestUtils.BASE_PATH_AD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_ADD_PAYLOAD_INCOMPLETE)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createAdvertisementStatus404() throws Exception {
        final String CREATE_ADD_PAYLOAD_INVALID = """
                {
                    "type" : "OFFER",
                    "categoryId": 200003,
                    "title":"Zimmer in 4er WG",
                    "description":"Wohnheim direkt neben der HS",
                    "price" : 400,
                    "location":"Birkenweg, Darmstadt",
                    "userId" : 100009
                }""";

        mockMvc.perform(post(TestUtils.BASE_PATH_AD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_ADD_PAYLOAD_INVALID)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAdvertisementByIdStatus200() throws Exception {
        mockMvc.perform(get(TestUtils.BASE_PATH_AD + "/{id}", ad1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ad1.getId()))
                .andExpect(jsonPath("$.type").value("OFFER"))
                .andExpect(jsonPath("$.title").value("Titel Ad"))
                .andExpect(jsonPath("$.description").value("Beschreibung"))
                .andExpect(jsonPath("$.price").value(42))
                .andExpect(jsonPath("$.location").value("Standort"))
                .andExpect(jsonPath("$.category.id").value(category.getId()))
                .andExpect(jsonPath("$.category.name").value("NameAd"))
                .andExpect(jsonPath("$.user.id").value(user.getId()))
                .andExpect(jsonPath("$.user.email").value("somevalidad@email.de"))
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
                        .param("categoryId", String.valueOf(category.getId()))
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

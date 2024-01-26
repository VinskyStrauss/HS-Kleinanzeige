package de.hs.da.hskleinanzeigen;

import de.hs.da.hskleinanzeigen.entity.AdType;
import de.hs.da.hskleinanzeigen.entity.Advertisement;
import de.hs.da.hskleinanzeigen.entity.Category;
import de.hs.da.hskleinanzeigen.entity.User;
import de.hs.da.hskleinanzeigen.repository.AdvertisementRepository;
import de.hs.da.hskleinanzeigen.repository.CategoryRepository;
import de.hs.da.hskleinanzeigen.repository.NotepadRepository;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest
public class NotepadControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AdvertisementRepository advertisementRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotepadRepository notepadRepository;

    @BeforeEach
    void setUp(){
        Category category1 = TestUtils.createCategory("NameA");
        User user = TestUtils.createUser("somevalid@email.de","Vorname", "Nachname", "Standort", "pass123supi","06254-call-me-maybe");
        category1 = categoryRepository.save(category1);
        user = userRepository.save(user);

        Advertisement advertisement = TestUtils.createAd(AdType.OFFER, category1, user, "Titel A", "Beschreibung", 42, "Standort");
        advertisement = advertisementRepository.save(advertisement);
        advertisementRepository.save(TestUtils.createAd(AdType.OFFER, category1, user, "Titel B", "Beschreibung", 42, "Standort"));
        notepadRepository.save(TestUtils.createNotepad(user, advertisement, "Notiz"));
    }

    @Test
    void createNotepadStatus200_NewNotepad() throws Exception {
        final String NOTEPAD_PAYLOAD = "{\n" +
                "        \"advertisementId\": " + 2 + ",\n" +
                "        \"note\": \"Zimmer direkt bei der HS\"\n" +
                "    }\n";

        mockMvc.perform(put(TestUtils.BASE_PATH_NOTEPAD, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(NOTEPAD_PAYLOAD)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void createNotepadStatus200_ExistingNotepad() throws Exception{
        final String NOTEPAD_PAYLOAD_NOTE_CHANGED = "{\n" +
                "        \"advertisementId\": " + 1 + ",\n" +
                "        \"note\": \"Beste WG ever!\"\n" +
                "    }\n";

        mockMvc.perform(put(TestUtils.BASE_PATH_NOTEPAD, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(NOTEPAD_PAYLOAD_NOTE_CHANGED)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void testCreateNotepadStatus400() throws Exception {
        final int USER_ID = 100002;
        final String NOTEPAD_PAYLOAD_INCOMPLETE = "{\n" +
                "   \"note\":\"Zimmer direkt bei der HS\"\n" +
                "}\n";

        mockMvc.perform(put(TestUtils.BASE_PATH_NOTEPAD, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(NOTEPAD_PAYLOAD_INCOMPLETE)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetNotepadByUserIdStatus200() throws Exception {
        mockMvc.perform(get(TestUtils.BASE_PATH_NOTEPAD, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id", notNullValue()))
                .andExpect(jsonPath("[0].note", notNullValue()))
                .andExpect(jsonPath("[0].advertisement.id", notNullValue()))
                .andExpect(jsonPath("[0].advertisement.type", notNullValue()))
                .andExpect(jsonPath("[0].advertisement.title", notNullValue()))
                .andExpect(jsonPath("[0].advertisement.description", notNullValue()))
                .andExpect(jsonPath("[0].advertisement.price", notNullValue()))
                .andExpect(jsonPath("[0].advertisement.location", notNullValue()))
                .andExpect(jsonPath("[0].advertisement.category.id", notNullValue()))
                .andExpect(jsonPath("[0].advertisement.category.name", notNullValue()))
                .andExpect(jsonPath("[0].advertisement.user.id", notNullValue()))
                .andExpect(jsonPath("[0].advertisement.user.email", notNullValue()))
                .andExpect(jsonPath("[0].advertisement.user.firstName", notNullValue()))
                .andExpect(jsonPath("[0].advertisement.user.lastName", notNullValue()))
                .andExpect(jsonPath("[0].advertisement.user.location", notNullValue()))
                .andExpect(jsonPath("[0].advertisement.user.phone", notNullValue()));
    }

    @Test
    void testGetNotepadByUserIdStatus404() throws Exception {
        final int USER_ID = 100009;

        mockMvc.perform(get(TestUtils.BASE_PATH_NOTEPAD, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteEntityByUserIdAndAdvertisementIdStatus200() throws Exception {
        final int USER_ID = 1;
        final int AD_ID = 1;

        mockMvc.perform(delete(TestUtils.BASE_PATH_NOTEPAD, USER_ID)
                        .param("advertisementId", String.valueOf(AD_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteEntityByUserIdAndAdvertisementIdStatus404() throws Exception {
        final int USER_ID = 100009;
        final int AD_ID = 1;

        mockMvc.perform(delete(TestUtils.BASE_PATH_NOTEPAD, USER_ID)
                        .param("advertisementId", String.valueOf(AD_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isNotFound());
    }
}

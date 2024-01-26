package de.hs.da.hskleinanzeigen;

import de.hs.da.hskleinanzeigen.entity.Category;
import de.hs.da.hskleinanzeigen.repository.CategoryRepository;
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
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp(){
        Category category1 = TestUtils.createCategory("NameAA");
        categoryRepository.save(category1);
    }

    @Test
    void createAdvertisementStatus201() throws Exception {
        final String CREATE_CATEGORY_PAYLOAD = "{\n" +
                "   \"name\":\"" + "Category" + "\"\n" +
                "}\n";

        mockMvc.perform(post(TestUtils.BASE_PATH_CATEGORY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_CATEGORY_PAYLOAD)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Category"));
    }

    @Test
    void createAdvertisementStatus400() throws Exception {
        final String CREATE_CATEGORY_PAYLOAD_INCOMPLETE = "{\n" +
                "   \"name\":\"" + "" + "\"\n" +
                "}\n";

        mockMvc.perform(post(TestUtils.BASE_PATH_CATEGORY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_CATEGORY_PAYLOAD_INCOMPLETE)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createAdvertisementStatus409() throws Exception {
        categoryRepository.save(TestUtils.createCategory("NameAB"));
        final String CREATE_CATEGORY_PAYLOAD_INCOMPLETE = "{\n" +
                "   \"name\":\"" + "NameAB" + "\"\n" +
                "}\n";

        mockMvc.perform(post(TestUtils.BASE_PATH_CATEGORY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_CATEGORY_PAYLOAD_INCOMPLETE)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isConflict());
    }

    @Test
    void getCategoryByIdStatus200() throws Exception {
        categoryRepository.save(TestUtils.createCategory("NameAAA"));
        final int CATEGORY_ID = categoryRepository.findByName("NameAAA").orElseThrow().getId();

        mockMvc.perform(get(TestUtils.BASE_PATH_CATEGORY + "/{id}", CATEGORY_ID)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CATEGORY_ID))
                .andExpect(jsonPath("$.name").value("NameAAA"));
    }

    @Test
    void getCategoryByIdStatus404() throws Exception {
        final int CATEGORY_ID = 9;

        mockMvc.perform(get(TestUtils.BASE_PATH_CATEGORY + "{id}", CATEGORY_ID)
                        .with(httpBasic("user", "user")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllCategoriesStatus200() throws Exception {
        mockMvc.perform(get(TestUtils.BASE_PATH_CATEGORY)
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists());
    }

}

package de.hs.da.hskleinanzeigen.integration.controller;

import de.hs.da.hskleinanzeigen.HsKleinanzeigenApplication;
import de.hs.da.hskleinanzeigen.entity.User;
import de.hs.da.hskleinanzeigen.integration.TestUtils;
import de.hs.da.hskleinanzeigen.mapper.AdvertisementMapper;
import de.hs.da.hskleinanzeigen.service.AdvertisementService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


@SpringJUnitConfig
@SpringBootTest
@Transactional
public class CategoryControllerTest {
    @Autowired
    private DataSource dataSource;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = dataSource.getConnection();
        Assertions.assertNotNull(connection);

        RestAssured.baseURI = TestUtils.HOST;
        RestAssured.port = TestUtils.PORT;
        TestUtils.setAuthenticationForUser();

        TestUtils.emptyDatabase(connection);
        TestUtils.insertCategoryData(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void createAdvertisementStatus201() throws SQLException{
        TestUtils.deleteCategoryFromDatabase("Category", connection);
        final String CREATE_CATEGORY_PAYLOAD = "{\n" +
                "   \"name\":\"" + "Category" + "\"\n" +
                "}\n";

        given()
                .basePath(TestUtils.BASE_PATH_CATEGORY)
                .header("Content-Type", "application/json")
                .body(CREATE_CATEGORY_PAYLOAD)
                .accept("application/json")
                .when().post()
                .then().statusCode(HttpStatus.CREATED.value())
                .body("keySet()", containsInAnyOrder("id", "name"))
                .body("id", notNullValue())
                .body("name", equalTo("Category"));
    }

    @Test
    void createAdvertisementStatus400(){
        final String CREATE_CATEGORY_PAYLOAD_INCOMPLETE = "{\n" +
                "   \"name\":\"" + "" + "\"\n" +
                "}\n";

        given()
                .basePath(TestUtils.BASE_PATH_CATEGORY)
                .header("Content-Type", "application/json")
                .body(CREATE_CATEGORY_PAYLOAD_INCOMPLETE)
                .accept("application/json")
                .when().post()
                .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void createAdvertisementStatus409(){
        final String CREATE_CATEGORY_PAYLOAD_INCOMPLETE = "{\n" +
                "   \"name\":\"" + "NameA" + "\"\n" +
                "}\n";

        given()
                .basePath(TestUtils.BASE_PATH_CATEGORY)
                .header("Content-Type", "application/json")
                .body(CREATE_CATEGORY_PAYLOAD_INCOMPLETE)
                .accept("application/json")
                .when().post()
                .then().statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    void getCategoryByIdStatus200(){
        final int CATEGORY_ID = 200003;

        given()
                .basePath(TestUtils.BASE_PATH_CATEGORY + "/" + CATEGORY_ID)
                .header("Content-Type", "application/json")
                .accept("application/json")
                .when().get()
                .then().statusCode(HttpStatus.OK.value())
                .body("keySet()", containsInAnyOrder("id", "name"))
                .body("id", equalTo(CATEGORY_ID))
                .body("name", equalTo("NameA"));
    }

    @Test
    void getCategoryByIdStatus404(){
        final int CATEGORY_ID = 200009;

        given()
                .basePath(TestUtils.BASE_PATH_CATEGORY + "/" + CATEGORY_ID)
                .header("Content-Type", "application/json")
                .accept("application/json")
                .when().get()
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void getAllCategoriesStatus200(){
        given()
                .basePath(TestUtils.BASE_PATH_CATEGORY)
                .header("Content-Type", "application/json")
                .accept("application/json")
                .when().get()
                .then().statusCode(HttpStatus.OK.value())
                .body("[0].keySet()", containsInAnyOrder("id", "name"))
                .body("[0].id", notNullValue())
                .body("[0].name", notNullValue());
    }
}

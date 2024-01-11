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
public class AdvertisementControllerTest {
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
        TestUtils.insertAdvertisementData(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void createAdvertisementStatus201() throws SQLException{
        TestUtils.deleteAdFromDatabase(100002,200003, connection);
        final String CREATE_ADD_PAYLOAD = "{\n" +
                "    \"type\" : \"OFFER\",\n" +
                "    \"categoryId\": 200003,\n" +
                "    \"title\":\"Zimmer in 4er WG\",\n" +
                "    \"description\":\"Wohnheim direkt neben der HS\",\n" +
                "    \"price\" : 400,\n" +
                "    \"location\":\"Birkenweg, Darmstadt\",\n" +
                "    \"userId\" : 100002\n" +
                "}";

        given()
                .basePath(TestUtils.BASE_PATH_AD)
                .header("Content-Type", "application/json")
                .body(CREATE_ADD_PAYLOAD)
                .accept("application/json")
                .when().post()
                .then().statusCode(HttpStatus.CREATED.value())
                .body("keySet()", containsInAnyOrder("id", "type", "category", "user", "title", "description", "price", "location"))
                .body("category.keySet()", containsInAnyOrder("id", "name"))
                .body("user.keySet()",
                        containsInAnyOrder("id", "email", "firstName", "lastName", "phone", "location"))
                .body("id", notNullValue())
                .body("type", equalTo("OFFER"))
                .body("title", equalTo("Zimmer in 4er WG"))
                .body("description", equalTo("Wohnheim direkt neben der HS"))
                .body("price", equalTo(400))
                .body("location", equalTo("Birkenweg, Darmstadt"))
                .body("category.id", equalTo(200003))
                .body("category.name", equalTo("NameA"))
                .body("user.id", equalTo(100002))
                .body("user.email", equalTo("somevalid@email.de"))
                .body("user.firstName", equalTo("Vorname"))
                .body("user.lastName", equalTo("Nachname"))
                .body("user.location", equalTo("Standort"))
                .body("user.phone", equalTo("06254-call-me-maybe"));
    }

    @Test
    void createAdvertisementStatus400(){
        final String CREATE_ADD_PAYLOAD_INCOMPLETE = "{\n" +
                "    \"type\" : \"OFFER\",\n" +
                "    \"categoryId\" : 200003,\n" +
                "    \"title\":\"Zimmer in 4er WG\",\n" +
                "    \"description\":\"Wohnheim direkt neben der HS\",\n" +
                "    \"price\" : 400,\n" +
                "    \"location\":\"Birkenweg, Darmstadt\"\n" +
                "}";

        given()
                .basePath(TestUtils.BASE_PATH_AD)
                .header("Content-Type", "application/json")
                .body(CREATE_ADD_PAYLOAD_INCOMPLETE)
                .accept("application/json")
                .when().post()
                .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void createAdvertisementStatus404(){
        final String CREATE_ADD_PAYLOAD_INVALID = "{\n" +
                "    \"type\" : \"OFFER\",\n" +
                "    \"categoryId\": 200003,\n" +
                "    \"title\":\"Zimmer in 4er WG\",\n" +
                "    \"description\":\"Wohnheim direkt neben der HS\",\n" +
                "    \"price\" : 400,\n" +
                "    \"location\":\"Birkenweg, Darmstadt\",\n" +
                "    \"userId\" : 100009\n" +
                "}";

        given()
                .basePath(TestUtils.BASE_PATH_AD)
                .header("Content-Type", "application/json")
                .body(CREATE_ADD_PAYLOAD_INVALID)
                .accept("application/json")
                .when().post()
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void getAdvertisementByIdStatus200(){
        final int AD_ID = 300004;

        given()
                .basePath(TestUtils.BASE_PATH_AD + "/" + AD_ID)
                .header("Content-Type", "application/json")
                .accept("application/json")
                .when().get()
                .then().statusCode(HttpStatus.OK.value())
                .body("keySet()", containsInAnyOrder("id", "type", "category", "user", "title", "description", "price", "location"))
                .body("category.keySet()", containsInAnyOrder("id", "name"))
                .body("user.keySet()",
                        containsInAnyOrder("id", "email", "firstName", "lastName", "phone", "location"))
                .body("id", equalTo(AD_ID))
                .body("type", equalTo("REQUEST"))
                .body("title", equalTo("Titel"))
                .body("description", equalTo("Beschreibung"))
                .body("price", equalTo(42))
                .body("location", equalTo("Standort"))
                .body("category.id", equalTo(200003))
                .body("category.name", equalTo("NameA"))
                .body("user.id", equalTo(100002))
                .body("user.email", equalTo("somevalid@email.de"))
                .body("user.firstName", equalTo("Vorname"))
                .body("user.lastName", equalTo("Nachname"))
                .body("user.location", equalTo("Standort"))
                .body("user.phone", equalTo("06254-call-me-maybe"));
    }

    @Test
    void getAdvertisementByIdStatus404(){
        final int AD_ID_INVALID = 300009;

        given()
                .basePath(TestUtils.BASE_PATH_AD + "/" + AD_ID_INVALID)
                .header("Content-Type", "application/json")
                .accept("application/json")
                .when().get()
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void getAllAdvertisementsStatus200() {
        given()
                .basePath(TestUtils.BASE_PATH_AD)
                .accept("application/json")
                .queryParams("page", 0, "size", 3)
                .when().get()
                .then().statusCode(HttpStatus.OK.value())
                .body("content[0].id", notNullValue())
                .body("content[0].type", notNullValue())
                .body("content[0].title", notNullValue())
                .body("content[0].price", notNullValue())
                .body("content[0].location", notNullValue())
                .body("content[0].description", notNullValue())
                .body("content[0].category", notNullValue())
                .body("content[0].category.id", notNullValue())
                .body("content[0].category.name", notNullValue())
                .body("content[0].user", notNullValue())
                .body("content[0].user.id", notNullValue())
                .body("content[0].user.email", notNullValue())
                .body("content[0].user.firstName", notNullValue())
                .body("content[0].user.lastName", notNullValue())
                .body("content[0].user.phone", notNullValue())
                .body("content[0].user.location", notNullValue())
                .body("pageable", notNullValue())
                .body("totalPages", greaterThanOrEqualTo(1))
                .body("totalElements", greaterThanOrEqualTo(1))
                .body("content[0].keySet()",
                        containsInAnyOrder("id", "type", "category", "user", "title", "price", "location", "description"))
                .body("content[0].category.keySet()", containsInAnyOrder("id", "name"))
                .body("content[0].user.keySet()",
                        containsInAnyOrder("id", "email", "firstName", "lastName", "phone", "location"));

    }

    @Test
    void getAllAdvertisementsStatus200WithAllParams() {
        given()
                .basePath(TestUtils.BASE_PATH_AD)
                .accept("application/json")
                .queryParams("type", "REQUEST", "categoryId", 200001, "priceFrom", 0, "priceTo", 600, "page",
                        0, "size", 3)
                .when().get()
                .then().statusCode(HttpStatus.OK.value())
                .body("content", notNullValue())
                .body("pageable", notNullValue());
    }

    @Test
    void getAllAdvertisementsStatus200_NoAdsFoundForGivenPrice() {
        given()
                .basePath(TestUtils.BASE_PATH_AD)
                .accept("application/json")
                .queryParams("type", "OFFER", "categoryId", 200001, "priceFrom", 100000, "priceTo", 100000,
                        "page", 0, "size", 3)
                .when().get()
                .then().statusCode(HttpStatus.OK.value())
                .body("content", notNullValue())
                .body("pageable", notNullValue())
                .body("totalElements", equalTo(0));
    }

    @Test
    void getAllAdvertisementsStatus200_NoAdsFoundForUnknownCategory() {
        given()
                .basePath(TestUtils.BASE_PATH_AD)
                .accept("application/json")
                .queryParams("type", "REQUEST", "categoryId", 200002, "priceFrom", 0, "priceTo", 600, "page", 0,
                        "size", 3)
                .when().get()
                .then().statusCode(HttpStatus.OK.value())
                .body("content", notNullValue())
                .body("pageable", notNullValue())
                .body("totalElements", equalTo(0));
    }
}

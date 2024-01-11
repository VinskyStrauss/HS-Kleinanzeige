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
public class NotepadControllerTest {
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
        TestUtils.insertNotepadData(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void createNotepadStatus200_NewNotepad() throws SQLException{
        final int USER_ID = 100002;
        final String NOTEPAD_PAYLOAD = "{\n" +
                "        \"advertisementId\": " + 300004 + ",\n" +
                "        \"note\": \"Zimmer direkt bei der HS\"\n" +
                "    }\n";

        given()
                .basePath(TestUtils.BASE_PATH_NOTEPAD)
                .header("Content-Type", "application/json")
                .pathParam("userId", USER_ID)
                .body(NOTEPAD_PAYLOAD)
                .accept("application/json")
                .when().put()
                .then().statusCode(HttpStatus.OK.value())
                .body("keySet()",
                        containsInAnyOrder("id"))
                .body("id", notNullValue());
        TestUtils.deleteNotepadFromDatabase(100002,300004, connection);
    }

    @Test
    void createNotepadStatus200_ExistingNotepad(){
        final int USER_ID = 100002;
        final String NOTEPAD_PAYLOAD_NOTE_CHANGED = "{\n" +
                "        \"advertisementId\": " + 300003 + ",\n" +
                "        \"note\": \"Beste WG ever!\"\n" +
                "    }\n";

        given()
                .basePath(TestUtils.BASE_PATH_NOTEPAD)
                .header("Content-Type", "application/json")
                .pathParam("userId", USER_ID)
                .body(NOTEPAD_PAYLOAD_NOTE_CHANGED)
                .accept("application/json")
                .when().put()
                .then().statusCode(HttpStatus.OK.value())
                .body("keySet()",
                        containsInAnyOrder("id"))
                .body("id", notNullValue());
    }

    @Test
    void createNotepadStatus400(){
        final int USER_ID = 100002;
        final String NOTEPAD_PAYLOAD_INCOMPLETE = "{\n" +
                "   \"note\":\"Zimmer direkt bei der HS\"\n" +
                "}\n";

        given()
                .basePath(TestUtils.BASE_PATH_NOTEPAD)
                .header("Content-Type", "application/json")
                .pathParam("userId", USER_ID)
                .body(NOTEPAD_PAYLOAD_INCOMPLETE)
                .accept("application/json")
                .when().put()
                .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void getNotepadByUserIdStatus200(){
        final int USER_ID = 100002;

        given()
                .basePath(TestUtils.BASE_PATH_NOTEPAD)
                .header("Content-Type", "application/json")
                .pathParam("userId", USER_ID)
                .accept("application/json")
                .when().get()
                .then().statusCode(HttpStatus.OK.value())
                .body("[0].keySet()", containsInAnyOrder("id", "advertisement", "note"))
                .body("[0].advertisement.keySet()", containsInAnyOrder("id", "type", "category", "user", "title", "description", "price", "location"))
                .body("[0].advertisement.category.keySet()", containsInAnyOrder("id", "name"))
                .body("[0].advertisement.user.keySet()",
                        containsInAnyOrder("id", "email", "firstName", "lastName", "phone", "location"))
                .body("[0].id", notNullValue())
                .body("[0].note", notNullValue())
                .body("[0].advertisement.id", notNullValue())
                .body("[0].advertisement.type", notNullValue())
                .body("[0].advertisement.title", notNullValue())
                .body("[0].advertisement.description", notNullValue())
                .body("[0].advertisement.price", notNullValue())
                .body("[0].advertisement.location", notNullValue())
                .body("[0].advertisement.category.id", notNullValue())
                .body("[0].advertisement.category.name", notNullValue())
                .body("[0].advertisement.user.id", notNullValue())
                .body("[0].advertisement.user.email", notNullValue())
                .body("[0].advertisement.user.firstName", notNullValue())
                .body("[0].advertisement.user.lastName", notNullValue())
                .body("[0].advertisement.user.location", notNullValue())
                .body("[0].advertisement.user.phone", notNullValue());
    }

    @Test
    void getNotepadByUserIdStatus404(){
        final int USER_ID = 100009;

        given()
                .basePath(TestUtils.BASE_PATH_NOTEPAD)
                .header("Content-Type", "application/json")
                .pathParam("userId", USER_ID)
                .accept("application/json")
                .when().get()
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void deleteEntityByUserIdAndAdvertisementIdStatus200(){
        final int USER_ID = 100002;
        final int AD_ID = 300003;

        given()
                .basePath(TestUtils.BASE_PATH_NOTEPAD)
                .header("Content-Type", "application/json")
                .pathParam("userId", USER_ID)
                .queryParam("advertisementId", AD_ID)
                .accept("application/json")
                .when().delete()
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void deleteEntityByUserIdAndAdvertisementIdStatus404(){
        final int USER_ID = 100009;
        final int AD_ID = 300003;

        given()
                .basePath(TestUtils.BASE_PATH_NOTEPAD)
                .header("Content-Type", "application/json")
                .pathParam("userId", USER_ID)
                .queryParam("advertisementId", AD_ID)
                .accept("application/json")
                .when().delete()
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

}

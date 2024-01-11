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
public class UserControllerTest {
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
        TestUtils.insertUserData(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void createUserStatus201() throws SQLException{
        TestUtils.deleteUserFromDatabase("valid@email.com", connection);
        final String CREATE_USER_PAYLOAD = "{\n" +
                "   \"email\":\"" + "valid@email.com" + "\",\n" +
                "   \"password\":\"secret\",\n" +
                "   \"firstName\":\"Thomas\",\n" +
                "   \"lastName\":\"Müller\",\n" +
                "   \"phone\":\"069-123456\",\n" +
                "   \"location\":\"Darmstadt\"\n" +
                "}\n";

        given()
                .basePath(TestUtils.BASE_PATH_USER)
                .header("Content-Type", "application/json")
                .body(CREATE_USER_PAYLOAD)
                .accept("application/json")
                .when().post()
                .then().statusCode(HttpStatus.CREATED.value())
                .body("keySet()",
                        containsInAnyOrder("id", "email", "firstName", "lastName", "phone", "location"))
                .body("id", notNullValue())
                .body("email", equalTo("valid@email.com"))
                .body("firstName", equalTo("Thomas"))
                .body("lastName", equalTo("Müller"))
                .body("location", equalTo("Darmstadt"))
                .body("phone", equalTo("069-123456"));
    }

    @Test
    void createUserStatus400_InvalidPassword(){
        final String CREATE_USER_PAYLOAD_INVALID = "{\n" +
                "   \"email\":\"" + "valid@email.com" + "\",\n" +
                "   \"password\":\"sec\",\n" +
                "   \"firstName\":\"Thomas\",\n" +
                "   \"lastName\":\"Müller\",\n" +
                "   \"phone\":\"069-123456\",\n" +
                "   \"location\":\"Darmstadt\"\n" +
                "}\n";

        given()
                .basePath(TestUtils.BASE_PATH_USER)
                .header("Content-Type", "application/json")
                .body(CREATE_USER_PAYLOAD_INVALID)
                .accept("application/json")
                .when().post()
                .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void createUserStatus400_InvalidName(){
        final String CREATE_USER_PAYLOAD_INVALID = "{\n" +
                "   \"email\":\"" + "valid@email.com" + "\",\n" +
                "   \"password\":\"secret\",\n" +
                "   \"firstName\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Aenean ut magna mollis, ornare sapien vitae, bibendum felis. Duis in libero sit amet mauris malesuada gravida. Nulla facilisi. Vivamus ac elit at nibh egestas auctor. Fusce et efficitur lacus, eget volutpat eros. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Curabitur tristique sit amet orci eu congue. Sed at elit a velit vestibulum rhoncus. Proin at bibendum nisi. Donec non tristique nulla, in convallis orci. Nulla eu ligula lectus. Donec sed tortor sit amet leo placerat facilisis. Donec laoreet felis felis, ac rhoncus nisl sodales vitae.\",\n" +
                "   \"lastName\":\"Müller\",\n" +
                "   \"phone\":\"069-123456\",\n" +
                "   \"location\":\"Darmstadt\"\n" +
                "}\n";

        given()
                .basePath(TestUtils.BASE_PATH_USER)
                .header("Content-Type", "application/json")
                .body(CREATE_USER_PAYLOAD_INVALID)
                .accept("application/json")
                .when().post()
                .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void createUserStatus400_InvalidEmail(){
        final String CREATE_USER_PAYLOAD_INVALID = "{\n" +
                "   \"email\":\"" + "invalid" + "\",\n" +
                "   \"password\":\"secret\",\n" +
                "   \"firstName\":\"Thomas\",\n" +
                "   \"lastName\":\"Müller\",\n" +
                "   \"phone\":\"069-123456\",\n" +
                "   \"location\":\"Darmstadt\"\n" +
                "}\n";

        given()
                .basePath(TestUtils.BASE_PATH_USER)
                .header("Content-Type", "application/json")
                .body(CREATE_USER_PAYLOAD_INVALID)
                .accept("application/json")
                .when().post()
                .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void createUserStatus409(){
        final String CREATE_USER_PAYLOAD_INVALID = "{\n" +
                "   \"email\":\"" + "some@email.de" + "\",\n" +
                "   \"password\":\"secret\",\n" +
                "   \"firstName\":\"Thomas\",\n" +
                "   \"lastName\":\"Müller\",\n" +
                "   \"phone\":\"069-123456\",\n" +
                "   \"location\":\"Darmstadt\"\n" +
                "}\n";

        given()
                .basePath(TestUtils.BASE_PATH_USER)
                .header("Content-Type", "application/json")
                .body(CREATE_USER_PAYLOAD_INVALID)
                .accept("application/json")
                .when().post()
                .then().statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    void getUserByIdStatus200(){
        final int USER_ID = 100002;

        given()
                .basePath(TestUtils.BASE_PATH_USER + "/" + USER_ID)
                .header("Content-Type", "application/json")
                .accept("application/json")
                .when().get()
                .then().statusCode(HttpStatus.OK.value())
                .body("keySet()",
                        containsInAnyOrder("id", "email", "firstName", "lastName", "phone", "location"))
                .body("id",  equalTo(USER_ID))
                .body("email", equalTo("somevalid@email.de"))
                .body("firstName", equalTo("Vorname"))
                .body("lastName", equalTo("Nachname"))
                .body("location", equalTo("Standort"))
                .body("phone", equalTo("06254-call-me-maybe"));
    }

    @Test
    void getUserByIdStatus404(){
        final int USER_ID = 100009;

        given()
                .basePath(TestUtils.BASE_PATH_USER + "/" + USER_ID)
                .header("Content-Type", "application/json")
                .accept("application/json")
                .when().get()
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void getAllUsersStatus200() {
        given()
                .basePath(TestUtils.BASE_PATH_USER)
                .accept("application/json")
                .queryParams("page", 0, "size", 3)
                .when().get()
                .then().statusCode(HttpStatus.OK.value())
                .body("content[0].keySet()",
                        containsInAnyOrder("id", "email", "firstName", "lastName", "phone", "location"))
                .body("content[0].id", notNullValue())
                .body("content[0].email", notNullValue())
                .body("content[0].firstName", notNullValue())
                .body("content[0].lastName", notNullValue())
                .body("content[0].phone", notNullValue())
                .body("content[0].location", notNullValue())
                .body("pageable", notNullValue())
                .body("totalPages", greaterThanOrEqualTo(1))
                .body("totalElements", greaterThanOrEqualTo(1));
    }
}

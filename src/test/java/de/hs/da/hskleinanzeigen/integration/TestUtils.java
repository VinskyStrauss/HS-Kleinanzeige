package de.hs.da.hskleinanzeigen.integration;

import de.hs.da.hskleinanzeigen.entity.*;
import io.restassured.RestAssured;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.*;
import java.util.*;
import java.util.Date;

@SpringBootApplication
public class TestUtils {
    public static final String HOST = "http://localhost";
    public static final String BASE_PATH_CATEGORY = "/hs-kleinanzeigen/api/categories";
    public static final String BASE_PATH_USER = "/hs-kleinanzeigen/api/users";
    public static final String BASE_PATH_NOTEPAD = "/hs-kleinanzeigen/api/users/{userId}/notepad";
    public static final String BASE_PATH_AD = "/hs-kleinanzeigen/api/advertisements";

    public static final int PORT = 8081;

    public static Category category1 = createCategory(200003,"NameA");
    public static Category category2 = createCategory(200004,"NameB");
    public static User user = createUser(100002,"somevalid@email.de","Vorname", "Nachname", "Standort", "pass123supi","06254-call-me-maybe");
    public static Advertisement ad1 = createAd(300003, AdType.OFFER, category1, user, "Titel", "Beschreibung", 42, "Standort");
    public static Advertisement ad2 = createAd(300004, AdType.REQUEST, category1, user, "Titel", "Beschreibung", 42, "Standort");

    public static Notepad notepad = createNotepad(400002, user, ad1, "Notiz");
    public static Category createCategory(int id, String name){
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }

    public static User createUser(int id, String email, String firstName, String lastName, String location, String password, String phone){
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setLocation(location);
        user.setPassword(password);
        user.setPhone(phone);
        return user;
    }

    public static Advertisement createAd(int id, AdType type, Category category, User user, String title, String description,int price,String location){
        Advertisement advertisement = new Advertisement();
        advertisement.setId(id);
        advertisement.setType(type);
        advertisement.setCategory(category);
        advertisement.setUser(user);
        advertisement.setTitle(title);
        advertisement.setDescription(description);
        advertisement.setPrice(price);
        advertisement.setLocation(location);
        return advertisement;
    }

    public static Notepad createNotepad(int id, User user, Advertisement advertisement, String note){
        Notepad notepad = new Notepad();
        notepad.setId(id);
        notepad.setUser(user);
        notepad.setAdvertisement(advertisement);
        notepad.setNote(note);
        return notepad;
    }

    public static void insertUserData(Connection connection) throws SQLException{
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO `KLEINANZEIGEN`.`USER`\n" +
                    "(`ID`,\n" +
                    "`EMAIL`,\n" +
                    "`FIRST_NAME`,\n" +
                    "`LAST_NAME`,\n" +
                    "`LOCATION`,\n" +
                    "`PASSWORD`,\n" +
                    "`PHONE`,\n" +
                    "`CREATED`)\n" +
                    "VALUES\n" +
                    "(100002,\n" +
                    "\"somevalid@email.de\",\n" +
                    "\"Vorname\",\n" +
                    "\"Nachname\",\n" +
                    "\"Standort\",\n" +
                    "\"pass123supi\",\n" +
                    "\"06254-call-me-maybe\",\n" +
                    "NOW());");
        }
    }

    public static void insertCategoryData(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO `KLEINANZEIGEN`.`CATEGORY`" +
                    "(`ID`,\n" +
                    "`NAME`)\n" +
                    "VALUES\n" +
                    "(200003,\n" +
                    "\"NameA\");\n");

            statement.executeUpdate("INSERT INTO `KLEINANZEIGEN`.`CATEGORY`" +
                    "(`ID`,\n" +
                    "`NAME`)\n" +
                    "VALUES\n" +
                    "(200004,\n" +
                    "\"NameB\");\n");
        }
    }

    public static void insertAdvertisementData(Connection connection) throws SQLException {
        insertCategoryData(connection);
        insertUserData(connection);
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO `KLEINANZEIGEN`.`AD`\n" +
                    "(`ID`,\n" +
                    "`TYPE`,\n" +
                    "`CATEGORY_ID`,\n" +
                    "`USER_ID`,\n" +
                    "`TITLE`,\n" +
                    "`DESCRIPTION`,\n" +
                    "`PRICE`,\n" +
                    "`LOCATION`,\n" +
                    "`CREATED`)\n" +
                    "VALUES\n" +
                    "(300003,\n" +
                    "\"OFFER\",\n" +
                    "200003,\n" +
                    "100002,\n" +
                    "\"Titel\",\n" +
                    "\"Beschreibung\",\n" +
                    "42,\n" +
                    "\"Standort\",\n" +
                    "NOW());");

            statement.executeUpdate("INSERT INTO `KLEINANZEIGEN`.`AD`\n" +
                    "(`ID`,\n" +
                    "`TYPE`,\n" +
                    "`CATEGORY_ID`,\n" +
                    "`USER_ID`,\n" +
                    "`TITLE`,\n" +
                    "`DESCRIPTION`,\n" +
                    "`PRICE`,\n" +
                    "`LOCATION`,\n" +
                    "`CREATED`)\n" +
                    "VALUES\n" +
                    "(300004,\n" +
                    "\"REQUEST\",\n" +
                    "200003,\n" +
                    "100002,\n" +
                    "\"Titel\",\n" +
                    "\"Beschreibung\",\n" +
                    "42,\n" +
                    "\"Standort\",\n" +
                    "NOW());");
        }
    }

    public static void insertNotepadData(Connection connection) throws SQLException {
        insertAdvertisementData(connection);
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO `KLEINANZEIGEN`.`NOTEPAD`\n" +
                    "(`ID`,\n" +
                    "`USER_ID`,\n" +
                    "`AD_ID`,\n" +
                    "`NOTE`,\n" +
                    "`CREATED`)\n" +
                    "VALUES\n" +
                    "(400002,\n" +
                    "100002,\n" +
                    "300003,\n" +
                    "\"Notiz\",\n" +
                    "NOW());\n");
        }
    }

    public static void emptyDatabase(Connection connection) throws SQLException {
        deleteNotepadFromDatabase(100002,300003,connection);
        deleteAdFromDatabase(100002,200003,connection);
        deleteCategoryFromDatabase("NameA",connection);
        deleteCategoryFromDatabase("NameB",connection);
        deleteUserFromDatabase("somevalid@email.de",connection);
    }

    public static void deleteUserFromDatabase(String email, Connection connection) throws SQLException {
        final String deleteUser = "DELETE FROM USER WHERE EMAIL = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteUser)) {
            preparedStatement.setString(1, email);
            preparedStatement.executeUpdate();
        }
    }

    public static void deleteCategoryFromDatabase(String name, Connection connection) throws SQLException {
        final String deleteCategory = "DELETE FROM CATEGORY WHERE NAME = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteCategory)) {
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
        }
    }

    public static void deleteAdFromDatabase(int userId, int categoryId, Connection connection) throws SQLException {
        final String deleteAd = "DELETE FROM AD WHERE USER_ID = ? AND CATEGORY_ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteAd)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, categoryId);
            preparedStatement.executeUpdate();
        }
    }

    public static void deleteNotepadFromDatabase(int userId,int adId, Connection connection) throws SQLException {
        final String deleteNote = "DELETE FROM NOTEPAD WHERE USER_ID = ? AND AD_ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteNote)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, adId);
            preparedStatement.executeUpdate();
        }
    }

    public static void setAuthenticationForUser(){
        PreemptiveBasicAuthScheme authScheme = new PreemptiveBasicAuthScheme();
        authScheme.setUserName("user");
        authScheme.setPassword("user");
        RestAssured.authentication = authScheme;
    }
}

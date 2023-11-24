package de.hs.da.hskleinanzeigen.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserPayload {
    private final String email;
    private final String password;
    private final String firstname;
    private final String lastname;
    private final String location;
    private final String phone;

    @JsonCreator
    UserPayload(@JsonProperty("email") String email, @JsonProperty("password") String password, @JsonProperty("firstName") String firstname, @JsonProperty("lastName") String lastname, @JsonProperty("phone") String phone, @JsonProperty("location") String location) {
        if (!checkValueValid(email, password))
            throw new IllegalArgumentException("Invalid values for UserPayload");

        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.location = location;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPhone() {
        return phone;
    }

    public String getLocation() {
        return location;
    }

    private boolean checkValueValid(String email, String password) {
        return checkValueValid(email) && checkValueValid(password);
    }

    private boolean checkValueValid(String value) {
        return value != null && !value.isEmpty();
    }

}

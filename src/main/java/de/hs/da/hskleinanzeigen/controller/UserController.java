package de.hs.da.hskleinanzeigen.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.hs.da.hskleinanzeigen.entity.Category;
import de.hs.da.hskleinanzeigen.entity.User;
import de.hs.da.hskleinanzeigen.exception.EntityIntegrityViolationException;
import de.hs.da.hskleinanzeigen.exception.EntityNotFoundException;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.List;

class UserPayload {
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private String location;
    private String phone;

    @JsonCreator
    UserPayload(@JsonProperty("email") String email,@JsonProperty("password") String password, @JsonProperty("firstName") String firstname, @JsonProperty("lastName") String lastname,@JsonProperty("phone") String phone, @JsonProperty("location") String location) {
        if(!checkValueValid(email,password))
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

    public String getPassword() {return password;}

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

    private boolean checkValueValid(String email,String password) {
        return checkValueValid(email) && checkValueValid(password);
    }
    private boolean checkValueValid(String value) {
        return value != null && !value.isEmpty();
    }

}

@RestController
public class UserController {
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping(path = "/api/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<User> createUser(@RequestBody UserPayload user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent())
            throw new EntityIntegrityViolationException("User",user.getEmail());
        System.out.println(user.getEmail()+" "+user.getPassword()+" "+user.getFirstname()+" "+user.getLastname()+" "+user.getPhone()+" "+user.getLocation());
        userRepository.save(new User(user.getEmail(),user.getPassword(),user.getFirstname(),user.getLastname(),user.getPhone(),user.getLocation()));
        return userRepository.findByEmail(user.getEmail())
                .map(newUser -> ResponseEntity.created(URI.create("/api/users")).body(newUser))
                .orElseThrow(() -> new EntityNotFoundException("User",user.getEmail()));
    }

    @GetMapping(path = "/api/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("User",id));
    }

    @GetMapping(path = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<User> getAllUsers(
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return userRepository.findAll(pageRequest);
    }
}

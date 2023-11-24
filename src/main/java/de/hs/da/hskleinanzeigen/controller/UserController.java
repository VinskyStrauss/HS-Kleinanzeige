package de.hs.da.hskleinanzeigen.controller;

import de.hs.da.hskleinanzeigen.dto.UserPayload;
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

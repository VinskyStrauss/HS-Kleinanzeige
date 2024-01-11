package de.hs.da.hskleinanzeigen.controller;

import de.hs.da.hskleinanzeigen.dto.request.RequestUserDTO;
import de.hs.da.hskleinanzeigen.dto.response.ResponseUserDTO;
import de.hs.da.hskleinanzeigen.exception.EntityNotFoundException;
import de.hs.da.hskleinanzeigen.exception.IllegalEntityException;
import de.hs.da.hskleinanzeigen.mapper.UserMapper;
import de.hs.da.hskleinanzeigen.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;

@RestController
@Secured({"ROLE_ADMIN", "ROLE_USER"})
@Tag(name = "User", description = "Read and set users and their properties")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping(path = "/api/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "User already exists")})
    public ResponseEntity<ResponseUserDTO> createUser(@Parameter(description = "User details to create a new user", required = true)  @RequestBody RequestUserDTO user) {
        if (!checkValueValid(user))
            throw new IllegalEntityException("User",user.getEmail());
        return userService.createUser(userMapper.toEntity(user))
                .map(newUser -> ResponseEntity.created(URI.create("/api/users")).body(userMapper.toResDTO(newUser)))
                .orElseThrow(() -> new EntityNotFoundException("User",user.getEmail()));
    }

    public boolean checkValueValid(RequestUserDTO requestUserDTO) {
        return checkEmailValid(requestUserDTO.getEmail()) && checkPasswordValid(requestUserDTO.getPassword())
                && checkNameValid(requestUserDTO.getFirstName()) && checkNameValid(requestUserDTO.getLastName());
    }

    public boolean checkEmailValid(String email) {
        return checkValueValid(email) && email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }
    public boolean checkPasswordValid(String password) {
        return checkValueValid(password) && password.length() >= 6;
    }

    public boolean checkNameValid(String name) {
        return checkValueValid(name) && name.length() <= 255;
    }

    public boolean checkValueValid(String value) {
        return value != null && !value.isEmpty();
    }

    @GetMapping(path = "/api/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a user by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the user"),
            @ApiResponse(responseCode = "404", description = "User not found")})
    public ResponseEntity<ResponseUserDTO> getUserById(@Parameter(description = "To get user by id") @PathVariable int id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok().body(userMapper.toResDTO(user)))
                .orElseThrow(() -> new EntityNotFoundException("User",id));
    }

    @GetMapping(path = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all users with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the users")})
    public Page<ResponseUserDTO> getAllUsers(
            @Parameter(description = "Page number")
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size
    ) {
        return userService.getAllUsers(page, size)
                .map(userMapper::toResDTO);
    }
}

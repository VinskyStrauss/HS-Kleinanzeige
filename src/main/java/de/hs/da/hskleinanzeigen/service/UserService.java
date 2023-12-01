package de.hs.da.hskleinanzeigen.service;

import de.hs.da.hskleinanzeigen.dto.response.ResponseUserDTO;
import de.hs.da.hskleinanzeigen.entity.User;
import de.hs.da.hskleinanzeigen.exception.EntityIntegrityViolationException;
import de.hs.da.hskleinanzeigen.exception.EntityNotFoundException;
import de.hs.da.hskleinanzeigen.exception.IllegalEntityException;
import de.hs.da.hskleinanzeigen.mapper.UserMapper;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;

    }

    public ResponseEntity<ResponseUserDTO> createUser(User user) {
        if (!checkValueValid(user))
            throw new IllegalEntityException("User",user.getEmail());

        if(userRepository.findByEmail(user.getEmail()).isPresent())
            throw new EntityIntegrityViolationException("User",user.getEmail());
        userRepository.save(user);
        return userRepository.findByEmail(user.getEmail())
                .map(newUser -> ResponseEntity.created(URI.create("/api/users")).body(userMapper.toResDTO(newUser)))
                .orElseThrow(() -> new EntityNotFoundException("User",user.getEmail()));
    }

    private boolean checkValueValid(User user) {
        return checkEmailValid(user.getEmail()) && checkPasswordValid(user.getPassword())
                && checkNameValid(user.getFirstName()) && checkNameValid(user.getLastName());
    }

    private boolean checkEmailValid(String email) {
        return checkValueValid(email) && email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }
    private boolean checkPasswordValid(String password) {
        return checkValueValid(password) && password.length() >= 6;
    }

    private boolean checkNameValid(String name) {
        return checkValueValid(name) && name.length() <= 255;
    }

    private boolean checkValueValid(String value) {
        return value != null && !value.isEmpty();
    }

    public ResponseEntity<ResponseUserDTO> getUserById(int id) {

        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok().body(userMapper.toResDTO(user)))
                .orElseThrow(() -> new EntityNotFoundException("User",id));
    }

    public Page<ResponseUserDTO> getAllUsers(
            int page,
            int size
    ) {

        PageRequest pageRequest = PageRequest.of(page, size);
        return userRepository.findAll(pageRequest)
                .map(userMapper::toResDTO);
    }
}

package de.hs.da.hskleinanzeigen.service;

import de.hs.da.hskleinanzeigen.entity.User;
import de.hs.da.hskleinanzeigen.exception.EntityIntegrityViolationException;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;

@RestController
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> createUser(User user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent())
            throw new EntityIntegrityViolationException("User",user.getEmail());
        userRepository.save(user);
        return userRepository.findByEmail(user.getEmail());
    }

    public Optional<User> getUserById(int id) {

        return userRepository.findById(id);
    }

    public Page<User> getAllUsers(
            int page,
            int size
    ) {

        PageRequest pageRequest = PageRequest.of(page, size);
        return userRepository.findAll(pageRequest);
    }
}

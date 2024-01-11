package de.hs.da.hskleinanzeigen.unit.service;

import de.hs.da.hskleinanzeigen.entity.AdType;
import de.hs.da.hskleinanzeigen.entity.Advertisement;
import de.hs.da.hskleinanzeigen.entity.Category;
import de.hs.da.hskleinanzeigen.entity.User;
import de.hs.da.hskleinanzeigen.exception.EntityIntegrityViolationException;
import de.hs.da.hskleinanzeigen.exception.EntityNotFoundException;
import de.hs.da.hskleinanzeigen.repository.AdvertisementRepository;
import de.hs.da.hskleinanzeigen.repository.CategoryRepository;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import de.hs.da.hskleinanzeigen.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(MockitoExtension.class)
public class UserServiceTest  {
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private final User selectedUser = new User();

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @BeforeEach
    public void setUpObject(){
        selectedUser.setId(123);
        selectedUser.setEmail("testuser@testmail.com");
        selectedUser.setPassword("testpass123");
        selectedUser.setLocation("testUserLocation");
        selectedUser.setPhone("1234567");
        selectedUser.setFirstName("Test");
        selectedUser.setLastName("User");
    }

    private void setUpFindUserByEmailStub(){
        when(userRepository.findByEmail(anyString())).thenAnswer(invocation -> {
            String userEmail = invocation.getArgument(0);
            if (userEmail.equals("testuser@testmail.com")) {
                return Optional.empty();
            }
            return Optional.of("invalid");
        }).thenReturn(Optional.of(selectedUser));
    }

    private void setUpFindUserByIdStub(){
        when(userRepository.findById(anyInt())).thenAnswer(invocation -> {
            int userId = invocation.getArgument(0);
            if (userId == 123) {
                return Optional.of(selectedUser);
            }
            throw new EntityNotFoundException("User of Advertisement",userId);
        });
    }

    @Test
    public void testCreateUser_NameExist() {
        setUpFindUserByEmailStub();
        User invalidUser = new User();
        invalidUser.setEmail("invalidEmail");
        assertThrows(EntityIntegrityViolationException.class,() -> userService.createUser(invalidUser));
    }

    @Test
    public void testCreateUser_UserCreated() {
        setUpFindUserByEmailStub();
        when(userRepository.save(selectedUser)).thenReturn(selectedUser);

        Optional<User> result = userService.createUser(selectedUser);
        verify(userRepository).save(selectedUser);
        assertEquals(result, Optional.of(selectedUser));
    }

    @Test
    public void getUserById_UserNotFound() {
        setUpFindUserByIdStub();
        assertThrows(EntityNotFoundException.class,() -> userService.getUserById(100));
    }

    @Test
    public void getUserById_UserFound() {
        setUpFindUserByIdStub();
        assertEquals(userService.getUserById(123), Optional.of(selectedUser));
    }

    @Test
    public void getAllUsers() {
        List<User> users = new ArrayList<>();
        users.add(selectedUser);
        Page<User> mockPage = new PageImpl<>(users, Pageable.unpaged(), users.size());
        PageRequest pageRequest = PageRequest.of(0, 5);
        when(userRepository.findAll(pageRequest)).thenReturn(mockPage);

        assertEquals(userService.getAllUsers(0,5), mockPage);
        verify(userRepository).findAll(pageRequest);
    }

}

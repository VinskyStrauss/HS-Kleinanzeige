package de.hs.da.hskleinanzeigen.unit.controller;

import de.hs.da.hskleinanzeigen.controller.AdvertisementController;
import de.hs.da.hskleinanzeigen.controller.UserController;
import de.hs.da.hskleinanzeigen.dto.request.RequestAdvertisementDTO;
import de.hs.da.hskleinanzeigen.dto.request.RequestUserDTO;
import de.hs.da.hskleinanzeigen.entity.AdType;
import de.hs.da.hskleinanzeigen.mapper.AdvertisementMapper;
import de.hs.da.hskleinanzeigen.mapper.UserMapper;
import de.hs.da.hskleinanzeigen.repository.AdvertisementRepository;
import de.hs.da.hskleinanzeigen.repository.CategoryRepository;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import de.hs.da.hskleinanzeigen.service.AdvertisementService;
import de.hs.da.hskleinanzeigen.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    private UserController userController;
    @Mock
    private UserService userService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private CacheManager cacheManager;

    @BeforeEach
    public void setUp() {
        userService = mock(UserService.class);
        userMapper = mock(UserMapper.class);
        userController = new UserController(userService, userMapper,cacheManager);
    }

    @Test
    public void testCheckValueValid_AllInvalid() {
        String longString = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Aenean ut magna mollis, ornare sapien vitae, bibendum felis. Duis in libero sit amet mauris malesuada gravida. Nulla facilisi. Vivamus ac elit at nibh egestas auctor. Fusce et efficitur lacus, eget volutpat eros. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Curabitur tristique sit amet orci eu congue. Sed at elit a velit vestibulum rhoncus. Proin at bibendum nisi. Donec non tristique nulla, in convallis orci. Nulla eu ligula lectus. Donec sed tortor sit amet leo placerat facilisis. Donec laoreet felis felis, ac rhoncus nisl sodales vitae.";
        // Empty values
        assertFalse(userController.checkValueValid(new RequestUserDTO()));
        // Contains invalid email
        assertFalse(userController.checkValueValid(new RequestUserDTO(123, "invalid", "validPassword", "First", "Last", "testLocation", "0890182374", null)));
        assertFalse(userController.checkValueValid(new RequestUserDTO(123, "", "validPassword", "First", "Last", "testLocation", "0890182374", null)));
        assertFalse(userController.checkValueValid(new RequestUserDTO(123, null, "validPassword", "First", "Last", "testLocation", "0890182374", null)));
        // Contains invalid password
        assertFalse(userController.checkValueValid(new RequestUserDTO(123, "valid@email.com", "inv", "First", "Last", "testLocation", "0890182374", null)));
        assertFalse(userController.checkValueValid(new RequestUserDTO(123, "valid@email.com", "", "First", "Last", "testLocation", "0890182374", null)));
        assertFalse(userController.checkValueValid(new RequestUserDTO(123, "valid@email.com", null, "First", "Last", "testLocation", "0890182374", null)));
        // Contains invalid FirstName
        assertFalse(userController.checkValueValid(new RequestUserDTO(123, "valid@email.com", "validPassword", longString, "Last", "testLocation", "0890182374", null)));
        assertFalse(userController.checkValueValid(new RequestUserDTO(123, "valid@email.com", "validPassword", "", "Last", "testLocation", "0890182374", null)));
        assertFalse(userController.checkValueValid(new RequestUserDTO(123, "valid@email.com", "validPassword", null, "Last", "testLocation", "0890182374", null)));
        // Contains invalid LastName
        assertFalse(userController.checkValueValid(new RequestUserDTO(123, "valid@email.com", "validPassword", "First", longString, "testLocation", "0890182374", null)));
        assertFalse(userController.checkValueValid(new RequestUserDTO(123, "valid@email.com", "validPassword", "First", "", "testLocation", "0890182374", null)));
        assertFalse(userController.checkValueValid(new RequestUserDTO(123, "valid@email.com", "validPassword", "First", null, "testLocation", "0890182374", null)));
    }

    @Test
    public void testCheckValueValid_IsValid() {
        assertTrue(userController.checkValueValid(new RequestUserDTO(123, "valid@email.com", "validPassword", "First", "Last", "testLocation", "0890182374", null)));
    }

}

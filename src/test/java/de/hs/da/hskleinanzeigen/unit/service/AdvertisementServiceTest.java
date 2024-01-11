package de.hs.da.hskleinanzeigen.unit.service;

import de.hs.da.hskleinanzeigen.entity.AdType;
import de.hs.da.hskleinanzeigen.entity.Advertisement;
import de.hs.da.hskleinanzeigen.entity.Category;
import de.hs.da.hskleinanzeigen.entity.User;
import de.hs.da.hskleinanzeigen.exception.EntityNotFoundException;
import de.hs.da.hskleinanzeigen.repository.AdvertisementRepository;
import de.hs.da.hskleinanzeigen.repository.CategoryRepository;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import de.hs.da.hskleinanzeigen.service.AdvertisementService;
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
public class AdvertisementServiceTest  {
    private AdvertisementService advertisementService;
    @Mock
    private AdvertisementRepository advertisementRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;

    private final Category selectedCategory = new Category();
    private final User selectedUser = new User();
    private final Advertisement selectedAdvertisement = new Advertisement();

    @BeforeEach
    public void setUp() {
        advertisementRepository = mock(AdvertisementRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        userRepository = mock(UserRepository.class);
        advertisementService = new AdvertisementService(advertisementRepository, categoryRepository, userRepository);
    }

    @BeforeEach
    public void setUpObject(){
        // Create mock date
        Calendar calendar = Calendar.getInstance();
        calendar.set(2011, Calendar.NOVEMBER, 11);
        Date date = calendar.getTime();

        // Setup information for mock objects
        // Category
        selectedCategory.setId(123);
        selectedCategory.setName("testCategory");
        // User
        selectedUser.setId(123);
        selectedUser.setEmail("testuser@testmail.com");
        selectedUser.setPassword("testpass123");
        selectedUser.setLocation("testUserLocation");
        selectedUser.setPhone("1234567");
        selectedUser.setFirstName("Test");
        selectedUser.setLastName("User");
        selectedUser.setCreated(date);
        // Advertisement
        selectedAdvertisement.setId(123);
        selectedAdvertisement.setLocation("testAdLocation");
        selectedAdvertisement.setTitle("testTitle");
        selectedAdvertisement.setType(AdType.OFFER);
        selectedAdvertisement.setPrice(100);
        selectedAdvertisement.setDescription("testDesc");
        selectedAdvertisement.setCategory(null);
        selectedAdvertisement.setUser(null);
        selectedAdvertisement.setCreated(date);
    }

    private void setUpFindCategoryByIdStub(){
        when(categoryRepository.findById(anyInt())).thenAnswer(invocation -> {
            int categoryID = invocation.getArgument(0);
            if (categoryID == 123) {
                return Optional.of(selectedCategory);
            }
            throw new EntityNotFoundException("Category of Advertisement",categoryID);
        });
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

    private void setUpCreateAdvertisementRepositoryStubs(){
        // Create stubs for user repository function
        when(advertisementRepository.save(selectedAdvertisement)).thenAnswer(invocation -> {
            selectedAdvertisement.setUser(selectedUser);
            selectedAdvertisement.setCategory(selectedCategory);
            return selectedAdvertisement;
        });
        when(advertisementRepository.findByTitle(anyString())).thenReturn(Optional.of(selectedAdvertisement));
    }

    private void setUpFindAdvertisementByIdStub(){
        when(advertisementRepository.findById(anyInt())).thenAnswer(invocation -> {
            int advertisementId = invocation.getArgument(0);
            if (advertisementId == 123) {
                return Optional.of(selectedAdvertisement);
            }
            throw new EntityNotFoundException("User of Advertisement",advertisementId);
        });
    }

    @Test
    public void testCreateAdvertisement_CategoryNotFound() {
        setUpFindCategoryByIdStub();
        assertThrows(EntityNotFoundException.class,() -> advertisementService.createAdvertisement(selectedAdvertisement,123,100));
        verify(categoryRepository).findById(100);
        verify(userRepository, times(0)).findById(anyInt());
    }

    @Test
    public void testCreateAdvertisement_UserNotFound() {
        setUpFindCategoryByIdStub();
        setUpFindUserByIdStub();
        assertThrows(EntityNotFoundException.class,() -> advertisementService.createAdvertisement(selectedAdvertisement,100,123));
        verify(categoryRepository).findById(123);
        verify(userRepository).findById(100);
    }

    @Test
    public void testCreateAdvertisement_AdvertisementCreated() {
        setUpFindCategoryByIdStub();
        setUpFindUserByIdStub();
        setUpCreateAdvertisementRepositoryStubs();
        Optional<Advertisement> result = advertisementService.createAdvertisement(selectedAdvertisement,123,123);
        selectedAdvertisement.setCategory(selectedCategory);
        selectedAdvertisement.setUser(selectedUser);
        verify(categoryRepository).findById(123);
        verify(categoryRepository).findById(123);
        verify(advertisementRepository).save(selectedAdvertisement);
        assertEquals(result, Optional.of(selectedAdvertisement));
    }

    @Test
    public void getAdvertisementById_AdvertisementNotFound() {
        setUpFindAdvertisementByIdStub();
        assertThrows(EntityNotFoundException.class,() -> advertisementService.getAdvertisementById(100));
        verify(advertisementRepository).findById(100);
    }

    @Test
    public void getAdvertisementById_AdvertisementFound() {
        setUpFindAdvertisementByIdStub();
        assertEquals(advertisementService.getAdvertisementById(123), Optional.of(selectedAdvertisement));
        verify(advertisementRepository).findById(123);
    }

    @Test
    public void getAllAdvertisements_AllParams() {
        List<Advertisement> advertisements = new ArrayList<>();
        advertisements.add(selectedAdvertisement);
        Page<Advertisement> mockPage = new PageImpl<>(advertisements, Pageable.unpaged(), advertisements.size());
        PageRequest pageRequest = PageRequest.of(0, 5);
        when(advertisementRepository.findByQuery(AdType.OFFER, 123, 100, 200, pageRequest)).thenReturn(mockPage);

        assertEquals(advertisementService.getAllAdvertisements(AdType.OFFER, 123, 100, 200, 0, 5), mockPage);
        verify(advertisementRepository).findByQuery(AdType.OFFER, 123, 100, 200, pageRequest);
    }
}

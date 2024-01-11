package de.hs.da.hskleinanzeigen.unit.controller;

import de.hs.da.hskleinanzeigen.controller.AdvertisementController;
import de.hs.da.hskleinanzeigen.dto.request.RequestAdvertisementDTO;
import de.hs.da.hskleinanzeigen.entity.AdType;
import de.hs.da.hskleinanzeigen.mapper.AdvertisementMapper;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(MockitoExtension.class)
public class AdvertisementControllerTest {
    private AdvertisementController advertisementController;
    @Mock
    private AdvertisementService advertisementService;
    @Mock
    private AdvertisementMapper advertisementMapper;

    @BeforeEach
    public void setUp() {
        advertisementService = mock(AdvertisementService.class);
        advertisementMapper = mock(AdvertisementMapper.class);
        advertisementController = new AdvertisementController(advertisementService, advertisementMapper);
    }

    @Test
    public void testCheckValueValid_AllInvalid(){
        // Empty values
        assertFalse(advertisementController.checkValueValid(new RequestAdvertisementDTO()));
        // Contains invalid number (<-1)
        assertFalse(advertisementController.checkValueValid(new RequestAdvertisementDTO(-1, AdType.OFFER, 0, 123, "testAdvertisement", "testDescription", 100, "testLocation")));
        // Contains invalid string (empty or null)
        assertFalse(advertisementController.checkValueValid(new RequestAdvertisementDTO(-1, AdType.OFFER, 1, 0, null, null, 100, null)));
    }

    @Test
    public void testCheckValueValid_String(){
        assertFalse(advertisementController.checkValueValid(""));
        assertFalse(advertisementController.checkValueValid((String) null));
        assertTrue(advertisementController.checkValueValid("test"));
    }

    @Test
    public void testCheckValueValid_IsValid(){
        assertTrue(advertisementController.checkValueValid(new RequestAdvertisementDTO(123, AdType.OFFER, 123, 123, "testAdvertisement", "testDescription", 100, "testLocation")));
        assertTrue(advertisementController.checkValueValid(new RequestAdvertisementDTO(123, AdType.REQUEST, 123, 123, "testAdvertisement", "testDescription", 100, "testLocation")));
    }


}

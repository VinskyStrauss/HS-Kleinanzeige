package de.hs.da.hskleinanzeigen.unit.service;

import de.hs.da.hskleinanzeigen.entity.*;
import de.hs.da.hskleinanzeigen.exception.EntityNotFoundException;
import de.hs.da.hskleinanzeigen.repository.AdvertisementRepository;
import de.hs.da.hskleinanzeigen.repository.NotepadRepository;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import de.hs.da.hskleinanzeigen.service.NotepadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(MockitoExtension.class)
public class NotepadServiceTest  {
    private NotepadService notepadService;
    @Mock
    private AdvertisementRepository advertisementRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotepadRepository notepadRepository;

    private final User newUser = new User();
    private final User existingUser = new User();
    private final Advertisement newAdvertisement = new Advertisement();
    private final Advertisement existingAdvertisement = new Advertisement();

    private final Notepad newNotepad = new Notepad();
    private final Notepad existingNotepad = new Notepad();

    @BeforeEach
    public void setUp() {
        advertisementRepository = mock(AdvertisementRepository.class);
        userRepository = mock(UserRepository.class);
        notepadRepository = mock(NotepadRepository.class);
        notepadService = new NotepadService(advertisementRepository, userRepository, notepadRepository);
    }

    @BeforeEach
    public void setUpObject(){
        // Create mock date
        Calendar calendar = Calendar.getInstance();
        calendar.set(2011, Calendar.NOVEMBER, 11);
        Date date = calendar.getTime();

        // Setup information for mock objects
        // new User
        newUser.setId(123);
        newUser.setEmail("testuser@testmail.com");
        newUser.setPassword("testpass123");
        newUser.setLocation("testUserLocation");
        newUser.setPhone("1234567");
        newUser.setFirstName("Test");
        newUser.setLastName("User");
        newUser.setCreated(date);
        // existing User
        existingUser.setId(125);
        // Advertisement
        newAdvertisement.setId(123);
        newAdvertisement.setLocation("testAdLocation");
        newAdvertisement.setTitle("testTitle");
        newAdvertisement.setType(AdType.OFFER);
        newAdvertisement.setPrice(100);
        newAdvertisement.setDescription("testDesc");
        newAdvertisement.setCategory(null);
        newAdvertisement.setUser(null);
        newAdvertisement.setCreated(date);
        // existing Advertisement
        existingAdvertisement.setId(125);
        //Notepad
        newNotepad.setId(123);
        newNotepad.setNote("testNote");
        newNotepad.setAdvertisement(null);
        newNotepad.setUser(null);
        //Existing Notepad
        existingNotepad.setId(125);
        existingNotepad.setNote("existNote");
        existingNotepad.setAdvertisement(newAdvertisement);
        existingNotepad.setUser(newUser);
    }

    private void setUpFindUserByIdStub(){
        when(userRepository.findById(anyInt())).thenAnswer(invocation -> {
            int userId = invocation.getArgument(0);
            if (userId == 123) {
                return Optional.of(newUser);
            }
            if (userId == 125) {
                return Optional.of(existingUser);
            }
            throw new EntityNotFoundException("User of Notepad",userId);
        });
    }

    private void setUpFindAdvertisementByIdStub(){
        when(advertisementRepository.findById(anyInt())).thenAnswer(invocation -> {
            int advertisementId = invocation.getArgument(0);
            if (advertisementId == 123) {
                return Optional.of(newAdvertisement);
            }
            if (advertisementId == 125) {
                return Optional.of(existingAdvertisement);
            }
            throw new EntityNotFoundException("User of Notepad",advertisementId);
        });
    }

    private void setUpCreateNotepadStub(){
        when(notepadRepository.findByUserAndAdvertisement(any(User.class),any(Advertisement.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            Advertisement advertisement = invocation.getArgument(1);
            if(user.getId() == 125 && advertisement.getId() == 125){
                return Optional.of(existingNotepad);
            }
            if(user.getId() == 123 && advertisement.getId() == 123){
                return Optional.empty();
            }
            throw new EntityNotFoundException("Notepad",1);
        }).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            if(user.getId() == 125){
                return Optional.of(existingNotepad);
            }
            return Optional.of(newNotepad);
        });
        when(notepadRepository.save(any(Notepad.class))).thenAnswer(invocation -> {
            Notepad notepad = invocation.getArgument(0);
            if (notepad.getId() == 123){
                notepad.setUser(newUser);
                notepad.setAdvertisement(newAdvertisement);
                return newNotepad;
            }
            return existingNotepad;
        });
    }

    private void setUpFindNotepadByUserStub(){
        when(notepadRepository.findByUser(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            if (user.getId() == 123) {
                List<Notepad> notepads = new ArrayList<>();
                notepads.add(newNotepad);
                return Optional.of(notepads);
            }
            throw new EntityNotFoundException("User of Notepad",user.getId());
        });
    }

    @Test
    public void testCreateNotepad_UserNotFound() {
        setUpFindUserByIdStub();
        assertThrows(EntityNotFoundException.class,() -> notepadService.createNotepad(100,125, existingNotepad));
        verify(userRepository).findById(100);
        verify(advertisementRepository, times(0)).findById(anyInt());
    }

    @Test
    public void testCreateNotepad_AdvertisementNotFound() {
        setUpFindUserByIdStub();
        setUpFindAdvertisementByIdStub();
        assertThrows(EntityNotFoundException.class,() -> notepadService.createNotepad(125,100, existingNotepad));
        verify(userRepository).findById(125);
        verify(advertisementRepository).findById(100);
    }

    @Test
    public void testCreateAdvertisement_ExistingNotepadModified() {
        setUpFindUserByIdStub();
        setUpFindAdvertisementByIdStub();
        setUpCreateNotepadStub();
        Optional<Notepad> result = notepadService.createNotepad(125,125, existingNotepad);
        verify(userRepository).findById(125);
        verify(advertisementRepository).findById(125);
        verify(notepadRepository).save(existingNotepad);
        assertEquals(result, Optional.of(existingNotepad));
    }

    @Test
    public void testCreateAdvertisement_NewNotepadCreated() {
        setUpFindUserByIdStub();
        setUpFindAdvertisementByIdStub();
        setUpCreateNotepadStub();
        Optional<Notepad> result = notepadService.createNotepad(123,123, newNotepad);
        newNotepad.setUser(newUser);
        newNotepad.setAdvertisement(newAdvertisement);
        verify(userRepository).findById(123);
        verify(advertisementRepository).findById(123);
        verify(notepadRepository).save(newNotepad);
        assertEquals(result, Optional.of(newNotepad));
    }

    @Test
    public void testGetNotepadByUserId_UserNotFound() {
        setUpFindUserByIdStub();
        assertThrows(EntityNotFoundException.class,() -> notepadService.getNotepadByUserId(100));
        verify(userRepository).findById(100);
    }

    @Test
    public void testGetNotepadByUserId_NotepadNotFound() {
        setUpFindUserByIdStub();
        setUpFindNotepadByUserStub();
        assertThrows(EntityNotFoundException.class,() -> notepadService.getNotepadByUserId(125));
        verify(userRepository).findById(125);
    }

    @Test
    public void testGetNotepadByUserId_NotepadFound() {
        setUpFindUserByIdStub();
        setUpFindNotepadByUserStub();
        List<Notepad> notepads = new ArrayList<>();
        notepads.add(newNotepad);
        assertEquals(notepadService.getNotepadByUserId(123), Optional.of(notepads));
        verify(userRepository).findById(123);
    }

    @Test
    public void testDeleteEntityByUserIdAndAdvertisementId_UserNotFound() {
        setUpFindUserByIdStub();
        assertThrows(EntityNotFoundException.class,() -> notepadService.deleteEntityByUserIdAndAdvertisementId(100,125));
        verify(userRepository).findById(100);
        verify(advertisementRepository, times(0)).findById(anyInt());
    }

    @Test
    public void testDeleteEntityByUserIdAndAdvertisementId_AdvertisementNotFound() {
        setUpFindUserByIdStub();
        setUpFindAdvertisementByIdStub();
        assertThrows(EntityNotFoundException.class,() -> notepadService.deleteEntityByUserIdAndAdvertisementId(125,100));
        verify(userRepository).findById(125);
        verify(advertisementRepository).findById(100);
    }

    @Test
    public void testDeleteEntityByUserIdAndAdvertisementId_NotepadDeleted() {
        setUpFindUserByIdStub();
        setUpFindAdvertisementByIdStub();
            notepadService.deleteEntityByUserIdAndAdvertisementId(125,125);
        verify(userRepository).findById(125);
        verify(advertisementRepository).findById(125);
        verify(notepadRepository).deleteByUserAndAdvertisement(existingUser,existingAdvertisement);
    }
}

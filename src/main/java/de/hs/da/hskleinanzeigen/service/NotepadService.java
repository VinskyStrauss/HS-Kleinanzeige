package de.hs.da.hskleinanzeigen.service;

import de.hs.da.hskleinanzeigen.entity.Advertisement;
import de.hs.da.hskleinanzeigen.entity.Notepad;
import de.hs.da.hskleinanzeigen.entity.User;
import de.hs.da.hskleinanzeigen.exception.EntityNotFoundException;
import de.hs.da.hskleinanzeigen.exception.IllegalEntityException;
import de.hs.da.hskleinanzeigen.mapper.NotepadMapper;
import de.hs.da.hskleinanzeigen.repository.AdvertisementRepository;
import de.hs.da.hskleinanzeigen.repository.NotepadRepository;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class NotepadService {
    private final AdvertisementRepository advertisementRepository;
    private final UserRepository userRepository;
    private final NotepadRepository notepadRepository;


    @Autowired
    public NotepadService(AdvertisementRepository advertisementRepository, UserRepository userRepository, NotepadRepository notepadRepository, NotepadMapper notepadMapper) {
        this.advertisementRepository = advertisementRepository;
        this.userRepository = userRepository;
        this.notepadRepository = notepadRepository;
    }

    public Optional<Notepad> createNotepad(int userId, int advertisementId, Notepad notepad) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalEntityException("User of Notepad",String.valueOf(userId)));
        Advertisement advertisement = advertisementRepository.findById(advertisementId).orElseThrow(() -> new IllegalEntityException("Advertisement of Notepad",String.valueOf(advertisementId)));


        Notepad existingNotepad = notepadRepository.findByUserAndAdvertisement(user, advertisement).orElse(null);
        if (existingNotepad != null) {
            existingNotepad.setNote(notepad.getNote());
        }

        notepad.setUser(user);
        notepad.setAdvertisement(advertisement);

        notepadRepository.save(existingNotepad != null ? existingNotepad : notepad);
        return notepadRepository.findByUserAndAdvertisement(user, advertisement);
    }

    public Optional<List<Notepad>> getNotepadByUserId(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User of Notepad",userId));



        return notepadRepository.findByUser(user);
    }

    public ResponseEntity<Void> deleteEntityByUserIdAndAdvertisementId(int userId, int advertisementId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User of Notepad",userId));
        Advertisement advertisement = advertisementRepository.findById(advertisementId).orElseThrow(() -> new EntityNotFoundException("Advertisement of Notepad",advertisementId));

        notepadRepository.deleteByUserAndAdvertisement(user, advertisement);
        return ResponseEntity.noContent().build();
    }


}

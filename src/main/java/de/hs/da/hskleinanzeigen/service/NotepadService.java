package de.hs.da.hskleinanzeigen.service;

import de.hs.da.hskleinanzeigen.dto.request.RequestNotepadDTO;
import de.hs.da.hskleinanzeigen.dto.response.ResponseNotepadDTO;
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class NotepadService {
    private final AdvertisementRepository advertisementRepository;
    private final UserRepository userRepository;
    private final NotepadRepository notepadRepository;

    private final NotepadMapper notepadMapper;

    @Autowired
    public NotepadService(AdvertisementRepository advertisementRepository, UserRepository userRepository, NotepadRepository notepadRepository, NotepadMapper notepadMapper) {
        this.advertisementRepository = advertisementRepository;
        this.userRepository = userRepository;
        this.notepadRepository = notepadRepository;
        this.notepadMapper = notepadMapper;
    }

    public ResponseEntity<Map<String, Integer>> createNotepad(int userId, RequestNotepadDTO notepad) {
        notepad.setUserId(userId);
        User user = userRepository.findById(notepad.getUserId()).orElseThrow(() -> new IllegalEntityException("User of Notepad",String.valueOf(notepad.getUserId())));
        Advertisement advertisement = advertisementRepository.findById(notepad.getAdvertisementId()).orElseThrow(() -> new IllegalEntityException("Advertisement of Notepad",String.valueOf(notepad.getAdvertisementId())));

        Notepad existingNotepad = notepadRepository.findByUserAndAdvertisement(user, advertisement).orElse(null);
        if (existingNotepad != null) {
            existingNotepad.setNote(notepad.getNote());
        }

        Notepad createdNotepad = notepadMapper.toEntity(notepad);
        createdNotepad.setUser(user);
        createdNotepad.setAdvertisement(advertisement);

        notepadRepository.save(existingNotepad != null ? existingNotepad : createdNotepad);
        return notepadRepository.findByUserAndAdvertisement(user, advertisement)
                .map(newNotepad -> ResponseEntity.ok().body(Collections.singletonMap("id", newNotepad.getId())))
                .orElseThrow(() -> new EntityNotFoundException("Notepad", notepad.getUserId() + "/" + notepad.getAdvertisementId()));
    }

    public ResponseEntity<List<ResponseNotepadDTO>> getNotepadByUserId(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User of Notepad",userId));


        List<ResponseNotepadDTO> notepads = notepadRepository.findByUser(user)
                .stream()
                .flatMap(Collection::stream)
                .map(notepadMapper::toResNotepadDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(notepads);
    }

    public ResponseEntity<Void> deleteEntityByUserIdAndAdvertisementId(int userId, @RequestParam(name = "advertisementId", required = true) int advertisementId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User of Notepad",userId));
        Advertisement advertisement = advertisementRepository.findById(advertisementId).orElseThrow(() -> new EntityNotFoundException("Advertisement of Notepad",advertisementId));

        notepadRepository.deleteByUserAndAdvertisement(user, advertisement);
        return ResponseEntity.noContent().build();
    }


}

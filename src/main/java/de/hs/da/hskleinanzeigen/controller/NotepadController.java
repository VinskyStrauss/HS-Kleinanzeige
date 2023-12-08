package de.hs.da.hskleinanzeigen.controller;

import de.hs.da.hskleinanzeigen.dto.request.RequestNotepadDTO;
import de.hs.da.hskleinanzeigen.dto.response.ResponseNotepadDTO;
import de.hs.da.hskleinanzeigen.exception.EntityNotFoundException;
import de.hs.da.hskleinanzeigen.mapper.NotepadMapper;
import de.hs.da.hskleinanzeigen.service.NotepadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
@Tag(name = "Notepad", description = "Read and set notepad entries")
public class NotepadController {
    private final NotepadService notepadService;

    private final NotepadMapper notepadMapper;

    @Autowired
    public NotepadController(NotepadService notepadService, NotepadMapper notepadMapper) {
        this.notepadService = notepadService;
        this.notepadMapper = notepadMapper;
    }

    @PutMapping(path = "/api/users/{userId}/notepad", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    @Operation(summary = "Create or update notepad entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notepad entry created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")})
    public ResponseEntity<Map<String, Integer>> createNotepad(@Parameter(description = "Notepad details to create a new notepad") @PathVariable("userId") int userId, @RequestBody RequestNotepadDTO notepad) {
        notepad.setUserId(userId);
        return notepadService.createNotepad(userId, notepad.getAdvertisementId(), notepadMapper.toEntity(notepad))
                .map(newNotepad -> ResponseEntity.ok().body(Collections.singletonMap("id", newNotepad.getId())))
                .orElseThrow(() -> new EntityNotFoundException("Notepad", userId + "/" + notepad.getAdvertisementId()));
    }

    @GetMapping(path = "/api/users/{userId}/notepad", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "Get notepad by user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the notepad"),
            @ApiResponse(responseCode = "204", description = "Notepad not found"),
            @ApiResponse(responseCode = "404", description = "User not found"),
    })
    public ResponseEntity<List<ResponseNotepadDTO>> getNotepadByUserId(@Parameter(description = "To get notepad by user id") @PathVariable int userId) {
        List<ResponseNotepadDTO> notepads = notepadService.getNotepadByUserId(userId)
                .stream()
                .flatMap(Collection::stream)
                .map(notepadMapper::toResNotepadDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(notepads);
    }

    @DeleteMapping(path = "/api/users/{userId}/notepad", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @Transactional
    @Operation(summary = "Delete notepad entry by user id and advertisement id")
    @ApiResponse(responseCode = "204", description = "Notepad entry not found")
    public ResponseEntity<Void> deleteEntityByUserIdAndAdvertisementId(@Parameter(description = "To delete notepad") @PathVariable int userId, @RequestParam(name = "advertisementId", required = true) int advertisementId) {
        return notepadService.deleteEntityByUserIdAndAdvertisementId(userId, advertisementId);
    }


}

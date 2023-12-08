package de.hs.da.hskleinanzeigen.mapper;

import de.hs.da.hskleinanzeigen.dto.request.RequestNotepadDTO;
import de.hs.da.hskleinanzeigen.dto.response.ResponseNotepadDTO;
import de.hs.da.hskleinanzeigen.entity.Notepad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotepadMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    Notepad toEntity(RequestNotepadDTO notepadDTO);

    ResponseNotepadDTO toResNotepadDTO(Notepad notepad);
}

package de.hs.da.hskleinanzeigen.mapper;

import de.hs.da.hskleinanzeigen.dto.request.RequestUserDTO;
import de.hs.da.hskleinanzeigen.dto.response.ResponseUserDTO;
import de.hs.da.hskleinanzeigen.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    User toEntity(RequestUserDTO userDTO);

    ResponseUserDTO toResDTO(User user);
}

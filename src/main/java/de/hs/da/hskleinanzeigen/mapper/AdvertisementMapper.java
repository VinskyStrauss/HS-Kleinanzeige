package de.hs.da.hskleinanzeigen.mapper;

import de.hs.da.hskleinanzeigen.dto.request.RequestAdvertisementDTO;
import de.hs.da.hskleinanzeigen.dto.response.ResponseAdvertisementDTO;
import de.hs.da.hskleinanzeigen.entity.Advertisement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdvertisementMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    Advertisement toEntity(RequestAdvertisementDTO advertisementDTO);
    ResponseAdvertisementDTO toResDTO(Advertisement advertisement);
}

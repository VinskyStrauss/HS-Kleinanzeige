package de.hs.da.hskleinanzeigen.dto.response;

import de.hs.da.hskleinanzeigen.dto.CategoryDTO;
import de.hs.da.hskleinanzeigen.entity.AdType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseAdvertisementDTO {
    private int id;
    private AdType type;
    private CategoryDTO category;
    private ResponseUserDTO user;
    private String title;
    private String description;
    private int price;
    private String location;
}
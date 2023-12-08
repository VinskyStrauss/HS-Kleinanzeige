package de.hs.da.hskleinanzeigen.dto.request;

import de.hs.da.hskleinanzeigen.entity.AdType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RequestAdvertisementDTO {
    private int id;
    private AdType type;
    private int categoryId;
    private int userId;
    private String title;
    private String description;
    private int price;
    private String location;
}





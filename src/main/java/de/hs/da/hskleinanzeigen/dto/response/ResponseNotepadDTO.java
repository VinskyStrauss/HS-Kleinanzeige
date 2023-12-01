package de.hs.da.hskleinanzeigen.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseNotepadDTO {
    private int id;
    private ResponseUserDTO user;
    private ResponseAdvertisementDTO advertisement;
    private String note;
}

package de.hs.da.hskleinanzeigen.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RequestNotepadDTO {
    private int userId;
    private int advertisementId;
    private String note;
}

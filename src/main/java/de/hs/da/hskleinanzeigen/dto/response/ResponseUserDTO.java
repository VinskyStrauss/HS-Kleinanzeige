package de.hs.da.hskleinanzeigen.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseUserDTO {
    private int id;
    private String email;
    private String firstName;
    private String lastName;
    private String location;
    private String phone;
}

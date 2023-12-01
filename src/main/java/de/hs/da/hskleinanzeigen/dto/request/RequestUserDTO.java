package de.hs.da.hskleinanzeigen.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RequestUserDTO {
    private int id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String location;
    private String phone;
    private Date created;
}

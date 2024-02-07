package de.hs.da.hskleinanzeigen.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseUserDTO implements Serializable {
    private int id;
    private String email;
    private String firstName;
    private String lastName;
    private String location;
    private String phone;
}

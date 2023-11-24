package de.hs.da.hskleinanzeigen.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.hs.da.hskleinanzeigen.exception.IllegalEntityException;

public record CategoryPayload(String name) {
    @JsonCreator
    public CategoryPayload(@JsonProperty("name") String name) {
        if (name == null || name.isEmpty())
            throw new IllegalEntityException("CategoryPayload", "?", "Name must not be empty");
        this.name = name;
    }
}

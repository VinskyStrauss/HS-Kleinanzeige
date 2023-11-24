package de.hs.da.hskleinanzeigen.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.hs.da.hskleinanzeigen.entity.AdType;
import de.hs.da.hskleinanzeigen.exception.IllegalEntityException;

public class AdvertisementPayload {
    private final AdType type;
    private final int category_id;
    private final int user_id;
    private final String title;
    private final String description;
    private final int price;
    private final String location;

    public AdType getType() {
        return type;
    }

    public int getCategoryID() {
        return category_id;
    }

    public int getUserID() {
        return user_id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public String getLocation() {
        return location;
    }

    @JsonCreator
    AdvertisementPayload(@JsonProperty("type") String type, @JsonProperty("categoryId") int categoryId, @JsonProperty("userId") int userId, @JsonProperty("title") String title, @JsonProperty("description") String description, @JsonProperty("price") int price, @JsonProperty("location") String location) {
        if (!checkValueValid(type, categoryId, userId, title, description, price, location))
            throw new IllegalEntityException("AdvertisementPayload", title);
        if (!type.equals("OFFER") && !type.equals("REQUEST"))
            throw new IllegalEntityException("AdvertisementPayload", title, "Invalid type for AdvertisementPayload (must be OFFER or REQUEST)");
        this.type = type.equals("OFFER") ? AdType.OFFER : AdType.REQUEST;
        this.category_id = categoryId;
        this.user_id = userId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.location = location;
    }

    private boolean checkValueValid(String type, int categoryId, int userId, String title, String description, int price, String location) {
        return checkValueValid(type) && checkValueValid(categoryId)
                && checkValueValid(userId) && checkValueValid(title)
                && checkValueValid(description) && checkValueValid(price)
                && checkValueValid(location);
    }

    private boolean checkValueValid(String value) {
        return value != null && !value.isEmpty();
    }

    private boolean checkValueValid(int value) {
        return value > 0;
    }
}

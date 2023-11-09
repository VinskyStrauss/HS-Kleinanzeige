package de.hs.da.hskleinanzeigen;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

class AdvertisementPayload{
    private AdType type;
    private int category_id;
    private String title;
    private String description;
    private int price;
    private String location;

    public AdType getType() {
        return type;
    }

    public int getCategoryID() {
        return category_id;
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
    AdvertisementPayload(@JsonProperty("type") String type,@JsonProperty("categoryId") int categoryId,@JsonProperty("title") String title,@JsonProperty("description") String description,@JsonProperty("price") int price,@JsonProperty("location") String location) {
        if(!checkValueValid(type, categoryId, title, description, price, location))
            throw new IllegalArgumentException("Invalid values for AdvertisementPayload");
        if (!type.equals("OFFER") && !type.equals("REQUEST"))
            throw new IllegalArgumentException("Invalid type for AdvertisementPayload (must be OFFER or REQUEST)");
        System.out.println(categoryId);
        this.type = type.equals("OFFER") ? AdType.OFFER : AdType.REQUEST;
        this.category_id = categoryId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.location = location;
    }

    private boolean checkValueValid(String type, int categoryId, String title, String description, int price, String location) {
        return checkValueValid(type) && checkValueValid(categoryId)
                && checkValueValid(title) && checkValueValid(description)
                && checkValueValid(price) && checkValueValid(location);
    }
    private boolean checkValueValid(String value) {
        return value != null && !value.isEmpty();
    }

    private boolean checkValueValid(int value) {
        return value > 0;
    }
}

@RestController
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class AdvertisementController {
    private final AdvertisementRepository advertisementRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public AdvertisementController(AdvertisementRepository advertisementRepository, CategoryRepository categoryRepository) {
        this.advertisementRepository = advertisementRepository;
        this.categoryRepository = categoryRepository;
    }

    @PostMapping(path = "/api/advertisements", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<Advertisement> createAdvertisement(@RequestBody AdvertisementPayload advertisement) {
        Category category = categoryRepository.findById(advertisement.getCategoryID()).orElseThrow(() -> new NullPointerException("Category not found"));
        advertisementRepository.saveAndFlush(new Advertisement(advertisement.getType(), category, advertisement.getTitle(), advertisement.getDescription(), advertisement.getPrice(), advertisement.getLocation()));
        Advertisement createdAd = advertisementRepository.findByTitle(advertisement.getTitle());
        if (createdAd != null) {
            return ResponseEntity.status(201).body(createdAd);
        } else {
            throw new IllegalArgumentException("Bad Payload");
        }
    }

    @GetMapping(path = "/api/advertisements/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<Advertisement> getAdvertisementById(@PathVariable int id) {
        return advertisementRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NullPointerException("Advertisement not found"));
    }

    @GetMapping(path = "/api/advertisements", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public List<Advertisement> getAllAdvertisements() {
        return advertisementRepository.findAll();
    }
}

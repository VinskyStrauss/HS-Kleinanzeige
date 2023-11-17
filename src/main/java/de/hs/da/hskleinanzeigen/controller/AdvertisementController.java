package de.hs.da.hskleinanzeigen.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.hs.da.hskleinanzeigen.entity.User;
import de.hs.da.hskleinanzeigen.exception.EntityNotFoundException;
import de.hs.da.hskleinanzeigen.exception.IllegalEntityException;
import de.hs.da.hskleinanzeigen.repository.AdvertisementRepository;
import de.hs.da.hskleinanzeigen.entity.Category;
import de.hs.da.hskleinanzeigen.repository.CategoryRepository;
import de.hs.da.hskleinanzeigen.entity.AdType;
import de.hs.da.hskleinanzeigen.entity.Advertisement;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.List;

class AdvertisementPayload{
    private AdType type;
    private int category_id;
    private int user_id;
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

    public int getUserID() {return user_id;}

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
    AdvertisementPayload(@JsonProperty("type") String type,@JsonProperty("categoryId") int categoryId,@JsonProperty("userId") int userId,@JsonProperty("title") String title,@JsonProperty("description") String description,@JsonProperty("price") int price,@JsonProperty("location") String location) {
        if(!checkValueValid(type, categoryId, userId, title, description, price, location))
            throw new IllegalEntityException("AdvertisementPayload",title);
        if (!type.equals("OFFER") && !type.equals("REQUEST"))
            throw new IllegalEntityException("AdvertisementPayload",title,"Invalid type for AdvertisementPayload (must be OFFER or REQUEST)");
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

@RestController
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class AdvertisementController {
    private final AdvertisementRepository advertisementRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public AdvertisementController(AdvertisementRepository advertisementRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.advertisementRepository = advertisementRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @PostMapping(path = "/api/advertisements", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<Advertisement> createAdvertisement(@RequestBody AdvertisementPayload advertisement) {
        Category category = categoryRepository.findById(advertisement.getCategoryID()).orElseThrow(() -> new EntityNotFoundException("Category of Advertisement",advertisement.getCategoryID()));
        User user = userRepository.findById(advertisement.getUserID()).orElseThrow(() -> new EntityNotFoundException("User of Advertisement",advertisement.getUserID()));

        advertisementRepository.save(new Advertisement(advertisement.getType(), category,user, advertisement.getTitle(), advertisement.getDescription(), advertisement.getPrice(), advertisement.getLocation()));
        return advertisementRepository.findByTitle(advertisement.getTitle())
                .map(newAdvertisement -> ResponseEntity.created(URI.create("/api/advertisements")).body(newAdvertisement))
                .orElseThrow(() -> new EntityNotFoundException("Advertisement",advertisement.getTitle()));
    }

    @GetMapping(path = "/api/advertisements/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<Advertisement> getAdvertisementById(@PathVariable int id) {
        System.out.println("id: " + id + " " + advertisementRepository.findById(id));
        return advertisementRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Advertisement",id));
    }

    @GetMapping(path = "/api/advertisements", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public Page<Advertisement> getAllAdvertisements(
            @RequestParam(name = "type", required = false) AdType type,
            @RequestParam(name = "category", required = false) Integer categoryId,
            @RequestParam(name = "priceFrom", required = false, defaultValue = "0") Integer priceFrom,
            @RequestParam(name = "priceTo", required = false, defaultValue = "2147483647") Integer priceTo,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return advertisementRepository.findByQuery(type, categoryId, priceFrom, priceTo, pageRequest);
    }
}

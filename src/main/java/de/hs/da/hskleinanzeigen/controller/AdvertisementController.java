package de.hs.da.hskleinanzeigen.controller;

import de.hs.da.hskleinanzeigen.dto.AdvertisementPayload;
import de.hs.da.hskleinanzeigen.entity.User;
import de.hs.da.hskleinanzeigen.exception.EntityNotFoundException;
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

        return advertisementRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Advertisement",id));
    }

    @GetMapping(path = "/api/advertisements", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public Page<Advertisement> getAllAdvertisements(
            @RequestParam(name = "type", required = false) AdType type,
            @RequestParam(name = "categoryId", required = false) Integer categoryId,
            @RequestParam(name = "priceFrom", required = false, defaultValue = "0") Integer priceFrom,
            @RequestParam(name = "priceTo", required = false, defaultValue = "2147483647") Integer priceTo,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return advertisementRepository.findByQuery(type, categoryId, priceFrom, priceTo, pageRequest);
    }
}

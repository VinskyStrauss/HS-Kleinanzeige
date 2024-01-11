package de.hs.da.hskleinanzeigen.service;

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
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

@RestController
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class AdvertisementService {
    private final AdvertisementRepository advertisementRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public AdvertisementService(AdvertisementRepository advertisementRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.advertisementRepository = advertisementRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public Optional<Advertisement> createAdvertisement(Advertisement advertisement, int userId, int categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new EntityNotFoundException("Category of Advertisement",categoryId));
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User of Advertisement",userId));

        advertisement.setCategory(category);
        advertisement.setUser(user);

        advertisementRepository.save(advertisement);
        return advertisementRepository.findByTitle(advertisement.getTitle());
    }

    public Optional<Advertisement> getAdvertisementById(int id) {

        return advertisementRepository.findById(id);

    }

    public Page<Advertisement> getAllAdvertisements(
            AdType type,
            Integer categoryId,
            Integer priceFrom,
            Integer priceTo,
            int page,
            int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return advertisementRepository.findByQuery(type, categoryId, priceFrom, priceTo, pageRequest);
    }
}

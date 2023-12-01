package de.hs.da.hskleinanzeigen.service;

import de.hs.da.hskleinanzeigen.dto.request.RequestAdvertisementDTO;
import de.hs.da.hskleinanzeigen.dto.response.ResponseAdvertisementDTO;
import de.hs.da.hskleinanzeigen.entity.User;
import de.hs.da.hskleinanzeigen.exception.EntityNotFoundException;
import de.hs.da.hskleinanzeigen.exception.IllegalEntityException;
import de.hs.da.hskleinanzeigen.mapper.AdvertisementMapper;
import de.hs.da.hskleinanzeigen.repository.AdvertisementRepository;
import de.hs.da.hskleinanzeigen.entity.Category;
import de.hs.da.hskleinanzeigen.repository.CategoryRepository;
import de.hs.da.hskleinanzeigen.entity.AdType;
import de.hs.da.hskleinanzeigen.entity.Advertisement;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;

@RestController
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class AdvertisementService {
    private final AdvertisementRepository advertisementRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    private final AdvertisementMapper advertisementMapper;

    @Autowired
    public AdvertisementService(AdvertisementRepository advertisementRepository, CategoryRepository categoryRepository, UserRepository userRepository, AdvertisementMapper advertisementMapper) {
        this.advertisementRepository = advertisementRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.advertisementMapper = advertisementMapper;
    }

    public ResponseEntity<ResponseAdvertisementDTO> createAdvertisement(RequestAdvertisementDTO advertisement) {
        if (!checkValueValid(advertisement))
            throw new IllegalEntityException("AdvertisementPayload", advertisement.getTitle());

        Category category = categoryRepository.findById(advertisement.getCategoryId()).orElseThrow(() -> new EntityNotFoundException("Category of Advertisement",advertisement.getCategoryId()));
        User user = userRepository.findById(advertisement.getUserId()).orElseThrow(() -> new EntityNotFoundException("User of Advertisement",advertisement.getUserId()));

        Advertisement createdAdvertisement = advertisementMapper.toEntity(advertisement);
        createdAdvertisement.setCategory(category);
        createdAdvertisement.setUser(user);

        advertisementRepository.save(createdAdvertisement);
        return advertisementRepository.findByTitle(advertisement.getTitle())
                .map(newAdvertisement -> ResponseEntity.created(URI.create("/api/advertisements")).body(advertisementMapper.toResDTO(newAdvertisement)))
                .orElseThrow(() -> new EntityNotFoundException("Advertisement",advertisement.getTitle()));
    }

    private boolean checkValueValid(RequestAdvertisementDTO advertisement) {
        return checkValueValid(advertisement.getType()) && checkValueValid(advertisement.getCategoryId())
                && checkValueValid(advertisement.getUserId()) && checkValueValid(advertisement.getTitle())
                && checkValueValid(advertisement.getDescription()) && checkValueValid(advertisement.getPrice())
                && checkValueValid(advertisement.getLocation());
    }

    private boolean checkValueValid(String value) {
        return value != null && !value.isEmpty();
    }

    private boolean checkValueValid(int value) {
        return value > 0;
    }

    private boolean checkValueValid(AdType type) {
        return type == AdType.OFFER || type == AdType.REQUEST;
    }


    public ResponseEntity<ResponseAdvertisementDTO> getAdvertisementById(int id) {

        return advertisementRepository.findById(id)
                .map(advertisement -> ResponseEntity.ok().body(advertisementMapper.toResDTO(advertisement)))
                .orElseThrow(() -> new EntityNotFoundException("Advertisement",id));
    }

    public Page<ResponseAdvertisementDTO> getAllAdvertisements(
            AdType type,
            Integer categoryId,
            Integer priceFrom,
            Integer priceTo,
            int page,
            int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return advertisementRepository.findByQuery(type, categoryId, priceFrom, priceTo, pageRequest)
                .map(advertisementMapper::toResDTO);
    }
}

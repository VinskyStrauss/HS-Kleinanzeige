package de.hs.da.hskleinanzeigen.controller;

import de.hs.da.hskleinanzeigen.dto.request.RequestAdvertisementDTO;
import de.hs.da.hskleinanzeigen.dto.response.ResponseAdvertisementDTO;
import de.hs.da.hskleinanzeigen.mapper.AdvertisementMapper;
import de.hs.da.hskleinanzeigen.repository.CategoryRepository;
import de.hs.da.hskleinanzeigen.entity.AdType;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import de.hs.da.hskleinanzeigen.service.AdvertisementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@Secured({"ROLE_ADMIN", "ROLE_USER"})
@Tag(name = "Advertisement", description = "Read and set advertisements and their properties")
public class AdvertisementController {
    private final AdvertisementService advertisementService;

    @Autowired
    public AdvertisementController(AdvertisementService advertisementService, CategoryRepository categoryRepository, UserRepository userRepository, AdvertisementMapper advertisementMapper) {
        this.advertisementService = advertisementService;
    }

    @PostMapping(path = "/api/advertisements", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "Create a new advertisement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Advertisement created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Advertisement already exists")})
    public ResponseEntity<ResponseAdvertisementDTO> createAdvertisement(@Parameter(description = "Advertisement details to create a new advertisement") @RequestBody RequestAdvertisementDTO advertisement) {
        return  advertisementService.createAdvertisement(advertisement);
    }

    @GetMapping(path = "/api/advertisements/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "Get a advertisement by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the advertisement"),
            @ApiResponse(responseCode = "404", description = "Advertisement not found")})
    public ResponseEntity<ResponseAdvertisementDTO> getAdvertisementById(@Parameter(description = "To get advertisement by id") @PathVariable int id) {
        return advertisementService.getAdvertisementById(id);
    }

    @GetMapping(path = "/api/advertisements", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "Get all advertisements with pagination")
    @ApiResponse(responseCode = "200", description = "Found the advertisements")
    public Page<ResponseAdvertisementDTO> getAllAdvertisements(
            @Parameter(description = "get advertisements by query")
            @RequestParam(name = "type", required = false) AdType type,
            @RequestParam(name = "categoryId", required = false) Integer categoryId,
            @RequestParam(name = "priceFrom", required = false, defaultValue = "0") Integer priceFrom,
            @RequestParam(name = "priceTo", required = false, defaultValue = "2147483647") Integer priceTo,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size
    ) {
        return advertisementService.getAllAdvertisements(type, categoryId, priceFrom, priceTo, page, size);
    }
}

package de.hs.da.hskleinanzeigen.controller;

import de.hs.da.hskleinanzeigen.dto.CategoryPayload;
import de.hs.da.hskleinanzeigen.exception.EntityIntegrityViolationException;
import de.hs.da.hskleinanzeigen.exception.EntityNotFoundException;
import de.hs.da.hskleinanzeigen.repository.CategoryRepository;
import de.hs.da.hskleinanzeigen.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class CategoryController {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @PostMapping(path = "/api/categories", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<Category> createAdvertisement(@RequestBody CategoryPayload category) {
        if(categoryRepository.findByName(category.name()).isPresent())
            throw new EntityIntegrityViolationException("Category",category.name());
        categoryRepository.save(new Category(category.name()));
        return categoryRepository.findByName(category.name())
                .map(newCategory -> ResponseEntity.created(URI.create("/api/categories")).body(newCategory))
                .orElseThrow(() -> new EntityNotFoundException("Category",category.name()));
    }

    @GetMapping(path = "/api/categories/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public Category getCategoryById(@PathVariable int id) {
        return categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Category",id));
    }

    @GetMapping(path = "/api/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}

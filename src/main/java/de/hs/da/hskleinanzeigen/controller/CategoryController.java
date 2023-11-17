package de.hs.da.hskleinanzeigen.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.hs.da.hskleinanzeigen.exception.EntityIntegrityViolationException;
import de.hs.da.hskleinanzeigen.exception.EntityNotFoundException;
import de.hs.da.hskleinanzeigen.exception.IllegalEntityException;
import de.hs.da.hskleinanzeigen.repository.CategoryRepository;
import de.hs.da.hskleinanzeigen.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

class CategoryRequest {
    private String name;

    @JsonCreator
    public CategoryRequest(@JsonProperty("name") String name) {
        if(name == null || name.isEmpty())
            throw new IllegalEntityException("CategoryPayload","?","Name must not be empty");
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
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
    public ResponseEntity<Category> createAdvertisement(@RequestBody CategoryRequest category) {
        if(categoryRepository.findByName(category.getName()).isPresent())
            throw new EntityIntegrityViolationException("Category",category.getName());
        categoryRepository.save(new Category(category.getName()));
        return categoryRepository.findByName(category.getName())
                .map(newCategory -> ResponseEntity.created(URI.create("/api/categories")).body(newCategory))
                .orElseThrow(() -> new EntityNotFoundException("Category",category.getName()));
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

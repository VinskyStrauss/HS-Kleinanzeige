package de.hs.da.hskleinanzeigen;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

class CategoryRequest {
    private String name;

    @JsonCreator
    public CategoryRequest(@JsonProperty("name") String name) {
        if(name == null || name.isEmpty())
            throw new IllegalArgumentException("Invalid name for Category");
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
@RestController
public class CategoryController {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @PostMapping(path = "/api/categories", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    @Transactional
    public int createAdvertisement(@RequestBody CategoryRequest category) {
        if(categoryRepository.findByName(category.getName()) != null)
            throw new DataIntegrityViolationException("Category already exists");
        categoryRepository.saveAndFlush(new Category(category.getName()));
        return categoryRepository.findByName(category.getName()).getId();
    }

    @GetMapping(path = "/api/categories/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Category getCategoryById(@PathVariable int id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NullPointerException("Category not found"));
    }

    @GetMapping(path = "/api/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}

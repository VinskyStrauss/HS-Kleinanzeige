package de.hs.da.hskleinanzeigen.service;

import de.hs.da.hskleinanzeigen.entity.Category;
import de.hs.da.hskleinanzeigen.exception.EntityIntegrityViolationException;
import de.hs.da.hskleinanzeigen.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class CategoryService {
    private final CategoryRepository categoryRepository;
    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Optional<Category> createCategory(Category category) {
        if(categoryRepository.findByName(category.getName()).isPresent())
            throw new EntityIntegrityViolationException("Category",category.getName());
        categoryRepository.save(category);
        return categoryRepository.findByName(category.getName());
    }

    public Optional<Category> getCategoryById(int id) {
        return categoryRepository.findById(id);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}

package de.hs.da.hskleinanzeigen.service;

import de.hs.da.hskleinanzeigen.dto.CategoryDTO;
import de.hs.da.hskleinanzeigen.exception.EntityIntegrityViolationException;
import de.hs.da.hskleinanzeigen.exception.EntityNotFoundException;
import de.hs.da.hskleinanzeigen.exception.IllegalEntityException;
import de.hs.da.hskleinanzeigen.mapper.CategoryMapper;
import de.hs.da.hskleinanzeigen.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class CategoryService {
    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public ResponseEntity<CategoryDTO> createAdvertisement(CategoryDTO categoryDTO) {
        if (categoryDTO.getName() == null || categoryDTO.getName().isEmpty())
            throw new IllegalEntityException("CategoryPayload", "?", "Name must not be empty");
        if(categoryRepository.findByName(categoryDTO.getName()).isPresent())
            throw new EntityIntegrityViolationException("Category",categoryDTO.getName());
        categoryRepository.save(categoryMapper.toEntity(categoryDTO));
        return categoryRepository.findByName(categoryDTO.getName())
                .map(newCategory -> ResponseEntity.created(URI.create("/api/categories")).body(categoryMapper.toDTO(newCategory)))
                .orElseThrow(() -> new EntityNotFoundException("Category",categoryDTO.getName()));
    }

    public CategoryDTO getCategoryById(int id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Category",id));
    }

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDTO)
                .toList();
    }
}

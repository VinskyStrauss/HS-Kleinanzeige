package de.hs.da.hskleinanzeigen.unit.service;

import de.hs.da.hskleinanzeigen.entity.AdType;
import de.hs.da.hskleinanzeigen.entity.Advertisement;
import de.hs.da.hskleinanzeigen.entity.Category;
import de.hs.da.hskleinanzeigen.entity.User;
import de.hs.da.hskleinanzeigen.exception.EntityIntegrityViolationException;
import de.hs.da.hskleinanzeigen.exception.EntityNotFoundException;
import de.hs.da.hskleinanzeigen.repository.AdvertisementRepository;
import de.hs.da.hskleinanzeigen.repository.CategoryRepository;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import de.hs.da.hskleinanzeigen.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest  {
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private final Category selectedCategory = new Category();

    @BeforeEach
    public void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        categoryService = new CategoryService(categoryRepository);
    }

    @BeforeEach
    public void setUpObject(){
        selectedCategory.setId(123);
        selectedCategory.setName("selectedCategory");
    }

    private void setUpFindCategoryByNameStub(){
        when(categoryRepository.findByName(anyString())).thenAnswer(invocation -> {
            String categoryName = invocation.getArgument(0);
            if (categoryName.equals("selectedCategory")) {
                return Optional.empty();
            }
            return Optional.of("invalid");
        }).thenReturn(Optional.of(selectedCategory));
    }

    private void setUpFindByCategoryIdStub(){
        when(categoryRepository.findById(anyInt())).thenAnswer(invocation -> {
            int categoryID = invocation.getArgument(0);
            if (categoryID == 123) {
                return Optional.of(selectedCategory);
            }
            throw new EntityNotFoundException("Category of Advertisement",categoryID);
        });
    }

    @Test
    public void testCreateCategory_NameExist() {
        setUpFindCategoryByNameStub();
        Category invalidCategory = new Category();
        invalidCategory.setName("invalidName");
        assertThrows(EntityIntegrityViolationException.class,() -> categoryService.createCategory(invalidCategory));
    }

    @Test
    public void testCreateCategory_CategoryCreated() {
        setUpFindCategoryByNameStub();
        when(categoryRepository.save(selectedCategory)).thenReturn(selectedCategory);

        Optional<Category> result = categoryService.createCategory(selectedCategory);
        verify(categoryRepository).save(selectedCategory);
        assertEquals(result, Optional.of(selectedCategory));
    }

    @Test
    public void getCategoryById_CategoryNotFound() {
        setUpFindByCategoryIdStub();
        assertThrows(EntityNotFoundException.class,() -> categoryService.getCategoryById(100));
    }

    @Test
    public void getCategoryById_CategoryFound() {
        setUpFindByCategoryIdStub();
        assertEquals(categoryService.getCategoryById(123), Optional.of(selectedCategory));
    }

    @Test
    public void getAllCategories() {
        List<Category> allCategories = new ArrayList<>();
        allCategories.add(selectedCategory);
        when(categoryRepository.findAll()).thenReturn(allCategories);

        assertEquals(categoryService.getAllCategories(), allCategories);
    }

}

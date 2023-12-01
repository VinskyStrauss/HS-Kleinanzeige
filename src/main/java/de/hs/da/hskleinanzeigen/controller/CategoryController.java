package de.hs.da.hskleinanzeigen.controller;

import de.hs.da.hskleinanzeigen.dto.CategoryDTO;
import de.hs.da.hskleinanzeigen.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Secured({"ROLE_ADMIN", "ROLE_USER"})
@Tag(name = "Category", description = "Read and set categories and their properties")
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping(path = "/api/categories", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "Create a new category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Category already exists")})
    public ResponseEntity<CategoryDTO> createAdvertisement(@Parameter(description = "Category details to create a new category") @RequestBody CategoryDTO categoryDTO) {
        return categoryService.createAdvertisement(categoryDTO);
    }

    @GetMapping(path = "/api/categories/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "Get category by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the category"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
    })
    public CategoryDTO getCategoryById(@Parameter(description = "To get category by id") @PathVariable int id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping(path = "/api/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "Get all categories")
    @ApiResponse(responseCode = "200", description = "Found the categories")
    @Parameter(description = "get all categories ")
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategories();
    }
}

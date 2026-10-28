package work.onlinebookshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import work.onlinebookshop.dto.book.BookDto;
import work.onlinebookshop.dto.category.CategoryDto;
import work.onlinebookshop.dto.category.CreateCategoryRequestDto;
import work.onlinebookshop.service.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Tag(name = "Categories", description = "Manage book categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get categories with pagination and sorting")
    public Page<CategoryDto> findAll(@ParameterObject Pageable pageable) {
        return categoryService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get a category by id")
    public CategoryDto getById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @GetMapping("/{id}/books")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get books in a category with pagination and sorting")
    public Page<BookDto> getBooks(@PathVariable Long id, @ParameterObject Pageable pageable) {
        return categoryService.getBooks(id, pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a category")
    public CategoryDto create(@Valid @RequestBody CreateCategoryRequestDto request) {
        return categoryService.save(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a category")
    public CategoryDto update(@PathVariable Long id,
            @Valid @RequestBody CreateCategoryRequestDto request) {
        return categoryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a category")
    public void delete(@PathVariable Long id) {
        categoryService.deleteById(id);
    }
}

package work.onlinebookshop.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import work.onlinebookshop.dto.category.CategoryDto;

public interface CategoryService {
    Page<CategoryDto> findAll(Pageable pageable);

    CategoryDto getById(Long id);

    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(Long id, CategoryDto categoryDto);

    void deleteById(Long id);
}

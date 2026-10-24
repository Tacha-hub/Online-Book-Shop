package work.onlinebookshop.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import work.onlinebookshop.dto.book.BookDto;
import work.onlinebookshop.dto.category.CategoryDto;
import work.onlinebookshop.dto.category.CreateCategoryRequestDto;

public interface CategoryService {
    Page<CategoryDto> findAll(Pageable pageable);

    CategoryDto getById(Long id);

    CategoryDto save(CreateCategoryRequestDto request);

    CategoryDto update(Long id, CreateCategoryRequestDto request);

    void deleteById(Long id);

    Page<BookDto> getBooks(Long id, Pageable pageable);
}

package work.onlinebookshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.onlinebookshop.dto.book.BookDto;
import work.onlinebookshop.dto.category.CategoryDto;
import work.onlinebookshop.dto.category.CreateCategoryRequestDto;
import work.onlinebookshop.exception.EntityNotFoundException;
import work.onlinebookshop.mapper.BookMapper;
import work.onlinebookshop.mapper.CategoryMapper;
import work.onlinebookshop.model.Category;
import work.onlinebookshop.repository.CategoryRepository;
import work.onlinebookshop.repository.book.BookRepository;
import work.onlinebookshop.service.CategoryService;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final CategoryMapper categoryMapper;
    private final BookMapper bookMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(categoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(Long id) {
        return categoryMapper.toDto(findCategory(id));
    }

    @Override
    public CategoryDto save(CreateCategoryRequestDto request) {
        return categoryMapper.toDto(categoryRepository.save(categoryMapper.toEntity(request)));
    }

    @Override
    public CategoryDto update(Long id, CreateCategoryRequestDto request) {
        Category category = findCategory(id);
        categoryMapper.updateFromDto(request, category);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.delete(findCategory(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookDto> getBooks(Long id, Pageable pageable) {
        findCategory(id);
        return bookRepository.findAllByCategoriesId(id, pageable).map(bookMapper::toDto);
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Category not found by id: " + id));
    }
}

package work.onlinebookshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import work.onlinebookshop.dto.book.BookDto;
import work.onlinebookshop.dto.book.BookSearchParameterDto;
import work.onlinebookshop.dto.book.CreateBookRequestDto;
import work.onlinebookshop.exception.EntityNotFoundException;
import work.onlinebookshop.mapper.BookMapper;
import work.onlinebookshop.model.Book;
import work.onlinebookshop.repository.book.BookRepository;
import work.onlinebookshop.repository.book.BookSpecificationBuilder;
import work.onlinebookshop.service.BookService;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto bookDto) {
        Book book = bookMapper.toEntity(bookDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public Page<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).map(bookMapper::toDto);
    }

    @Override
    public BookDto getById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Book not found by id: " + id));
        return bookMapper.toDto(book);
    }

    @Override
    public BookDto update(Long id, CreateBookRequestDto bookDto) {
        Book book = findBookById(id);
        bookMapper.updateBookFromDto(bookDto, book);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public void deleteById(Long id) {
        Book book = findBookById(id);
        bookRepository.delete(book);
    }

    private Book findBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Book not found by id: " + id));
    }

    @Override
    public Page<BookDto> search(BookSearchParameterDto searchParam, Pageable pageable) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParam);
        return bookRepository.findAll(bookSpecification, pageable).map(bookMapper::toDto);
    }
}

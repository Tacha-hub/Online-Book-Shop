package work.onlinebookshop.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import work.onlinebookshop.dto.book.BookDto;
import work.onlinebookshop.dto.book.BookSearchParameterDto;
import work.onlinebookshop.dto.book.CreateBookRequestDto;

public interface BookService {
    BookDto save(CreateBookRequestDto bookDto);

    Page<BookDto> findAll(Pageable pageable);

    BookDto getById(Long id);

    BookDto update(Long id, CreateBookRequestDto bookDto);

    void deleteById(Long id);

    Page<BookDto> search(BookSearchParameterDto searchParams, Pageable pageable);
}

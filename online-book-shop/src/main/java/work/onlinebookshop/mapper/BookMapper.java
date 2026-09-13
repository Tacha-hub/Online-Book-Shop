package work.onlinebookshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import work.onlinebookshop.config.MapperConfig;
import work.onlinebookshop.dto.book.BookDto;
import work.onlinebookshop.dto.book.CreateBookRequestDto;
import work.onlinebookshop.model.Book;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Book toEntity(CreateBookRequestDto bookDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateBookFromDto(CreateBookRequestDto bookDto,
                           @MappingTarget Book book);
}

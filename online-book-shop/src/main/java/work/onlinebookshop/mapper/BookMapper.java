package work.onlinebookshop.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import work.onlinebookshop.config.MapperConfig;
import work.onlinebookshop.dto.book.BookDto;
import work.onlinebookshop.dto.book.CreateBookRequestDto;
import work.onlinebookshop.model.Book;
import work.onlinebookshop.model.Category;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    @Mapping(target = "categoryIds", source = "categories", qualifiedByName = "categoryIds")
    BookDto toDto(Book book);

    @Named("categoryIds")
    default Set<Long> categoryIds(Set<Category> categories) {
        return categories.stream().map(Category::getId).collect(Collectors.toSet());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "categories", ignore = true)
    Book toEntity(CreateBookRequestDto bookDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "categories", ignore = true)
    void updateBookFromDto(CreateBookRequestDto bookDto,
                           @MappingTarget Book book);
}

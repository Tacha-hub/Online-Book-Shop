package work.onlinebookshop.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import work.onlinebookshop.config.MapperConfig;
import work.onlinebookshop.dto.book.BookDto;
import work.onlinebookshop.dto.book.CreateBookRequestDto;
import work.onlinebookshop.dto.category.BookDtoWithoutCategoryIds;
import work.onlinebookshop.model.Book;
import work.onlinebookshop.model.Category;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    @Mapping(target = "categoryIds", ignore = true)
    BookDto toDto(Book book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "categories", source = "categoryIds")
    Book toEntity(CreateBookRequestDto bookDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "categories", source = "categoryIds")
    void updateBookFromDto(CreateBookRequestDto bookDto,
                           @MappingTarget Book book);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        bookDto.setCategoryIds(
                book.getCategories().stream()
                        .map(Category::getId)
                        .toList());
    }

    default Category mapCategoryId(Long id) {
        if (id == null) {
            return null;
        }

        Category category = new Category();
        category.setId(id);
        return category;
    }
}

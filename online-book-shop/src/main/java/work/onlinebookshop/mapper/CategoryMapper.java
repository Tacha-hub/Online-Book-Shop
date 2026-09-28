package work.onlinebookshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import work.onlinebookshop.config.MapperConfig;
import work.onlinebookshop.dto.category.CategoryDto;
import work.onlinebookshop.dto.category.CreateCategoryRequestDto;
import work.onlinebookshop.model.Category;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Category toEntity(CreateCategoryRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateCategoryFromDto(CreateCategoryRequestDto requestDto,
                               @MappingTarget Category category);
}

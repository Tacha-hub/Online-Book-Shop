package work.onlinebookshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import work.onlinebookshop.config.MapperConfig;
import work.onlinebookshop.dto.user.UserRegistrationRequestDto;
import work.onlinebookshop.dto.user.UserResponseDto;
import work.onlinebookshop.model.User;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    User toEntity(UserRegistrationRequestDto requestDto);
}

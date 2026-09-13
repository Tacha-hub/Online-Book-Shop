package work.onlinebookshop.service;

import work.onlinebookshop.dto.user.UserRegistrationRequestDto;
import work.onlinebookshop.dto.user.UserResponseDto;
import work.onlinebookshop.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;
}

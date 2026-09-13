package work.onlinebookshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.onlinebookshop.dto.user.UserRegistrationRequestDto;
import work.onlinebookshop.dto.user.UserResponseDto;
import work.onlinebookshop.exception.RegistrationException;
import work.onlinebookshop.mapper.UserMapper;
import work.onlinebookshop.model.User;
import work.onlinebookshop.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("Cannot register user " + requestDto.getEmail());
        }
        User user = userMapper.toEntity(requestDto);
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponse(savedUser);
    }
}

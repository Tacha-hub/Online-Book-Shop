package work.onlinebookshop.service;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.onlinebookshop.dto.user.UserRegistrationRequestDto;
import work.onlinebookshop.dto.user.UserResponseDto;
import work.onlinebookshop.exception.EntityNotFoundException;
import work.onlinebookshop.exception.RegistrationException;
import work.onlinebookshop.mapper.UserMapper;
import work.onlinebookshop.model.Role;
import work.onlinebookshop.model.Role.RoleName;
import work.onlinebookshop.model.User;
import work.onlinebookshop.repository.RoleRepository;
import work.onlinebookshop.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("Cannot register user " + requestDto.getEmail());
        }
        User user = userMapper.toEntity(requestDto);
        Role roleUser = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new EntityNotFoundException("Default user role is missing"));
        user.setRoles(Set.of(roleUser));
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setEmail(requestDto.getEmail());
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponse(savedUser);
    }
}

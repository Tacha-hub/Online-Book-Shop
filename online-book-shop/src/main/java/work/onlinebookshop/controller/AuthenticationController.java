package work.onlinebookshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.onlinebookshop.dto.user.UserLoginRequestDto;
import work.onlinebookshop.dto.user.UserLoginResponseDto;
import work.onlinebookshop.dto.user.UserRegistrationRequestDto;
import work.onlinebookshop.dto.user.UserResponseDto;
import work.onlinebookshop.exception.RegistrationException;
import work.onlinebookshop.security.AuthenticationService;
import work.onlinebookshop.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User registration")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "Register a new user",
            description = "Creates a user with a unique email address")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid registration data"),
            @ApiResponse(responseCode = "409", description = "Email is already registered")
            })

    @PostMapping("/registration")
    public UserResponseDto register(@Valid @RequestBody UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @Operation(summary = "User login",
            description = "Authenticates a user by email and password and returns a JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "Invalid email or password")})

    @PostMapping("/login")
    public UserLoginResponseDto login(@Valid @RequestBody UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}

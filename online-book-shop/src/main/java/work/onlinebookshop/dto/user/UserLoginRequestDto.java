package work.onlinebookshop.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record UserLoginRequestDto(
        @NotEmpty
        @Email
        String email,

        @NotEmpty
        String password) {
}

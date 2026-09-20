package work.onlinebookshop.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import work.onlinebookshop.validation.FieldMatch;

@Data
@FieldMatch(field = "password", fieldMatch = "repeatPassword")
public class UserRegistrationRequestDto {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Length(min = 8, max = 35)
    private String password;
    @NotBlank
    @Length(min = 8, max = 35)
    private String repeatPassword;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String shippingAddress;
}

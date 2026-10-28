package work.onlinebookshop.dto.book;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Data
public class CreateBookRequestDto {

    @NotBlank(message = "Title must not be blank")
    @Size(max = 255)
    private String title;

    @NotBlank(message = "Author must not be blank")
    @Size(max = 255)
    private String author;

    @NotBlank(message = "ISBN must not be blank")
    @Size(max = 255)
    private String isbn;

    @NotNull(message = "Price is necessary")
    @Positive(message = "Price must be greater than zero")
    @Digits(integer = 36, fraction = 2)
    private BigDecimal price;
    @Size(max = 255)
    private String description;
    @Size(max = 255)
    private String coverImage;

    @NotEmpty
    private Set<@NotNull @Positive Long> categoryIds = new HashSet<>();
}

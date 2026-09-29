package work.onlinebookshop.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCategoryRequestDto {
    @NotBlank(message = "Category name must not be blank")
    private String name;
    private String description;
}

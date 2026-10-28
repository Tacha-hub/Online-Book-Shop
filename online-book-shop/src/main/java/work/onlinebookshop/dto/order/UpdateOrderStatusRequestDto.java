package work.onlinebookshop.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import work.onlinebookshop.model.Order;

@Data
public class UpdateOrderStatusRequestDto {
    @NotNull
    private Order.Status status;
}

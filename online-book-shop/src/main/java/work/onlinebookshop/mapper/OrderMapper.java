package work.onlinebookshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import work.onlinebookshop.config.MapperConfig;
import work.onlinebookshop.dto.order.OrderDto;
import work.onlinebookshop.model.Order;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    OrderDto toDto(Order order);
}

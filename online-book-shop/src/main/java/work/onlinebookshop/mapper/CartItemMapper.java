package work.onlinebookshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import work.onlinebookshop.config.MapperConfig;
import work.onlinebookshop.dto.cart.CartItemDto;
import work.onlinebookshop.model.CartItem;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    CartItemDto toDto(CartItem cartItem);
}

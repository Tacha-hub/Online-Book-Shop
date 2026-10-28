package work.onlinebookshop.mapper;

import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import work.onlinebookshop.config.MapperConfig;
import work.onlinebookshop.dto.cart.ShoppingCartDto;
import work.onlinebookshop.model.CartItem;
import work.onlinebookshop.model.ShoppingCart;

@Mapper(config = MapperConfig.class, uses = CartItemMapper.class)
public interface ShoppingCartMapper {
    @Mapping(target = "userId", source = "user.id")
    ShoppingCartDto toDto(ShoppingCart cart);

    @Mapping(target = "id", source = "cart.id")
    @Mapping(target = "userId", source = "cart.user.id")
    @Mapping(target = "cartItems", source = "items")
    ShoppingCartDto toDto(ShoppingCart cart, Set<CartItem> items);
}

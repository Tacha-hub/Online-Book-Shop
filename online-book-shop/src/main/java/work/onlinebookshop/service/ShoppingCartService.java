package work.onlinebookshop.service;

import org.springframework.data.domain.Pageable;
import work.onlinebookshop.dto.cart.AddCartItemRequestDto;
import work.onlinebookshop.dto.cart.ShoppingCartDto;
import work.onlinebookshop.dto.cart.UpdateCartItemRequestDto;
import work.onlinebookshop.model.User;

public interface ShoppingCartService {
    ShoppingCartDto getCart(Long userId, Pageable pageable);

    ShoppingCartDto addBook(Long userId, AddCartItemRequestDto request);

    ShoppingCartDto updateQuantity(Long userId, Long itemId, UpdateCartItemRequestDto request);

    void removeBook(Long userId, Long itemId);

    void createCart(User user);
}

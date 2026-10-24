package work.onlinebookshop.service.impl;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import work.onlinebookshop.dto.cart.AddCartItemRequestDto;
import work.onlinebookshop.dto.cart.ShoppingCartDto;
import work.onlinebookshop.dto.cart.UpdateCartItemRequestDto;
import work.onlinebookshop.exception.EntityNotFoundException;
import work.onlinebookshop.mapper.CartItemMapper;
import work.onlinebookshop.model.Book;
import work.onlinebookshop.model.CartItem;
import work.onlinebookshop.model.ShoppingCart;
import work.onlinebookshop.repository.CartItemRepository;
import work.onlinebookshop.repository.ShoppingCartRepository;
import work.onlinebookshop.repository.book.BookRepository;
import work.onlinebookshop.service.ShoppingCartService;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartDto getCart(Long userId, Pageable pageable) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found"));
        return toDto(cart, pageable);
    }

    @Override
    public ShoppingCartDto addBook(Long userId, AddCartItemRequestDto request) {
        ShoppingCart cart = getCartForUpdate(userId);
        Book book = bookRepository.findById(request.getBookId()).orElseThrow(() ->
                new EntityNotFoundException("Book not found by id: " + request.getBookId()));
        CartItem item = cartItemRepository.findByShoppingCartIdAndBookId(
                cart.getId(), book.getId()).orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setShoppingCart(cart);
                    newItem.setBook(book);
                    return newItem;
                });
        long quantity = (long) item.getQuantity() + request.getQuantity();
        if (quantity > Integer.MAX_VALUE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity is too large");
        }
        item.setQuantity((int) quantity);
        cartItemRepository.save(item);
        return toDto(cart, Pageable.unpaged());
    }

    @Override
    public ShoppingCartDto updateQuantity(Long userId, Long itemId,
            UpdateCartItemRequestDto request) {
        ShoppingCart cart = getCartForUpdate(userId);
        CartItem item = getItem(cart.getId(), itemId);
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);
        return toDto(cart, Pageable.unpaged());
    }

    @Override
    public void removeBook(Long userId, Long itemId) {
        ShoppingCart cart = getCartForUpdate(userId);
        cartItemRepository.delete(getItem(cart.getId(), itemId));
    }

    private ShoppingCart getCartForUpdate(Long userId) {
        return shoppingCartRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found"));
    }

    private CartItem getItem(Long cartId, Long itemId) {
        return cartItemRepository.findByIdAndShoppingCartId(itemId, cartId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cart item not found by id: " + itemId));
    }

    private ShoppingCartDto toDto(ShoppingCart cart, Pageable pageable) {
        ShoppingCartDto dto = new ShoppingCartDto();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUser().getId());
        dto.setCartItems(cartItemRepository.findAllByShoppingCartId(cart.getId(), pageable)
                .stream().map(cartItemMapper::toDto)
                .collect(Collectors.toCollection(LinkedHashSet::new)));
        return dto;
    }
}

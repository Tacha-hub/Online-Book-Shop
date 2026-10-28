package work.onlinebookshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import work.onlinebookshop.dto.cart.AddCartItemRequestDto;
import work.onlinebookshop.dto.cart.ShoppingCartDto;
import work.onlinebookshop.dto.cart.UpdateCartItemRequestDto;
import work.onlinebookshop.model.User;
import work.onlinebookshop.service.ShoppingCartService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
@PreAuthorize("hasRole('USER')")
@Tag(name = "Shopping cart", description = "Manage the current user's shopping cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    @Operation(summary = "Get your shopping cart",
            description = "Supports page, size and sort for cart items")
    public ShoppingCartDto getCart(@AuthenticationPrincipal User user,
            @ParameterObject Pageable pageable) {
        return shoppingCartService.getCart(user.getId(), pageable);
    }

    @PostMapping
    @Operation(summary = "Add a book to your cart")
    public ShoppingCartDto addBook(@AuthenticationPrincipal User user,
            @Valid @RequestBody AddCartItemRequestDto request) {
        return shoppingCartService.addBook(user.getId(), request);
    }

    @PutMapping("/cart-items/{cartItemId}")
    @Operation(summary = "Update a book's quantity in your cart")
    public ShoppingCartDto updateQuantity(@AuthenticationPrincipal User user,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequestDto request) {
        return shoppingCartService.updateQuantity(user.getId(), cartItemId, request);
    }

    @DeleteMapping("/cart-items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a book from your cart")
    public void removeBook(@AuthenticationPrincipal User user, @PathVariable Long cartItemId) {
        shoppingCartService.removeBook(user.getId(), cartItemId);
    }
}

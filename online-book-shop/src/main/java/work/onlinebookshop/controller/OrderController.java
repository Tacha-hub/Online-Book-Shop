package work.onlinebookshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import work.onlinebookshop.dto.order.CreateOrderRequestDto;
import work.onlinebookshop.dto.order.OrderDto;
import work.onlinebookshop.dto.order.OrderItemDto;
import work.onlinebookshop.dto.order.UpdateOrderStatusRequestDto;
import work.onlinebookshop.model.User;
import work.onlinebookshop.service.OrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Manage orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Place an order from your shopping cart")
    public OrderDto placeOrder(@AuthenticationPrincipal User user,
            @Valid @RequestBody CreateOrderRequestDto request) {
        return orderService.placeOrder(user.getId(), request);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get your order history with pagination and sorting")
    public Page<OrderDto> getOrders(@AuthenticationPrincipal User user,
            @ParameterObject Pageable pageable) {
        return orderService.getOrders(user.getId(), pageable);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an order's status")
    public OrderDto updateStatus(@PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequestDto request) {
        return orderService.updateStatus(id, request);
    }

    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get your order's items with pagination and sorting")
    public Page<OrderItemDto> getItems(@AuthenticationPrincipal User user,
            @PathVariable Long orderId, @ParameterObject Pageable pageable) {
        return orderService.getItems(user.getId(), orderId, pageable);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get an item in your order")
    public OrderItemDto getItem(@AuthenticationPrincipal User user,
            @PathVariable Long orderId, @PathVariable Long itemId) {
        return orderService.getItem(user.getId(), orderId, itemId);
    }
}

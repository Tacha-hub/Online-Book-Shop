package work.onlinebookshop.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import work.onlinebookshop.dto.order.CreateOrderRequestDto;
import work.onlinebookshop.dto.order.OrderDto;
import work.onlinebookshop.dto.order.OrderItemDto;
import work.onlinebookshop.dto.order.UpdateOrderStatusRequestDto;

public interface OrderService {
    OrderDto placeOrder(Long userId, CreateOrderRequestDto request);

    Page<OrderDto> getOrders(Long userId, Pageable pageable);

    OrderDto updateStatus(Long orderId, UpdateOrderStatusRequestDto request);

    Page<OrderItemDto> getItems(Long userId, Long orderId, Pageable pageable);

    OrderItemDto getItem(Long userId, Long orderId, Long itemId);
}

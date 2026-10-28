package work.onlinebookshop.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import work.onlinebookshop.dto.order.CreateOrderRequestDto;
import work.onlinebookshop.dto.order.OrderDto;
import work.onlinebookshop.dto.order.OrderItemDto;
import work.onlinebookshop.dto.order.UpdateOrderStatusRequestDto;
import work.onlinebookshop.exception.EntityNotFoundException;
import work.onlinebookshop.mapper.OrderItemMapper;
import work.onlinebookshop.mapper.OrderMapper;
import work.onlinebookshop.model.CartItem;
import work.onlinebookshop.model.Order;
import work.onlinebookshop.model.OrderItem;
import work.onlinebookshop.model.ShoppingCart;
import work.onlinebookshop.repository.OrderItemRepository;
import work.onlinebookshop.repository.OrderRepository;
import work.onlinebookshop.repository.ShoppingCartRepository;
import work.onlinebookshop.service.OrderService;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderDto placeOrder(Long userId, CreateOrderRequestDto request) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart not found: " + userId));
        if (cart.getCartItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shopping cart is empty");
        }
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(Order.Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(request.getShippingAddress());
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setBook(cartItem.getBook());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getBook().getPrice());
            order.getOrderItems().add(item);
            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        order.setTotal(total);
        Order savedOrder = orderRepository.save(order);
        cart.getCartItems().clear();
        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getOrders(Long userId, Pageable pageable) {
        return orderRepository.findAllByUserId(userId, pageable).map(orderMapper::toDto);
    }

    @Override
    public OrderDto updateStatus(Long orderId, UpdateOrderStatusRequestDto request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found by id: " + orderId));
        order.setStatus(request.getStatus());
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemDto> getItems(Long userId, Long orderId, Pageable pageable) {
        getOrder(userId, orderId);
        return orderItemRepository.findAllByOrderId(orderId, pageable).map(orderItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderItemDto getItem(Long userId, Long orderId, Long itemId) {
        getOrder(userId, orderId);
        OrderItem item = orderItemRepository.findByIdAndOrderId(itemId, orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order item not found by id: " + itemId));
        return orderItemMapper.toDto(item);
    }

    private Order getOrder(Long userId, Long orderId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found by id: " + orderId));
    }
}

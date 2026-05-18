package com.shopee.shopee_backend.service.impl;

import com.shopee.shopee_backend.dto.CreateOrderRequestDto;
import com.shopee.shopee_backend.dto.OrderDto;
import com.shopee.shopee_backend.dto.OrderItemDto;
import com.shopee.shopee_backend.dto.OrderItemRequestDto;
import com.shopee.shopee_backend.dto.UpdateOrderStatusRequestDto;
import com.shopee.shopee_backend.entity.Franchise;
import com.shopee.shopee_backend.entity.Order;
import com.shopee.shopee_backend.entity.OrderItem;
import com.shopee.shopee_backend.entity.OrderStatus;
import com.shopee.shopee_backend.entity.Product;
import com.shopee.shopee_backend.exception.ApiException;
import com.shopee.shopee_backend.exception.ResourceNotFoundException;
import com.shopee.shopee_backend.repository.FranchiseRepository;
import com.shopee.shopee_backend.repository.OrderRepository;
import com.shopee.shopee_backend.repository.ProductRepository;
import com.shopee.shopee_backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final FranchiseRepository franchiseRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByFranchise(Long franchiseId) {
        return orderRepository.findAllByFranchiseFranchiseIdOrderByCreatedAtDesc(franchiseId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrder(Long franchiseId, Long orderId) {
        Order order = findOrderInFranchise(franchiseId, orderId);
        return toDto(order);
    }

    @Override
    @Transactional
    public OrderDto createOrder(Long franchiseId, CreateOrderRequestDto request) {
        Franchise franchise = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new ResourceNotFoundException("Franchise not found: " + franchiseId));

        Order order = new Order();
        order.setFranchise(franchise);
        order.setCustomerName(request.getCustomerName());
        order.setCustomerMobile(request.getCustomerMobile());
        order.setNotes(request.getNotes());
        order.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "CASH");
        order.setDiscountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO);
        order.setPaid(request.isPaid());

        List<OrderItem> items = new ArrayList<>();
        BigDecimal subtotalSum = BigDecimal.ZERO;

        for (OrderItemRequestDto itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemReq.getProductId()));

            if (!product.getFranchise().getFranchiseId().equals(franchiseId)) {
                throw new ApiException("Product " + itemReq.getProductId()
                        + " does not belong to franchise " + franchiseId, HttpStatus.BAD_REQUEST);
            }
            if (!product.isActive()) {
                throw new ApiException("Product '" + product.getName() + "' is not available", HttpStatus.BAD_REQUEST);
            }
            if (product.getStockQuantity() < itemReq.getQuantity()) {
                throw new ApiException("Insufficient stock for product '" + product.getName()
                        + "'. Available: " + product.getStockQuantity(), HttpStatus.CONFLICT);
            }

            // Reduce stock
            product.setStockQuantity(product.getStockQuantity() - itemReq.getQuantity());
            productRepository.save(product);

            BigDecimal unitPrice = product.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(unitPrice);
            item.setSubtotal(subtotal);
            items.add(item);
            subtotalSum = subtotalSum.add(subtotal);
        }

        BigDecimal discount = order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal total = subtotalSum.subtract(discount);
        if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;

        order.setItems(items);
        order.setTotalAmount(total);

        return toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(Long franchiseId, Long orderId, UpdateOrderStatusRequestDto request) {
        Order order = findOrderInFranchise(franchiseId, orderId);

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new ApiException("Cannot update a cancelled order", HttpStatus.BAD_REQUEST);
        }

        order.setStatus(request.getStatus());
        if (request.getPaid() != null) {
            order.setPaid(request.getPaid());
        }
        order.setUpdatedAt(LocalDateTime.now());
        return toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto cancelOrder(Long franchiseId, Long orderId) {
        Order order = findOrderInFranchise(franchiseId, orderId);

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new ApiException("Cannot cancel a delivered order", HttpStatus.BAD_REQUEST);
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new ApiException("Order is already cancelled", HttpStatus.BAD_REQUEST);
        }

        // Restore stock
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        return toDto(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByStatus(Long franchiseId, OrderStatus status) {
        return orderRepository
                .findAllByFranchiseFranchiseIdAndStatusOrderByCreatedAtDesc(franchiseId, status)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // ---- helpers ----

    private Order findOrderInFranchise(Long franchiseId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        if (!order.getFranchise().getFranchiseId().equals(franchiseId)) {
            throw new ApiException("Order does not belong to franchise " + franchiseId, HttpStatus.FORBIDDEN);
        }
        return order;
    }

    private OrderDto toDto(Order o) {
        List<OrderItemDto> itemDtos = o.getItems().stream()
                .map(i -> new OrderItemDto(
                        i.getOrderItemId(),
                        i.getProduct().getProductId(),
                        i.getProduct().getName(),
                        i.getProduct().getSku(),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getSubtotal()))
                .collect(Collectors.toList());

        return new OrderDto(
                o.getOrderId(),
                o.getFranchise().getFranchiseId(),
                o.getFranchise().getOutletName(),
                o.getCustomerName(),
                o.getCustomerMobile(),
                o.getStatus().name(),
                o.getNotes(),
                o.getTotalAmount(),
                o.getDiscountAmount(),
                o.getPaymentMethod(),
                o.isPaid(),
                itemDtos,
                o.getCreatedAt(),
                o.getUpdatedAt());
    }
}

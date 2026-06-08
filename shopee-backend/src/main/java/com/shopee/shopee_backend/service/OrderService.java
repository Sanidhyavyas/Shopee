package com.shopee.shopee_backend.service;

import com.shopee.shopee_backend.dto.CreateOrderRequestDto;
import com.shopee.shopee_backend.dto.OrderDto;
import com.shopee.shopee_backend.dto.UpdateOrderStatusRequestDto;
import com.shopee.shopee_backend.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    Page<OrderDto> getOrdersByFranchise(Long franchiseId, Pageable pageable);

    OrderDto getOrder(Long franchiseId, Long orderId);

    OrderDto createOrder(Long franchiseId, CreateOrderRequestDto request);

    OrderDto updateOrderStatus(Long franchiseId, Long orderId, UpdateOrderStatusRequestDto request);

    OrderDto cancelOrder(Long franchiseId, Long orderId);

    Page<OrderDto> getOrdersByStatus(Long franchiseId, OrderStatus status, Pageable pageable);
}

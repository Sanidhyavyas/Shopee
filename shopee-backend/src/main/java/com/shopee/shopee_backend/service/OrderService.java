package com.shopee.shopee_backend.service;

import com.shopee.shopee_backend.dto.CreateOrderRequestDto;
import com.shopee.shopee_backend.dto.OrderDto;
import com.shopee.shopee_backend.dto.UpdateOrderStatusRequestDto;
import com.shopee.shopee_backend.entity.OrderStatus;

import java.util.List;

public interface OrderService {

    List<OrderDto> getOrdersByFranchise(Long franchiseId);

    OrderDto getOrder(Long franchiseId, Long orderId);

    OrderDto createOrder(Long franchiseId, CreateOrderRequestDto request);

    OrderDto updateOrderStatus(Long franchiseId, Long orderId, UpdateOrderStatusRequestDto request);

    OrderDto cancelOrder(Long franchiseId, Long orderId);

    List<OrderDto> getOrdersByStatus(Long franchiseId, OrderStatus status);
}

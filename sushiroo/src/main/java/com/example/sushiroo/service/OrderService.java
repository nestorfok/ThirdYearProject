package com.example.sushiroo.service;

import com.example.sushiroo.model.Order;
import com.example.sushiroo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Optional<Order> getOrderByOrderId(Long orderId) {
        return orderRepository.findById(orderId);
    }
}

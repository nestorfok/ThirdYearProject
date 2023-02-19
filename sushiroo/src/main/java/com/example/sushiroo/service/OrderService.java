package com.example.sushiroo.service;

import com.example.sushiroo.model.Order;
import com.example.sushiroo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    private LocalDateTime localDateTime;

    private final DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");;

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Optional<Order> getOrderByOrderId(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public void setDateTimeToCurrent() {
        localDateTime = LocalDateTime.now();
    }

    public void setFutureDateTime(int day, int hour) {
        localDateTime = localDateTime.plusDays(day);
        localDateTime = localDateTime.plusHours(hour);
    }

    public String getLocalDateTime() {
        return formatter.format(localDateTime);
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }
}

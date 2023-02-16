package com.example.sushiroo.repository;

import com.example.sushiroo.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    //@Query("SELECT o FROM Order o WHERE o.user.id = :userId")
    List<Order> findByUserId(Long userId);

    //Optional<Order> findById(Long id);

}

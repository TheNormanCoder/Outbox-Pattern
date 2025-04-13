package com.example.outbox.domain.repository;

import com.example.outbox.domain.model.Order;

// Repository per gli ordini
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Metodi standard forniti da Spring Data JPA
}


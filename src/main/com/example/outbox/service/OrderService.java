package com.example.outbox.service;

import com.example.outbox.domain.event.OrderCreatedEvent;
import com.example.outbox.domain.model.Order;
import com.example.outbox.domain.repository.OrderRepository;
import com.example.outbox.messagging.EventSerializer;
import com.example.outbox.outbox.model.OutboxEvent;
import com.example.outbox.outbox.repository.OutboxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final EventSerializer eventSerializer;

    @Autowired
    public OrderService(OrderRepository orderRepository, OutboxRepository outboxRepository, EventSerializer eventSerializer) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.eventSerializer = eventSerializer;
    }

    @Transactional
    public Order createOrder(Order order) {
        // 1. Salva l'ordine nel database
        Order savedOrder = orderRepository.save(order);

        // 2. Crea l'evento di dominio
        List<OrderCreatedEvent.OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> new OrderCreatedEvent.OrderItemDto(
                        item.getProductName(),
                        item.getQuantity(),
                        item.getPrice()))
                .collect(Collectors.toList());

        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getCustomerName(),
                savedOrder.getTotalAmount(),
                itemDtos
        );

        // 3. Serializza l'evento
        String payload = eventSerializer.serialize(event);

        // 4. Salva l'evento nella tabella outbox (nella stessa transazione)
        OutboxEvent outboxEvent = new OutboxEvent(
                "com.example.outbox.domain.model.Order",
                savedOrder.getId().toString(),
                "OrderCreated",
                payload
        );

        outboxRepository.save(outboxEvent);

        return savedOrder;
    }

    // Altri metodi per la gestione degli ordini...
}

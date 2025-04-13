package com.example.outbox.domain.event;

// Esempio di evento specifico: com.example.outbox.domain.event.OrderCreatedEvent
public class OrderCreatedEvent extends DomainEvent {
    private final Long orderId;
    private final String customerName;
    private final BigDecimal totalAmount;
    private final List<OrderItemDto> items;

    public OrderCreatedEvent(Long orderId, String customerName, BigDecimal totalAmount, List<OrderItemDto> items) {
        super();
        this.orderId = orderId;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    // Getters
    // ...

    // DTO interno per rappresentare gli elementi dell'ordine
    public static class OrderItemDto {
        private final String productName;
        private final int quantity;
        private final BigDecimal price;

        public OrderItemDto(String productName, int quantity, BigDecimal price) {
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }

        // Getters
        // ...
    }
}
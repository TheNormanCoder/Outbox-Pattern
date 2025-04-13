import com.example.outbox.domain.model.Order;
import com.example.outbox.domain.repository.OrderRepository;
import com.example.outbox.outbox.model.OutboxEvent;
import com.example.outbox.outbox.repository.OutboxRepository;
import com.example.outbox.service.OrderService;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OutboxRepository outboxRepository;

    @Spy
    private EventSerializer eventSerializer = new EventSerializer();

    @InjectMocks
    private OrderService orderService;

    @Test
    public void testCreateOrder_shouldSaveOrderAndOutboxEvent() {
        // Arrange
        Order order = new Order();
        order.setCustomerName("Test Customer");
        order.setTotalAmount(new BigDecimal("100.00"));

        OrderItem item = new OrderItem();
        item.setProductName("Test Product");
        item.setQuantity(2);
        item.setPrice(new BigDecimal("50.00"));
        item.setOrder(order);

        order.getItems().add(item);

        Order savedOrder = new Order();
        BeanUtils.copyProperties(order, savedOrder);
        savedOrder.setId(1L);

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Act
        Order result = orderService.createOrder(order);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Test Customer", result.getCustomerName());

        // Verifica che l'ordine sia stato salvato
        verify(orderRepository).save(order);

        // Verifica che l'evento sia stato salvato nella tabella outbox
        ArgumentCaptor<OutboxEvent> outboxEventCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxRepository).save(outboxEventCaptor.capture());

        OutboxEvent capturedEvent = outboxEventCaptor.getValue();
        assertEquals("com.example.outbox.domain.model.Order", capturedEvent.getAggregateType());
        assertEquals("1", capturedEvent.getAggregateId());
        assertEquals("OrderCreated", capturedEvent.getEventType());
        assertFalse(capturedEvent.isProcessed());

        // Verifica che il payload contenga i dati corretti
        String payload = capturedEvent.getPayload();
        assertTrue(payload.contains("Test Customer"));
        assertTrue(payload.contains("100.00"));
        assertTrue(payload.contains("Test Product"));
    }
}
